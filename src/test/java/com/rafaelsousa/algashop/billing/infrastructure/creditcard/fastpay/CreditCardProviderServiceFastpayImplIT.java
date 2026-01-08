package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.rafaelsousa.algashop.billing.infrastructure.AbstractFastpayIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(FastpayCreditCardTokenizationApiClientConfig.class)
class CreditCardProviderServiceFastpayImplIT extends AbstractFastpayIT {

    @Test
    void shouldRegisterCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        assertThat(limitedCreditCard.getGatewayCode()).isNotBlank();
    }

    @Test
    void shouldFindRegisteredCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        LimitedCreditCard limitedCreditCardFound = creditCardProvider
                .findById(limitedCreditCard.getGatewayCode()).orElseThrow();

        assertThat(limitedCreditCard.getGatewayCode()).isEqualTo(limitedCreditCardFound.getGatewayCode());
    }

    @Test
    void shoudRemoveCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        creditCardProvider.delete(limitedCreditCard.getGatewayCode());
        Optional<LimitedCreditCard> limitedCreditCardOptional = creditCardProvider
                .findById(limitedCreditCard.getGatewayCode());

        assertThat(limitedCreditCardOptional).isEmpty();
    }
}