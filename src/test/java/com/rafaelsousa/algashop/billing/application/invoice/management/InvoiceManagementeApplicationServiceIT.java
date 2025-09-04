package com.rafaelsousa.algashop.billing.application.invoice.management;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCard;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardTestDataBuilder;
import com.rafaelsousa.algashop.billing.domain.model.invoice.*;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class InvoiceManagementeApplicationServiceIT {
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final InvoiceManagementeApplicationService invoiceManagementeApplicationService;

    @MockitoSpyBean
    private InvoiceService invoiceService;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    InvoiceManagementeApplicationServiceIT(InvoiceRepository invoiceRepository, CreditCardRepository creditCardRepository,
                                           InvoiceManagementeApplicationService invoiceManagementeApplicationService) {
        this.invoiceRepository = invoiceRepository;
        this.creditCardRepository = creditCardRepository;
        this.invoiceManagementeApplicationService = invoiceManagementeApplicationService;
    }

    @Test
    void shouldIssueInvoiceWithCreditCardAsPayment() {
        UUID customerId = UUID.randomUUID();
        CreditCard creditCard = CreditCardTestDataBuilder.aCreditCard().customerId(customerId).build();
        creditCardRepository.saveAndFlush(creditCard);

        IssueInvoiceInput.IssueInvoiceInputBuilder invoiceInput = GenerateInvoiceInputTestDataBuilder.anInput();
        invoiceInput
                .customerId(customerId)
                .paymentSettings(
                        PaymentSettingsInput.builder()
                                .method(PaymentMethod.CREDIT_CARD)
                                .creditCardId(creditCard.getId())
                                .build()
                );

        UUID invoiceId = invoiceManagementeApplicationService.generate(invoiceInput.build());

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getId()).isEqualTo(invoiceId);
            assertThat(i.getCustomerId()).isEqualTo(customerId.toString());
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
            assertThat(i.getPaymentSettings().getMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
            assertThat(i.getPaymentSettings().getCreditCardId()).isNotNull();
            assertThat(i.getPaymentSettings().getCreditCardId()).isEqualTo(creditCard.getId());
        });

        verify(invoiceService).issue(invoice.getOrderId(), customerId, invoice.getPayer(), invoice.getItems());
    }

    @Test
    void shouldIssueInvoiceWithGatewayBalanceAsPayment() {
        UUID customerId = UUID.randomUUID();

        IssueInvoiceInput.IssueInvoiceInputBuilder invoiceInput = GenerateInvoiceInputTestDataBuilder.anInput();
        invoiceInput
                .customerId(customerId)
                .paymentSettings(
                        PaymentSettingsInput.builder()
                                .method(PaymentMethod.GATEWAY_BALANCE)
                                .build()
                );

        UUID invoiceId = invoiceManagementeApplicationService.generate(invoiceInput.build());

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getId()).isEqualTo(invoiceId);
            assertThat(i.getCustomerId()).isEqualTo(customerId.toString());
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
            assertThat(i.getPaymentSettings().getCreditCardId()).isNull();
        });

        verify(invoiceService).issue(invoice.getOrderId(), customerId, invoice.getPayer(), invoice.getItems());
    }

    @Test
    void shouldThrowExceptionWhenTryingIssueInvoiceWithoutNonExistentCreditCard() {
        UUID customerId = UUID.randomUUID();

        IssueInvoiceInput.IssueInvoiceInputBuilder invoiceInputBuilder = GenerateInvoiceInputTestDataBuilder.anInput();
        invoiceInputBuilder
                .customerId(customerId)
                .paymentSettings(
                        PaymentSettingsInput.builder()
                                .method(PaymentMethod.CREDIT_CARD)
                                .creditCardId(UUID.randomUUID())
                                .build()
                );

        IssueInvoiceInput invoiceInput = invoiceInputBuilder.build();
        assertThatThrownBy(() -> invoiceManagementeApplicationService.generate(invoiceInput))
                .isInstanceOf(CreditCardNotFoundException.class);
    }

    @Test
    void shouldProcessInvoicePayment() {
        InvoiceTestDataBuilder invoiceTestDataBuilder = InvoiceTestDataBuilder.anInvoice();
        Invoice invoice = invoiceTestDataBuilder
                .paymentSettings(PaymentMethod.GATEWAY_BALANCE, null)
                .build();
        invoiceRepository.saveAndFlush(invoice);

        UUID invoiceId = invoice.getId();
        Payment payment = Payment.builder()
                .gatewayCode("12345")
                .status(PaymentStatus.PAID)
                .method(invoice.getPaymentSettings().getMethod())
                .invoiceId(invoiceId)
                .build();

        when(paymentGatewayService.capture(any(PaymentRequest.class))).thenReturn(payment);

        invoiceManagementeApplicationService.processPayment(invoiceId);

        Invoice paidInvoice = invoiceRepository.findById(invoiceId).orElseThrow();

        assertThat(paidInvoice).satisfies(i -> {
            assertThat(i.isPaid()).isTrue();
            assertThat(i.getPaymentSettings().getMethod()).isEqualTo(PaymentMethod.GATEWAY_BALANCE);
            assertThat(i.getPaymentSettings().getCreditCardId()).isNull();
        });

        verify(paymentGatewayService, times(1)).capture(any(PaymentRequest.class));
        verify(invoiceService, times(1)).assignPayment(any(Invoice.class), any(Payment.class));
    }
}