package br.com.srm.creditengine.infrastructure.reporting;

import br.com.srm.creditengine.reporting.settlement.SettlementReportItem;
import br.com.srm.creditengine.reporting.settlement.SettlementReportPage;
import br.com.srm.creditengine.reporting.settlement.SettlementReportQuery;
import br.com.srm.creditengine.reporting.settlement.SettlementReportRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcSettlementReportRepository implements SettlementReportRepository {

    private static final String BASE_SELECT = """
            SELECT
                s.id                                  AS settlement_id,
                s.receivable_id,
                s.assignor_id,
                a.legal_name                          AS assignor_name,
                rt.code                               AS receivable_type,
                r.face_value,
                s.settled_amount,
                rc.code                               AS receivable_currency,
                pc.code                               AS payment_currency,
                s.exchange_rate_value,
                s.exchange_rate_base_currency_code,
                s.exchange_rate_quote_currency_code,
                s.exchange_rate_used_at,
                s.status                              AS settlement_status,
                s.created_at                          AS settled_at,
                r.due_date
            FROM settlements s
            JOIN receivables r       ON r.id = s.receivable_id
            JOIN assignors a         ON a.id = s.assignor_id
            JOIN currencies rc       ON rc.id = r.currency_id
            JOIN currencies pc       ON pc.id = s.payment_currency_id
            JOIN receivable_types rt ON rt.id = r.receivable_type_id""";

    private static final String BASE_COUNT = """
            SELECT COUNT(*)
            FROM settlements s
            JOIN receivables r ON r.id = s.receivable_id
            JOIN currencies pc ON pc.id = s.payment_currency_id""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcSettlementReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SettlementReportPage findAll(SettlementReportQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String where = buildWhere(query, params);

        Long total = jdbcTemplate.queryForObject(BASE_COUNT + where, params, Long.class);
        long totalElements = total != null ? total : 0L;

        params.addValue("size", query.size());
        params.addValue("offset", (long) query.page() * query.size());

        String dataSql = BASE_SELECT + where + " ORDER BY s.created_at DESC LIMIT :size OFFSET :offset";
        List<SettlementReportItem> items = jdbcTemplate.query(dataSql, params, rowMapper());

        int totalPages = totalElements == 0L ? 0 : (int) ((totalElements + query.size() - 1) / query.size());
        return new SettlementReportPage(items, query.page(), query.size(), totalElements, totalPages);
    }

    private String buildWhere(SettlementReportQuery query, MapSqlParameterSource params) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");

        if (query.from() != null) {
            where.append(" AND s.created_at >= :from");
            params.addValue("from", query.from().atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        if (query.to() != null) {
            // exclusive upper bound: covers the full 'to' day
            where.append(" AND s.created_at < :toExclusive");
            params.addValue("toExclusive", query.to().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        if (query.assignorId() != null) {
            where.append(" AND s.assignor_id = :assignorId");
            params.addValue("assignorId", query.assignorId());
        }
        if (query.currency() != null) {
            where.append(" AND pc.code = :currency");
            params.addValue("currency", query.currency().name());
        }

        return where.toString();
    }

    RowMapper<SettlementReportItem> rowMapper() {
        return this::mapRow;
    }

    private SettlementReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SettlementReportItem(
                rs.getObject("settlement_id", UUID.class),
                rs.getObject("receivable_id", UUID.class),
                rs.getObject("assignor_id", UUID.class),
                rs.getString("assignor_name"),
                rs.getString("receivable_type"),
                rs.getBigDecimal("face_value"),
                rs.getBigDecimal("settled_amount"),
                rs.getString("receivable_currency"),
                rs.getString("payment_currency"),
                rs.getBigDecimal("exchange_rate_value"),
                rs.getString("exchange_rate_base_currency_code"),
                rs.getString("exchange_rate_quote_currency_code"),
                toOffsetDateTime(rs.getTimestamp("exchange_rate_used_at")),
                rs.getString("settlement_status"),
                toOffsetDateTime(rs.getTimestamp("settled_at")),
                rs.getDate("due_date").toLocalDate()
        );
    }

    private OffsetDateTime toOffsetDateTime(Timestamp ts) {
        return ts.toInstant().atOffset(ZoneOffset.UTC);
    }
}
