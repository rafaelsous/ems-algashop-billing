package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelsousa.algashop.billing.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardProviderService;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.rafaelsousa.algashop.billing.presentation.BadGatewayException;
import com.rafaelsousa.algashop.billing.presentation.ExternalApiErrorResponse;
import com.rafaelsousa.algashop.billing.presentation.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
public class CreditCardProviderServiceFastpayImpl implements CreditCardProviderService {
    private final FastpayCreditCardApiClient fastpayCreditCardApiClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public LimitedCreditCard register(UUID customerId, String tokenizedCard) {
        FastpayCreditCardInput creditCardInput = FastpayCreditCardInput.builder()
                .tokenizedCard(tokenizedCard)
                .customerCode(customerId.toString())
                .build();

        FastpayCreditCardResponse creditCardResponse;
        try {
            creditCardResponse = fastpayCreditCardApiClient.create(creditCardInput);
        } catch (HttpClientErrorException.BadRequest ex) {
            ExternalApiErrorResponse error = getExternalApiErrorResponse(ex);

            throw new DomainEntityNotFoundException(error.getTitle(), ex);
        } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Fastpay API Timeout", ex);
        } catch (HttpClientErrorException ex) {
            throw new BadGatewayException("Fastpay API Bad Gateway");
        }

        return toLimitedCreditCard(creditCardResponse);
    }

    @Override
    public Optional<LimitedCreditCard> findById(String gatewayCode) {
        FastpayCreditCardResponse creditCardResponse;

        try {
            creditCardResponse = fastpayCreditCardApiClient.findyById(gatewayCode);
        } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Fastpay API Timeout", ex);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new BadGatewayException("Fastpay API Bad Gateway", ex);
        }

        return Optional.of(toLimitedCreditCard(creditCardResponse));
    }

    @Override
    public void delete(String gatewayCode) {
        try {
            fastpayCreditCardApiClient.delete(gatewayCode);
        } catch (ResourceAccessException ex) {
            throw new GatewayTimeoutException("Fastpay API Timeout", ex);
        } catch (HttpClientErrorException ex) {
            throw new BadGatewayException("Fastpay API Bad Gateway");
        }
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

    private ExternalApiErrorResponse getExternalApiErrorResponse(HttpClientErrorException ex) throws JsonProcessingException {
        return objectMapper.readValue(ex.getResponseBodyAsString(), ExternalApiErrorResponse.class);
    }
}