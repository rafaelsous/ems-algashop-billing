package com.rafaelsousa.algashop.billing.infrastructure.listener;

import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceCanceledIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceIntegrationEventPublisher;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoiceIssuedIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.invoice.event.InvoicePaidIntegrationEvent;
import com.rafaelsousa.algashop.billing.application.invoice.reply.InvoiceCanceledIntegrationReply;
import com.rafaelsousa.algashop.billing.application.invoice.reply.InvoiceIntegrationReplyPublisher;
import com.rafaelsousa.algashop.billing.application.invoice.reply.InvoicePaidIntegrationReply;
import com.rafaelsousa.algashop.billing.application.utility.Mapper;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceCanceledEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoicePaidEvent;

import com.rafaelsousa.algashop.billing.infrastructure.persistence.reply.PendingReplies;
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
    private final PendingReplies pendingReplies;
    private final InvoiceIntegrationEventPublisher invoiceIntegrationEventPublisher;
    private final InvoiceIntegrationReplyPublisher invoiceIntegrationReplyPublisher;

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

        pendingReplies.findByInvoiceId(event.getInvoiceId()).ifPresent(pendingReply -> {
            invoiceIntegrationReplyPublisher.send(
                pendingReply.getReplyTopic(),
                pendingReply.getCorrelationId(),
                InvoicePaidIntegrationReply.builder()
                    .invoiceId(event.getInvoiceId())
                    .orderId(pendingReply.getOrderId())
                    .paidAt(event.getPaidAt())
                    .build()
            );

            pendingReplies.remove(event.getInvoiceId());
        });
    }

    @Async
    @TransactionalEventListener
    public void listen(InvoiceCanceledEvent event) {
        InvoiceCanceledIntegrationEvent integrationEvent =
                mapper.convert(event, InvoiceCanceledIntegrationEvent.class);

        invoiceIntegrationEventPublisher.send(integrationEvent);

        pendingReplies.findByInvoiceId(event.getInvoiceId()).ifPresent(pendingReply -> {
            invoiceIntegrationReplyPublisher.send(
                pendingReply.getReplyTopic(),
                pendingReply.getCorrelationId(),
                InvoiceCanceledIntegrationReply.builder()
                    .invoiceId(event.getInvoiceId())
                    .orderId(pendingReply.getOrderId())
                    .canceledAt(event.getCanceledAt())
                    .build()
            );

            pendingReplies.remove(event.getInvoiceId());
        });
    }
}
