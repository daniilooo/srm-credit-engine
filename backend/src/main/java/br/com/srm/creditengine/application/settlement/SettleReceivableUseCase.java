package br.com.srm.creditengine.application.settlement;

import br.com.srm.creditengine.application.pricing.PricingSimulationService;
import br.com.srm.creditengine.domain.currency.CurrencyCode;
import br.com.srm.creditengine.domain.currency.CurrencyConversionException;
import br.com.srm.creditengine.domain.currency.ExchangeRateLookupService;
import br.com.srm.creditengine.domain.currency.ExchangeRateResult;
import br.com.srm.creditengine.domain.outbox.OutboxEvent;
import br.com.srm.creditengine.domain.outbox.OutboxEventStatus;
import br.com.srm.creditengine.domain.pricing.PricingRequest;
import br.com.srm.creditengine.domain.pricing.PricingResult;
import br.com.srm.creditengine.domain.receivable.Receivable;
import br.com.srm.creditengine.domain.receivable.ReceivableNotFoundException;
import br.com.srm.creditengine.domain.receivable.ReceivableStatus;
import br.com.srm.creditengine.domain.settlement.Settlement;
import br.com.srm.creditengine.domain.settlement.SettlementNotAllowedException;
import br.com.srm.creditengine.domain.settlement.SettlementStatus;
import br.com.srm.creditengine.domain.currency.Currency;
import br.com.srm.creditengine.infrastructure.persistence.jpa.CurrencyRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.OutboxEventRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.ReceivableRepository;
import br.com.srm.creditengine.infrastructure.persistence.jpa.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class SettleReceivableUseCase {

    private final ReceivableRepository receivableRepository;
    private final CurrencyRepository currencyRepository;
    private final SettlementRepository settlementRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final PricingSimulationService pricingSimulationService;
    private final ExchangeRateLookupService exchangeRateLookupService;

    public SettleReceivableUseCase(
            ReceivableRepository receivableRepository,
            CurrencyRepository currencyRepository,
            SettlementRepository settlementRepository,
            OutboxEventRepository outboxEventRepository,
            PricingSimulationService pricingSimulationService,
            ExchangeRateLookupService exchangeRateLookupService) {
        this.receivableRepository = Objects.requireNonNull(receivableRepository, "receivableRepository");
        this.currencyRepository = Objects.requireNonNull(currencyRepository, "currencyRepository");
        this.settlementRepository = Objects.requireNonNull(settlementRepository, "settlementRepository");
        this.outboxEventRepository = Objects.requireNonNull(outboxEventRepository, "outboxEventRepository");
        this.pricingSimulationService = Objects.requireNonNull(pricingSimulationService, "pricingSimulationService");
        this.exchangeRateLookupService = Objects.requireNonNull(exchangeRateLookupService, "exchangeRateLookupService");
    }

    public UUID execute(SettleReceivableCommand command) {
        Objects.requireNonNull(command, "command");

        // 1. Find receivable — ReceivableNotFoundException if absent
        Receivable receivable = receivableRepository.findById(command.receivableId())
                .orElseThrow(() -> new ReceivableNotFoundException(command.receivableId()));

        // 2. Validate eligibility — only REGISTERED receivables can be settled
        if (receivable.getStatus() != ReceivableStatus.REGISTERED) {
            throw new SettlementNotAllowedException(
                    "Receivable " + command.receivableId() + " is not eligible for settlement. Current status: "
                            + receivable.getStatus());
        }

        // 3. Calculate term in months — ceiling division: (days + 29) / 30
        long days = ChronoUnit.DAYS.between(LocalDate.now(), receivable.getDueDate());
        if (days <= 0) {
            throw new SettlementNotAllowedException(
                    "Receivable due date must be in the future. Days remaining: " + days);
        }
        int termInMonths = (int) ((days + 29) / 30);

        // 4. Calculate present value via Pricing Engine
        PricingRequest pricingRequest = new PricingRequest(
                receivable.getFaceValue(),
                command.baseTaxMonthly(),
                termInMonths,
                receivable.getReceivableType().getCode().name());
        PricingResult pricingResult = pricingSimulationService.simulate(pricingRequest);
        BigDecimal presentValue = pricingResult.getPresentValue();

        // 5. Determine exchange rate snapshot and settled amount
        CurrencyCode receivableCurrencyCode = receivable.getCurrency().getCode();
        CurrencyCode paymentCurrencyCode = command.paymentCurrencyCode();

        BigDecimal settledAmount;
        String exchangeRateBaseCurrencyCode;
        String exchangeRateQuoteCurrencyCode;
        BigDecimal exchangeRateValue;
        OffsetDateTime exchangeRateUsedAt;

        if (receivableCurrencyCode == paymentCurrencyCode) {
            // Same currency — identity rate, no Currency Engine lookup
            settledAmount = presentValue;
            exchangeRateBaseCurrencyCode = paymentCurrencyCode.name();
            exchangeRateQuoteCurrencyCode = paymentCurrencyCode.name();
            exchangeRateValue = BigDecimal.ONE;
            exchangeRateUsedAt = OffsetDateTime.now(ZoneOffset.UTC);
        } else {
            // Cross-currency — lookup rate and apply at the end
            ExchangeRateResult exchangeRate = exchangeRateLookupService.findLatest(
                    receivableCurrencyCode, paymentCurrencyCode);
            settledAmount = presentValue.multiply(exchangeRate.rateValue())
                    .setScale(4, RoundingMode.HALF_EVEN);
            exchangeRateBaseCurrencyCode = exchangeRate.baseCurrency().name();
            exchangeRateQuoteCurrencyCode = exchangeRate.quoteCurrency().name();
            exchangeRateValue = exchangeRate.rateValue();
            exchangeRateUsedAt = exchangeRate.capturedAt();
        }

        // 6. Resolve payment currency entity
        Currency paymentCurrency = currencyRepository.findByCode(paymentCurrencyCode)
                .orElseThrow(() -> new CurrencyConversionException("Currency not found: " + paymentCurrencyCode));

        // 7. Persist Settlement — triggers UNIQUE(receivable_id) barrier against double settlement
        Settlement settlement = new Settlement();
        settlement.setReceivable(receivable);
        settlement.setAssignor(receivable.getAssignor());
        settlement.setPaymentCurrency(paymentCurrency);
        settlement.setStatus(SettlementStatus.CONFIRMED);
        settlement.setSettledAmount(settledAmount);
        settlement.setExchangeRateBaseCurrencyCode(exchangeRateBaseCurrencyCode);
        settlement.setExchangeRateQuoteCurrencyCode(exchangeRateQuoteCurrencyCode);
        settlement.setExchangeRateValue(exchangeRateValue);
        settlement.setExchangeRateUsedAt(exchangeRateUsedAt);

        Settlement saved = settlementRepository.save(settlement);

        // 8. Update Receivable status — @Version triggers OptimisticLock barrier
        receivable.setStatus(ReceivableStatus.SETTLED);
        receivableRepository.save(receivable);

        // 9. Persist OutboxEvent in the same transaction
        String payload = ("{\"settlementId\":\"%s\",\"receivableId\":\"%s\",\"assignorId\":\"%s\"," +
                "\"settledAmount\":\"%s\",\"paymentCurrencyCode\":\"%s\"," +
                "\"exchangeRateValue\":\"%s\",\"status\":\"CONFIRMED\"}").formatted(
                saved.getId(),
                receivable.getId(),
                receivable.getAssignor().getId(),
                settledAmount.toPlainString(),
                paymentCurrencyCode.name(),
                exchangeRateValue.toPlainString());

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("Settlement");
        outboxEvent.setAggregateId(saved.getId());
        outboxEvent.setEventType("SettlementCreated");
        outboxEvent.setPayload(payload);
        outboxEvent.setStatus(OutboxEventStatus.PENDING);
        outboxEvent.setAttempts(0);

        outboxEventRepository.save(outboxEvent);

        return saved.getId();
    }
}
