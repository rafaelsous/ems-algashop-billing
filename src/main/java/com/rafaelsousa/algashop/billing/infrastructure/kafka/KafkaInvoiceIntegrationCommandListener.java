package com.rafaelsousa.algashop.billing.infrastructure.kafka;

import com.rafaelsousa.algashop.billing.application.invoice.command.IssueInvoiceIntegrationCommand;

import com.rafaelsousa.algashop.billing.application.invoice.management.InvoiceManagementApplicationService;
import com.rafaelsousa.algashop.billing.application.invoice.management.IssueInvoiceInput;
import com.rafaelsousa.algashop.billing.application.invoice.management.IssueInvoiceInputAssembler;
import com.rafaelsousa.algashop.billing.infrastructure.persistence.reply.PendingReplies;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.invoiceCommandsConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.invoiceCommandsTopicName}")
public class KafkaInvoiceIntegrationCommandListener {
    private final PendingReplies pendingReplies;
    private final TransactionTemplate transactionTemplate;
    private final IssueInvoiceInputAssembler issueInvoiceInputAssembler;
    private final InvoiceManagementApplicationService invoiceManagementApplicationService;

    @KafkaHandler(isDefault = true)
    public void handle(
            @Payload Object command,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.warn(
                "Unsupported invoice command: type={} key={} offset={}",
                command.getClass().getSimpleName(),
                messageKey,
                offset);
        throw new IllegalArgumentException(
                "Unsupported invoice command type " + command.getClass().getSimpleName());
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid IssueInvoiceIntegrationCommand command,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.REPLY_TOPIC, required = false) byte[] replyTopic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        logReceived(command, messageKey, partition, offset);

        String sagaReplyTopic = requireHeader(replyTopic, KafkaHeaders.REPLY_TOPIC);
        String sagaCorrelationId = requireHeader(correlationId, KafkaHeaders.CORRELATION_ID);

        IssueInvoiceInput input = issueInvoiceInputAssembler.toIssueInvoiceInput(command);

        transactionTemplate.executeWithoutResult(_ -> {
            UUID invoiceId = invoiceManagementApplicationService.generate(input);
            pendingReplies.register(
                invoiceId,
                command.getOrderId(),
                sagaCorrelationId,
                sagaReplyTopic);
        });
    }

    private String requireHeader(byte[] value, String name) {
        if (value == null || value.length == 0) {
            throw new IllegalArgumentException("Invoice command without the " + name + " header");
        }

        return new String(value, StandardCharsets.UTF_8);
    }

    private void logReceived(Object command, String messageKey, Integer partition, Long offset) {
        log.info(
                "Received {} | key={} | partition={} | offset={} | thread={}",
                command.getClass().getSimpleName(),
                messageKey,
                partition,
                offset,
                Thread.currentThread().getName());
    }
}
