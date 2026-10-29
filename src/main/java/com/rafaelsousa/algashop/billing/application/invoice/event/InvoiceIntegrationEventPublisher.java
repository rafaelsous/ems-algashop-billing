package com.rafaelsousa.algashop.billing.application.invoice.event;

import com.rafaelsousa.algashop.billing.application.OutboundIntegrationEvent;

public interface InvoiceIntegrationEventPublisher {
    void send(OutboundIntegrationEvent event);
}
