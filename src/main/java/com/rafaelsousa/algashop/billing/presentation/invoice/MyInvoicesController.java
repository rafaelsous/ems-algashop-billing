package com.rafaelsousa.algashop.billing.presentation.invoice;

import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceOutput;
import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.rafaelsousa.algashop.billing.application.security.SecurityChecks;
import com.rafaelsousa.algashop.billing.infrastructure.security.check.SecurityAnnotations.CanReadMyInvoices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/me/orders/{orderId}/invoice")
public class MyInvoicesController {
    private final InvoiceQueryService invoiceQueryService;
    private final SecurityChecks securityChecks;

    @GetMapping
    @CanReadMyInvoices
    public InvoiceOutput findByOrderId(@PathVariable String orderId) {
        return invoiceQueryService.findByOrderIdAndCustomerId(orderId, securityChecks.getAuthenticatedUserId());
    }
}
