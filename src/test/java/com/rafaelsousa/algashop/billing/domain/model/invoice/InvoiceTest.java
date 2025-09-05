package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceTest {

    @Test
    void shouldIssueInvoice() {
        String orderId = "123";
        UUID customerId = UUID.randomUUID();
        Payer payer = InvoiceTestDataBuilder.aPayer();
        Set<LineItem> items = Set.of(
                InvoiceTestDataBuilder.aLineItem(),
                InvoiceTestDataBuilder.aLineItemAlt()
        );
        Invoice invoice = Invoice.issue(orderId, customerId, payer, items);

        BigDecimal expectedTotalAmount = invoice.getItems().stream()
                .map(LineItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getId()).isNotNull();
            assertThat(i.getOrderId()).isNotBlank();
            assertThat(i.getCustomerId()).isNotNull();
            assertThat(i.getIssuedAt()).isNotNull();
            assertThat(i.getPaidAt()).isNull();
            assertThat(i.getCanceledAt()).isNull();
            assertThat(i.getCancelReason()).isNull();
            assertThat(i.getExpiresAt()).isNotNull();
            assertThat(i.getTotalAmount()).isEqualTo(expectedTotalAmount);
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
            assertThat(i.getPaymentSettings()).isNull();
            assertThat(i.getPayer()).isNotNull();
            assertThat(i.getItems()).isNotEmpty();
            assertThat(i.getItems()).hasSize(2);
        });
    }

    @Test
    void shouldMarkInvoiceAsPaid() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
            assertThat(i.isPaid()).isFalse();
            assertThat(i.getPaidAt()).isNull();
            assertThat(i.isUnpaid()).isTrue();
        });

        invoice.markAsPaid();

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.PAID);
            assertThat(i.getPaidAt()).isNotNull();
            assertThat(i.isPaid()).isTrue();
        });
    }

    @Test
    void shouldCancelInvoiceWithReason() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .items(Set.of(InvoiceTestDataBuilder.aLineItem()))
                .cancelReason(null)
                .build();

        invoice.cancel("Cancellation reason");

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getStatus()).isEqualTo(InvoiceStatus.CANCELED);
            assertThat(i.getCanceledAt()).isNotNull();
            assertThat(i.getCancelReason()).isNotBlank();
        });
    }

    @Test
    void shouldChangePaymentSettings() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .items(InvoiceTestDataBuilder.aLineItem(), InvoiceTestDataBuilder.aLineItemAlt())
                .build();
        UUID creditCardId = UUID.randomUUID();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        invoice.changePaymentSettings(paymentMethod, creditCardId);

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getPaymentSettings()).isNotNull();
            assertThat(i.getPaymentSettings().getMethod()).isEqualTo(paymentMethod);
            assertThat(i.getPaymentSettings().getCreditCardId()).isEqualTo(creditCardId);
        });
    }

    @Test
    void shouldAssignPaymentSettings() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .paymentSettings(PaymentMethod.CREDIT_CARD, UUID.randomUUID())
                .build();
        String paymentGatewayCode = "123";

        invoice.assignPaymentGatewayCode(paymentGatewayCode);

        assertThat(invoice).satisfies(i -> {
            assertThat(i.getPaymentSettings()).isNotNull();
            assertThat(i.getPaymentSettings().getGatewayCode()).isEqualTo(paymentGatewayCode);
        });
    }

    @Test
    void shouldThrowExceptionWhenTryingIssueWithInvalidOrderId() {
        UUID customerId = UUID.randomUUID();
        Payer payer = InvoiceTestDataBuilder.aPayer();
        Set<LineItem> items = Set.of(
                InvoiceTestDataBuilder.aLineItem(),
                InvoiceTestDataBuilder.aLineItemAlt()
        );

        assertThatThrownBy(() -> Invoice.issue("", customerId, payer, items))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_ORDER_ID_CANNOT_BE_EMPTY);
    }

    @Test
    void shouldThrowExceptionWhenTryingIssueWithEmptyItems() {
        String orderId = "123";
        UUID customerId = UUID.randomUUID();
        Payer payer = InvoiceTestDataBuilder.aPayer();
        Set<LineItem> emptyItems = Set.of();

        assertThatThrownBy(() -> Invoice.issue(orderId, customerId, payer, emptyItems))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_MUST_HAVE_AT_LEAST_ONE_ITEM);
    }

    @Test
    void shouldThrowExceptionWhenTryingMarkAlreadyPaidInvoiceAsPaid() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.PAID)
                .build();

        assertThatThrownBy(invoice::markAsPaid)
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_CANNOT_BE_MARKED_AS_PAID
                        .formatted(invoice.getId(), invoice.getStatus().name().toLowerCase()));
    }

    @Test
    void shouldThrowExceptionWhenTryingCancelAlreadyCanceledInvoice() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .build();

        assertThatThrownBy(() -> invoice.cancel("Cancellation reason"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_IS_ALREADY_CANCELED.formatted(invoice.getId()));
    }

    @Test
    void shouldThrowExceptionWhenTryingAssignPaymentGatewayCodeOfAnUnpaidInvoice() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .build();

        assertThatThrownBy(() -> invoice.assignPaymentGatewayCode("123"))
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
                        .formatted(invoice.getId(), invoice.getStatus().name().toLowerCase()));
    }

    @Test
    void shouldThrowExceptionWhenTryingChangePaymentSettingOfAnUnpaidInvoice() {
        UUID creditCardId = UUID.randomUUID();
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .build();

        assertThatThrownBy(() -> invoice.changePaymentSettings(PaymentMethod.CREDIT_CARD, creditCardId))
                .isInstanceOf(DomainException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
                        .formatted(invoice.getId(), invoice.getStatus().name().toLowerCase()));
    }
}