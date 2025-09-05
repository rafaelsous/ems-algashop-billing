package com.rafaelsousa.algashop.billing.infrastructure.listeners;

import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceCanceledEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoicePaidEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoiceEventListener {

    @EventListener
    public void listen(InvoiceIssuedEvent event) {
        log.info("Invoice issued: {}", event.getInvoiceId());
    }

    @EventListener
    public void listen(InvoicePaidEvent event) {
        log.info("Invoice paid: {}", event.getInvoiceId());
    }

    @EventListener
    public void listen(InvoiceCanceledEvent event) {
        log.info("Invoice canceled: {}", event.getInvoiceId());
    }
}