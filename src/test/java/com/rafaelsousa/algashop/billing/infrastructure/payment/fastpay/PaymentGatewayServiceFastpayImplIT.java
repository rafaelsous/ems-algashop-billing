package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCard;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceTestDataBuilder;
import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.rafaelsousa.algashop.billing.infrastructure.AbstractFastpayIT;
import com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay.FastpayCreditCardTokenizationApiClientConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@Import(FastpayCreditCardTokenizationApiClientConfig.class)
class PaymentGatewayServiceFastpayImplIT extends AbstractFastpayIT {

    @BeforeAll
    static void setUp() {
        startMock();
    }

    @AfterAll
    static void after() {
        stopMock();
    }

    @Autowired
    private PaymentGatewayServiceFastpayImpl paymentGatewayServiceFastpay;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Test
    void shouldProcessPaymentWithCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        CreditCard creditCard = CreditCard.brandNew(
                VALID_CUSTOMER_ID,
                limitedCreditCard.getLastNumbers(),
                limitedCreditCard.getBrand(),
                limitedCreditCard.getExpMonth(),
                limitedCreditCard.getExpYear(),
                limitedCreditCard.getGatewayCode()
        );

        creditCardRepository.save(creditCard);

        UUID invoiceId = UUID.randomUUID();

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .method(PaymentMethod.CREDIT_CARD)
                .amount(BigDecimal.valueOf(1500.00))
                .invoiceId(invoiceId)
                .creditCardId(creditCard.getId())
                .payer(InvoiceTestDataBuilder.aPayer())
                .build();

        Payment payment = paymentGatewayServiceFastpay.capture(paymentRequest);

        assertThat(payment.getInvoiceId()).isEqualTo(invoiceId);
        System.out.println(payment.getGatewayCode());
    }
}