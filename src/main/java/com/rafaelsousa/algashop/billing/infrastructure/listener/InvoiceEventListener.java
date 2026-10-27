package com.rafaelsousa.algashop.billing.infrastructure.listener;

import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceCanceledIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceIntegrationEventPublisher;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceIssuedIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoicePaidIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.utility.Mapper;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceCanceledEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoicePaidEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceEventListener {
    private final Mapper mapper;
    private final InvoiceIntegrationEventPublisher invoiceIntegrationEventPublisher;

    @Async
    @TransactionalEventListener
    public void listen(InvoiceIssuedEvent event) {
        InvoiceIssuedIntegrationEvent integrationEvent =
                mapper.convert(event, InvoiceIssuedIntegrationEvent.class);

        invoiceIntegrationEventPublisher.send(integrationEvent);
    }

    @Async
    @TransactionalEventListener
    public void listen(InvoicePaidEvent event) {
        InvoicePaidIntegrationEvent integrationEvent =
                mapper.convert(event, InvoicePaidIntegrationEvent.class);

        invoiceIntegrationEventPublisher.send(integrationEvent);
    }

    @Async
    @TransactionalEventListener
    public void listen(InvoiceCanceledEvent event) {
        InvoiceCanceledIntegrationEvent integrationEvent =
                mapper.convert(event, InvoiceCanceledIntegrationEvent.class);

        invoiceIntegrationEventPublisher.send(integrationEvent);
    }
}
