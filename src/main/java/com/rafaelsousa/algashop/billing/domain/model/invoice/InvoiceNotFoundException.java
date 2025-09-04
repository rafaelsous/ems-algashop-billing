package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;

import java.util.UUID;

public class InvoiceNotFoundException extends DomainException {
    public InvoiceNotFoundException(UUID invoiceId) {
        super(ErrorMessages.ERROR_INVOICE_NOT_FOUND.formatted(invoiceId));
    }

    public InvoiceNotFoundException(String orderId) {
        super(ErrorMessages.ERROR_INVOICE_NOT_FOUND_FOR_ORDER_ID.formatted(orderId));
    }
}