package com.rafaelsousa.algashop.billing.application.invoice.management;

import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCard;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardTestDataBuilder;
import com.rafaelsousa.algashop.billing.domain.model.invoice.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
class InvoiceManagementeApplicationServiceIT {
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final InvoiceManagementeApplicationService invoiceManagementeApplicationService;

    @MockitoSpyBean
    private InvoiceService invoiceService;

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
}