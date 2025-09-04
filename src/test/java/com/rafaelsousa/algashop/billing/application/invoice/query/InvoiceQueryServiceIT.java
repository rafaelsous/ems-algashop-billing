package com.rafaelsousa.algashop.billing.application.invoice.query;

import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.billing.domain.model.invoice.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class InvoiceQueryServiceIT {
    private final InvoiceQueryService invoiceQueryService;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceQueryServiceIT(InvoiceQueryService invoiceQueryService, InvoiceRepository invoiceRepository) {
        this.invoiceQueryService = invoiceQueryService;
        this.invoiceRepository = invoiceRepository;
    }

    @Test
    void shoudFindByOrderId() {
        InvoiceTestDataBuilder invoiceTestDataBuilder = InvoiceTestDataBuilder.anInvoice();
        Invoice invoice = invoiceTestDataBuilder
                .paymentSettings(PaymentMethod.GATEWAY_BALANCE, null)
                .build();
        invoiceRepository.saveAndFlush(invoice);

        InvoiceOutput invoiceOutput = invoiceQueryService.findByOrderId(invoice.getOrderId());

        assertThat(invoiceOutput).satisfies(io -> {
            assertThat(io.getId()).isEqualTo(invoice.getId());
            assertThat(io.getOrderId()).isEqualTo(invoice.getOrderId());
            assertThat(io.getCustomerId().toString()).hasToString(invoice.getCustomerId());
            assertThat(io.getTotalAmount()).isEqualTo(invoice.getTotalAmount());
        });

    }

    @Test
    void shouldThrowExceptionWhenTryingFindByOrderIdNonExistentInvoice() {
        String orderId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> invoiceQueryService.findByOrderId(orderId))
                .isInstanceOf(InvoiceNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_INVOICE_NOT_FOUND_FOR_ORDER_ID.formatted(orderId));
    }
}