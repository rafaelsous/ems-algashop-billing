package com.rafaelsousa.algashop.billing.infrastructure.kafka;

import com.rafaelsousa.algashop.billing.application.invoice.management.InvoiceManagementApplicationService;
import com.rafaelsousa.algashop.billing.application.invoice.management.IssueInvoiceInput;
import com.rafaelsousa.algashop.billing.application.invoice.management.IssueInvoiceInputAssembler;
import com.rafaelsousa.algashop.billing.application.order.event.OrderPlacedIntegrationEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.orderEventsConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.orderEventTopicName}")
public class KafkaOrderIntegrationEventListener {
    private final IssueInvoiceInputAssembler issueInvoiceInputAssembler;
    private final InvoiceManagementApplicationService invoiceManagementApplicationService;

    @KafkaHandler(isDefault = true)
    public void handle(
            @Payload Object event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.info(
                "Event ignored: type={} key={} offset={}",
                event.getClass().getSimpleName(),
                messageKey,
                offset);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid OrderPlacedIntegrationEvent event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        logReceived(event, messageKey, partition, offset);

        IssueInvoiceInput issueInvoiceInput = issueInvoiceInputAssembler.toIssueInvoiceInput(event);
        invoiceManagementApplicationService.generate(issueInvoiceInput);
    }

    private void logReceived(Object event, String messageKey, Integer partition, Long offset) {
        log.info(
                "Received {} | key={} | partition={} | offset={} | thread={}",
                event.getClass().getSimpleName(),
                messageKey,
                partition,
                offset,
                Thread.currentThread().getName());
    }
}
