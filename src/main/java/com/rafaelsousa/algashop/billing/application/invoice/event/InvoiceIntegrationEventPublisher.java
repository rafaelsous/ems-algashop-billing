package com.rafaelsousa.algashop.billing.application.invoice.event;

import com.rafaelsousa.algashop.billing.application.IntegrationEvent;

public interface InvoiceIntegrationEventPublisher {
    void send(IntegrationEvent event);
}
