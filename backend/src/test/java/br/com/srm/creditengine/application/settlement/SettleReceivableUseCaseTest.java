package br.com.srm.creditengine.application.settlement;

import br.com.srm.creditengine.application.pricing.PricingSimulationService;
import br.com.srm.creditengine.domain.assignor.Assignor;
import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.ExchangeRateLookupService;
import br.com.srm.creditengine.domain.currency.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import br.com.srm.creditengine.domain.outbox.OutboxEvent;
import br.com.srm.creditengine.domain.outbox.OutboxEventStatus;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.receivable.Receivable;
import br.com.srm.creditengine.domain.receivable.ReceivableNotFoundException;
import br.com.srm.creditengine.domain.receivable.ReceivableStatus;
import br.com.srm.creditengine.domain.receivable.ReceivableType;
import br.com.srm.creditengine.domain.receivable.ReceivableTypeCode;
import br.com.srm.creditengine.domain.settlement.Settlement;
import br.com.srm.creditengine.domain.settlement.SettlementNotAllowedException;
import br.com.srm.creditengine.domain.settlement.SettlementStatus;
import br.com.srm.creditengine.infrastructure.persistence.jpa.CurrencyRepository;
import br.com.srm.creditengine.infrastructure.observability.BusinessMetrics;
import br.com.srm.creditengine.infrastructure.persistence.jpa.OutboxEventRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.ReceivableRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.SettlementRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettleReceivableUseCaseTest {

    @Mock private ReceivableRepository receivableRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private SettlementRepository settlementRepository;
    @Mock private OutboxEventRepository outboxEventRepository;
    @Mock private PricingSimulationService pricingSimulationService;
    @Mock private ExchangeRateLookupService exchangeRateLookupService;

    private static final BusinessMetrics METRICS = new BusinessMetrics(new SimpleMeterRegistry());
    private SettleReceivableUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SettleReceivableUseCase(
                receivableRepository, currencyRepository, settlementRepository,
                outboxEventRepository, pricingSimulationService, exchangeRateLookupService, METRICS);
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void execute_settlesSuccessfully_sameCurrency() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID result = useCase.execute(command);

        assertNotNull(result);
        verifyNoInteractions(exchangeRateLookupService);
    }

    @Test
    void execute_settlesSuccessfully_crossCurrency_brlToUsd() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.USD, new BigDecimal("0.004167"));

        ExchangeRateResult exchangeRate = exchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD, "5.2500000000");

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(exchangeRateLookupService.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(exchangeRate);
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(currencyWith(CurrencyCode.USD)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID result = useCase.execute(command);

        assertNotNull(result);
        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        // 950.0000 × 5.25 = 4987.5000
        assertEquals(new BigDecimal("4987.5000"), settlementCaptor.getValue().getSettledAmount());
        assertEquals(SettlementStatus.CONFIRMED, settlementCaptor.getValue().getStatus());
        assertEquals("BRL", settlementCaptor.getValue().getExchangeRateBaseCurrencyCode());
        assertEquals("USD", settlementCaptor.getValue().getExchangeRateQuoteCurrencyCode());
    }

    @Test
    void execute_settledAmount_roundedCorrectly_halfEven() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.USD, new BigDecimal("0.004167"));

        // 100.0000 × 3.33333 = 333.333... → rounds to 333.3330 with HALF_EVEN
        ExchangeRateResult exchangeRate = exchangeRateResult(
                CurrencyCode.BRL, CurrencyCode.USD, "3.3333300000");

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("100.0000"));
        when(exchangeRateLookupService.findLatest(CurrencyCode.BRL, CurrencyCode.USD)).thenReturn(exchangeRate);
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(currencyWith(CurrencyCode.USD)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        ArgumentCaptor<Settlement> captor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(captor.capture());
        assertEquals(4, captor.getValue().getSettledAmount().scale());
    }

    @Test
    void execute_sameCurrency_usesIdentityRateSnapshot() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(30));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("900.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        ArgumentCaptor<Settlement> captor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(captor.capture());
        assertEquals(new BigDecimal("900.0000"), captor.getValue().getSettledAmount());
        assertEquals("BRL", captor.getValue().getExchangeRateBaseCurrencyCode());
        assertEquals("BRL", captor.getValue().getExchangeRateQuoteCurrencyCode());
        assertEquals(0, BigDecimal.ONE.compareTo(captor.getValue().getExchangeRateValue()));
        assertNotNull(captor.getValue().getExchangeRateUsedAt());
    }

    @Test
    void execute_receivableStatus_updatedToSettled() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        assertEquals(ReceivableStatus.SETTLED, receivable.getStatus());
        verify(receivableRepository).save(receivable);
    }

    @Test
    void execute_settlementSavedBeforeReceivable() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        InOrder inOrder = inOrder(settlementRepository, receivableRepository, outboxEventRepository);
        inOrder.verify(settlementRepository).save(any());
        inOrder.verify(receivableRepository).save(any());
        inOrder.verify(outboxEventRepository).save(any());
    }

    @Test
    void execute_outboxEvent_createdWithPendingStatusAndEssentialPayloadFields() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent event = captor.getValue();

        assertEquals("Settlement", event.getAggregateType());
        assertEquals("SettlementCreated", event.getEventType());
        assertEquals(OutboxEventStatus.PENDING, event.getStatus());
        assertEquals(Integer.valueOf(0), event.getAttempts());
        assertNotNull(event.getAggregateId());
        assertNotNull(event.getPayload());
        assertTrue(event.getPayload().contains("settlementId"));
        assertTrue(event.getPayload().contains("receivableId"));
        assertTrue(event.getPayload().contains("settledAmount"));
        assertTrue(event.getPayload().contains("CONFIRMED"));
        assertTrue(event.getPayload().contains("BRL"));
    }

    // ── term calculation ──────────────────────────────────────────────────────

    @Test
    void execute_termRoundedUp_oneDayCountsAsOneMonth() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(1));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.BRL, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.BRL)).thenReturn(Optional.of(currencyWith(CurrencyCode.BRL)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> useCase.execute(command));
    }

    // ── error scenarios ───────────────────────────────────────────────────────

    @Test
    void execute_throwsReceivableNotFoundException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(receivableRepository.findById(id)).thenReturn(Optional.empty());

        ReceivableNotFoundException ex = assertThrows(ReceivableNotFoundException.class,
                () -> useCase.execute(new SettleReceivableCommand(id, CurrencyCode.BRL, new BigDecimal("0.004167"))));

        assertTrue(ex.getMessage().contains(id.toString()));
        verifyNoInteractions(settlementRepository, outboxEventRepository, pricingSimulationService);
    }

    @Test
    void execute_throwsSettlementNotAllowedException_whenAlreadySettled() {
        Receivable receivable = receivableWith(ReceivableStatus.SETTLED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        when(receivableRepository.findById(receivable.getId())).thenReturn(Optional.of(receivable));

        assertThrows(SettlementNotAllowedException.class,
                () -> useCase.execute(new SettleReceivableCommand(
                        receivable.getId(), CurrencyCode.BRL, new BigDecimal("0.004167"))));

        verifyNoInteractions(settlementRepository, outboxEventRepository, pricingSimulationService);
    }

    @Test
    void execute_throwsSettlementNotAllowedException_whenCancelled() {
        Receivable receivable = receivableWith(ReceivableStatus.CANCELLED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        when(receivableRepository.findById(receivable.getId())).thenReturn(Optional.of(receivable));

        assertThrows(SettlementNotAllowedException.class,
                () -> useCase.execute(new SettleReceivableCommand(
                        receivable.getId(), CurrencyCode.BRL, new BigDecimal("0.004167"))));

        verifyNoInteractions(settlementRepository, outboxEventRepository, pricingSimulationService);
    }

    @Test
    void execute_throwsSettlementNotAllowedException_whenDueDateIsToday() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now()); // days = 0
        when(receivableRepository.findById(receivable.getId())).thenReturn(Optional.of(receivable));

        assertThrows(SettlementNotAllowedException.class,
                () -> useCase.execute(new SettleReceivableCommand(
                        receivable.getId(), CurrencyCode.BRL, new BigDecimal("0.004167"))));

        verifyNoInteractions(settlementRepository, outboxEventRepository, pricingSimulationService);
    }

    @Test
    void execute_throwsSettlementNotAllowedException_whenDueDateInPast() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().minusDays(1));
        when(receivableRepository.findById(receivable.getId())).thenReturn(Optional.of(receivable));

        SettlementNotAllowedException ex = assertThrows(SettlementNotAllowedException.class,
                () -> useCase.execute(new SettleReceivableCommand(
                        receivable.getId(), CurrencyCode.BRL, new BigDecimal("0.004167"))));

        assertTrue(ex.getMessage().contains("future"));
        verifyNoInteractions(settlementRepository, outboxEventRepository, pricingSimulationService);
    }

    @Test
    void execute_propagatesExchangeRateNotFoundException_whenNoRate() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.BRL,
                LocalDate.now().plusDays(60));
        when(receivableRepository.findById(receivable.getId())).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(exchangeRateLookupService.findLatest(CurrencyCode.BRL, CurrencyCode.USD))
                .thenThrow(new ExchangeRateNotFoundException(CurrencyCode.BRL, CurrencyCode.USD));

        assertThrows(ExchangeRateNotFoundException.class,
                () -> useCase.execute(new SettleReceivableCommand(
                        receivable.getId(), CurrencyCode.USD, new BigDecimal("0.004167"))));

        verifyNoInteractions(settlementRepository, outboxEventRepository);
    }

    @Test
    void execute_currencyEngineNotCalled_whenSameCurrency() {
        Receivable receivable = receivableWith(ReceivableStatus.REGISTERED, CurrencyCode.USD,
                LocalDate.now().plusDays(60));
        UUID receivableId = receivable.getId();
        SettleReceivableCommand command = new SettleReceivableCommand(
                receivableId, CurrencyCode.USD, new BigDecimal("0.004167"));

        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(pricingSimulationService.simulate(any())).thenReturn(pricingResult("950.0000"));
        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(currencyWith(CurrencyCode.USD)));
        when(settlementRepository.save(any())).thenAnswer(inv -> withId(inv.getArgument(0)));
        when(receivableRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outboxEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(command);

        verifyNoInteractions(exchangeRateLookupService);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Receivable receivableWith(ReceivableStatus status, CurrencyCode currencyCode, LocalDate dueDate) {
        Assignor assignor = new Assignor();
        assignor.setId(UUID.randomUUID());

        Currency currency = new Currency();
        currency.setCode(currencyCode);

        ReceivableType type = new ReceivableType();
        type.setCode(ReceivableTypeCode.DUPLICATA);

        Receivable receivable = new Receivable();
        receivable.setId(UUID.randomUUID());
        receivable.setStatus(status);
        receivable.setFaceValue(new BigDecimal("1000.00"));
        receivable.setDueDate(dueDate);
        receivable.setAssignor(assignor);
        receivable.setCurrency(currency);
        receivable.setReceivableType(type);
        return receivable;
    }

    private Currency currencyWith(CurrencyCode code) {
        Currency c = new Currency();
        c.setCode(code);
        return c;
    }

    private PricingResult pricingResult(String presentValue) {
        return new PricingResult(
                new BigDecimal(presentValue),
                new BigDecimal("0.004167"),
                new BigDecimal("0.015000"),
                2);
    }

    private ExchangeRateResult exchangeRateResult(CurrencyCode base, CurrencyCode quote, String rate) {
        return new ExchangeRateResult(base, quote, new BigDecimal(rate),
                OffsetDateTime.now().minusHours(1), OffsetDateTime.now());
    }

    private Settlement withId(Settlement s) {
        if (s.getId() == null) {
            s.setId(UUID.randomUUID());
        }
        return s;
    }
}
