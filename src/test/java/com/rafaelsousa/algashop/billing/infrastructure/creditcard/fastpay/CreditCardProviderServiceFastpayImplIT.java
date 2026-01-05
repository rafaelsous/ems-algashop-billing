package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(FastpayCreditCardTokenizationApiClientConfig.class)
class CreditCardProviderServiceFastpayImplIT {

    @Autowired
    private CreditCardProviderServiceFastpayImpl creditCardProvider;

    @Autowired
    private FastpayCreditCardTokenizationApiClient tokenizationApiClient;

    private static final UUID VALID_CUSTOMER_ID = UUID.randomUUID();
    private static final String ALWASYS_PAID_CARD_NUMBER = "4622943127011022";


    @Test
    void shouldRegisterCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        assertThat(limitedCreditCard.getGatewayCode()).isNotBlank();
    }

    @Test
    void shouldFindRegisteredCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        LimitedCreditCard limitedCreditCardFound = creditCardProvider.findById(limitedCreditCard.getGatewayCode()).orElseThrow();

        assertThat(limitedCreditCard.getGatewayCode()).isEqualTo(limitedCreditCardFound.getGatewayCode());
    }

    @Test
    void shoudRemoveCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        creditCardProvider.delete(limitedCreditCard.getGatewayCode());
        Optional<LimitedCreditCard> limitedCreditCardOptional = creditCardProvider.findById(limitedCreditCard.getGatewayCode());

        assertThat(limitedCreditCardOptional).isEmpty();
    }

    private LimitedCreditCard registerCard() {
        FastpayTokenizationInput tokenizationInput = FastpayTokenizationInput.builder()
                .number(ALWASYS_PAID_CARD_NUMBER)
                .cvv("333")
                .holderName("John Doe")
                .holderDocument("123234")
                .expMonth(7)
                .expYear(Year.now().plusYears(7).getValue())
                .build();

        FastpayTokenizedCreditCardModel response = tokenizationApiClient.tokenize(tokenizationInput);

        return creditCardProvider.register(VALID_CUSTOMER_ID, response.getTokenizedCard());
    }
}