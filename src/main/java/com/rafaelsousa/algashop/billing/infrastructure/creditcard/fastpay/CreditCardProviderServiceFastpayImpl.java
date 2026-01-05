package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardProviderService;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
public class CreditCardProviderServiceFastpayImpl implements CreditCardProviderService {
    private final FastpayCreditCardApiClient fastpayCreditCardApiClient;

    @Override
    public LimitedCreditCard register(UUID customerId, String tokenizedCard) {
        FastpayCreditCardInput creditCardInput = FastpayCreditCardInput.builder()
                .tokenizedCard(tokenizedCard)
                .customerCode(customerId.toString())
                .build();

        FastpayCreditCardResponse creditCardResponse = fastpayCreditCardApiClient.create(creditCardInput);

        return toLimitedCreditCard(creditCardResponse);
    }

    @Override
    public Optional<LimitedCreditCard> findById(String gatewayCode) {
        FastpayCreditCardResponse creditCardResponse;

        try {
            creditCardResponse = fastpayCreditCardApiClient.findyById(gatewayCode);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }

        return Optional.of(toLimitedCreditCard(creditCardResponse));
    }

    @Override
    public void delete(String gatewayCode) {
        fastpayCreditCardApiClient.delete(gatewayCode);
    }

    private static LimitedCreditCard toLimitedCreditCard(FastpayCreditCardResponse creditCardResponse) {
        return LimitedCreditCard.builder()
                .brand(creditCardResponse.getBrand())
                .lastNumbers(creditCardResponse.getLastNumbers())
                .expMonth(creditCardResponse.getExpMonth())
                .expYear(creditCardResponse.getExpYear())
                .gatewayCode(creditCardResponse.getId())
                .build();
    }
}