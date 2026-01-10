package com.rafaelsousa.algashop.billing.presentation.invoice;

import com.rafaelsousa.algashop.billing.application.invoice.management.InvoiceManagementApplicationService;
import com.rafaelsousa.algashop.billing.application.invoice.management.IssueInvoiceInput;
import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceOutput;
import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders/{orderId}/invoice")
public class InvoiceController {
    private final InvoiceQueryService invoiceQueryService;
    private final InvoiceManagementApplicationService invoiceManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceOutput issue(@PathVariable String orderId, @RequestBody @Valid IssueInvoiceInput input) {
        input.setOrderId(orderId);
        UUID invoiceId = invoiceManagementApplicationService.generate(input);

        try {
            invoiceManagementApplicationService.processPayment(invoiceId);
        } catch (Exception ex) {
            log.error("Error when process payment for invoice {}", invoiceId, ex);
        }

        return invoiceQueryService.findByOrderId(orderId);
    }

    @GetMapping
    public InvoiceOutput findByOrderId(@PathVariable String orderId) {
        return invoiceQueryService.findByOrderId(orderId);
    }
}