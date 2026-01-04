package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fake;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardProviderService;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FAKE")
public class CreditCardProviderServiceFakeImpl implements CreditCardProviderService {

    @Override
    public LimitedCreditCard register(UUID customerId, String tokenizedCard) {
        return fakeCard();
    }

    @Override
    public Optional<LimitedCreditCard> findById(String gatewayCode) {
        return Optional.of(fakeCard());
    }

    @Override
    public void delete(String gatewayCode) {
        log.info("Credit card removed successfuly");
    }

    private LimitedCreditCard fakeCard() {
        return LimitedCreditCard.builder()
                .lastNumbers("4321")
                .brand("Visa")
                .expMonth(7)
                .expYear(Year.now().plusYears(7).getValue())
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }
}