package com.rafaelsousa.algashop.billing.application.invoice.command;

import com.rafaelsousa.algashop.billing.application.InboundIntegrationCommand;
import com.rafaelsousa.algashop.billing.application.order.snapshot.BillingSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.OrderItemSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.PaymentSnapshot;
import com.rafaelsousa.algashop.billing.application.order.snapshot.ShippingSnapshot;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInvoiceIntegrationCommand implements InboundIntegrationCommand {

    @NotBlank
    private String orderId;

    @NotNull
    private UUID customerId;

    @NotNull
    private OffsetDateTime placedAt;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @Valid
    @Builder.Default
    @NotNull @Size(min = 1)
    private List<OrderItemSnapshot> items = new ArrayList<>();

    @Valid
    @NotNull
    private PaymentSnapshot payment;

    @Valid
    @NotNull
    private ShippingSnapshot shipping;

    @Valid
    @NotNull
    private BillingSnapshot billing;
}
