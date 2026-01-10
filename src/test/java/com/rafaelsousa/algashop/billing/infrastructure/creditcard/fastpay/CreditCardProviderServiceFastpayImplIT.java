package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.rafaelsousa.algashop.billing.infrastructure.AbstractFastpayIT;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Import(FastpayCreditCardTokenizationApiClientConfig.class)
class CreditCardProviderServiceFastpayImplIT extends AbstractFastpayIT {

    @BeforeAll
    static void setUp() {
        startMock();
    }

    @AfterAll
    static void after() {
        stopMock();
    }

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

        assertDoesNotThrow(() -> creditCardProvider.delete(limitedCreditCard.getGatewayCode()));
    }
}