package br.com.srm.creditengine.infrastructure.reporting;

import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.reporting.settlement.SettlementReportItem;
import br.com.srm.creditengine.reporting.settlement.SettlementReportPage;
import br.com.srm.creditengine.reporting.settlement.SettlementReportQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcSettlementReportRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private JdbcSettlementReportRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcSettlementReportRepository(jdbcTemplate);
    }

    private void stubEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Long.class)))
                .thenReturn(0L);
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());
    }

    @Test
    void findAll_returnsEmptyPage_whenNoData() {
        stubEmpty();

        SettlementReportPage page = repository.findAll(
                new SettlementReportQuery(null, null, null, null, 0, 20));

        assertEquals(0L, page.totalElements());
        assertEquals(0, page.totalPages());
        assertTrue(page.items().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_buildsSqlWithoutFilters_whenNoFiltersProvided() {
        stubEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        repository.findAll(new SettlementReportQuery(null, null, null, null, 0, 20));

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(SqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains(":from"));
        assertFalse(sql.contains(":toExclusive"));
        assertFalse(sql.contains(":assignorId"));
        assertFalse(sql.contains(":currency"));
        assertTrue(sql.contains("ORDER BY s.created_at DESC"));
        assertTrue(sql.contains("LIMIT :size OFFSET :offset"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_includesFromFilter_whenFromProvided() {
        stubEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        repository.findAll(new SettlementReportQuery(
                LocalDate.of(2026, 6, 1), null, null, null, 0, 20));

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(SqlParameterSource.class), any(RowMapper.class));
        assertTrue(sqlCaptor.getValue().contains(":from"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_includesToFilter_whenToProvided() {
        stubEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        repository.findAll(new SettlementReportQuery(
                null, LocalDate.of(2026, 6, 30), null, null, 0, 20));

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(SqlParameterSource.class), any(RowMapper.class));
        assertTrue(sqlCaptor.getValue().contains(":toExclusive"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_includesAssignorFilter_whenAssignorIdProvided() {
        stubEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        repository.findAll(new SettlementReportQuery(
                null, null, UUID.randomUUID(), null, 0, 20));

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(SqlParameterSource.class), any(RowMapper.class));
        assertTrue(sqlCaptor.getValue().contains(":assignorId"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_includesCurrencyFilter_whenCurrencyProvided() {
        stubEmpty();
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        repository.findAll(new SettlementReportQuery(
                null, null, null, CurrencyCode.BRL, 0, 20));

        verify(jdbcTemplate).query(sqlCaptor.capture(), any(SqlParameterSource.class), any(RowMapper.class));
        assertTrue(sqlCaptor.getValue().contains(":currency"));
    }

    @Test
    void findAll_calculatesTotalPagesCorrectly_whenTotalMatchesExactPage() {
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Long.class)))
                .thenReturn(40L);
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        SettlementReportPage page = repository.findAll(
                new SettlementReportQuery(null, null, null, null, 0, 20));

        assertEquals(40L, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    @Test
    void findAll_calculatesTotalPagesCorrectly_whenTotalIsUneven() {
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Long.class)))
                .thenReturn(41L);
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        SettlementReportPage page = repository.findAll(
                new SettlementReportQuery(null, null, null, null, 0, 20));

        assertEquals(3, page.totalPages());
    }

    @Test
    @SuppressWarnings("unchecked")
    void rowMapper_mapsResultSetToSettlementReportItem() throws Exception {
        ArgumentCaptor<RowMapper<SettlementReportItem>> mapperCaptor =
                ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Long.class)))
                .thenReturn(0L);
        when(jdbcTemplate.query(anyString(), any(SqlParameterSource.class), mapperCaptor.capture()))
                .thenReturn(List.of());

        repository.findAll(new SettlementReportQuery(null, null, null, null, 0, 20));

        UUID settlementId = UUID.randomUUID();
        UUID receivableId = UUID.randomUUID();
        UUID assignorId = UUID.randomUUID();
        Timestamp ts = Timestamp.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());

        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("settlement_id", UUID.class)).thenReturn(settlementId);
        when(rs.getObject("receivable_id", UUID.class)).thenReturn(receivableId);
        when(rs.getObject("assignor_id", UUID.class)).thenReturn(assignorId);
        when(rs.getString("assignor_name")).thenReturn("Empresa XPTO Ltda");
        when(rs.getString("receivable_type")).thenReturn("DUPLICATA");
        when(rs.getBigDecimal("face_value")).thenReturn(new BigDecimal("10000.0000"));
        when(rs.getBigDecimal("settled_amount")).thenReturn(new BigDecimal("9500.0000"));
        when(rs.getString("receivable_currency")).thenReturn("BRL");
        when(rs.getString("payment_currency")).thenReturn("BRL");
        when(rs.getBigDecimal("exchange_rate_value")).thenReturn(BigDecimal.ONE);
        when(rs.getString("exchange_rate_base_currency_code")).thenReturn("BRL");
        when(rs.getString("exchange_rate_quote_currency_code")).thenReturn("BRL");
        when(rs.getTimestamp("exchange_rate_used_at")).thenReturn(ts);
        when(rs.getString("settlement_status")).thenReturn("CONFIRMED");
        when(rs.getTimestamp("settled_at")).thenReturn(ts);
        when(rs.getDate("due_date")).thenReturn(Date.valueOf(LocalDate.of(2026, 12, 31)));

        SettlementReportItem item = mapperCaptor.getValue().mapRow(rs, 0);

        assertNotNull(item);
        assertEquals(settlementId, item.settlementId());
        assertEquals(receivableId, item.receivableId());
        assertEquals(assignorId, item.assignorId());
        assertEquals("Empresa XPTO Ltda", item.assignorName());
        assertEquals("DUPLICATA", item.receivableType());
        assertEquals(new BigDecimal("10000.0000"), item.faceValue());
        assertEquals(new BigDecimal("9500.0000"), item.settledAmount());
        assertEquals("BRL", item.receivableCurrency());
        assertEquals("BRL", item.paymentCurrency());
        assertEquals(BigDecimal.ONE, item.exchangeRateValue());
        assertEquals("BRL", item.exchangeRateBaseCurrency());
        assertEquals("BRL", item.exchangeRateQuoteCurrency());
        assertEquals("CONFIRMED", item.settlementStatus());
        assertEquals(LocalDate.of(2026, 12, 31), item.dueDate());
    }
}
