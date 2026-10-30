package com.rafaelsousa.algashop.billing.application.invoice.management;

import com.rafaelsousa.algashop.billing.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.rafaelsousa.algashop.billing.application.order.snapshot.AddressSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.BillingSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.OrderItemSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.PaymentSnapshot;
import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class IssueInvoiceInputAssembler {

    private static final String SHIPPING_LINE_ITEM_NAME = "Shipping";

    public IssueInvoiceInput toIssueInvoiceInput(IssueInvoiceIntegrationCommand command) {
        PaymentSettingsInput paymentSettings = toPaymentSettings(command.getPayment());
        List<LineItemInput> lineItems = toLineItems(command);

        return IssueInvoiceInput.builder()
                .orderId(command.getOrderId())
                .customerId(command.getCustomerId())
                .paymentSettings(paymentSettings)
                .payer(toPayer(command.getBilling()))
                .items(lineItems)
                .build();
    }

    private PaymentSettingsInput toPaymentSettings(PaymentSnapshot payment) {
        PaymentMethod method = PaymentMethod.valueOf(payment.method());
        return PaymentSettingsInput.builder()
                .method(method)
                .creditCardId(payment.creditCardId())
                .build();
    }

    private List<LineItemInput> toLineItems(IssueInvoiceIntegrationCommand command) {
        List<LineItemInput> lineItems = new ArrayList<>(command.getItems().size() + 1);
        command.getItems().stream().map(this::toLineItem).forEach(lineItems::add);

        if (command.getShipping().cost().compareTo(BigDecimal.ZERO) > 0) {
            lineItems.add(
                    LineItemInput.builder()
                            .name(SHIPPING_LINE_ITEM_NAME)
                            .amount(command.getShipping().cost())
                            .build());
        }

        return lineItems;
    }

    private LineItemInput toLineItem(OrderItemSnapshot item) {
        return LineItemInput.builder()
                .name(item.productName() + " - x" + item.quantity())
                .amount(item.totalAmount())
                .build();
    }

    private PayerData toPayer(BillingSnapshot billing) {
        return PayerData.builder()
                .fullName("%s %s".formatted(billing.firstName(), billing.lastName()))
                .document(billing.document())
                .email(billing.email())
                .phone(billing.phone())
                .address(toPayerAddress(billing.address()))
                .build();
    }

    private AddressData toPayerAddress(AddressSnapshot address) {
        return AddressData.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode())
                .build();
    }
}
