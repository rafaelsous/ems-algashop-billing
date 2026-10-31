package com.rafaelsousa.algashop.billing.infrastructure.kafka;

import com.rafaelsousa.algashop.billing.application.ReplyPublishingException;
import com.rafaelsousa.algashop.billing.application.OutboundIntegrationReply;
import com.rafaelsousa.algashop.billing.application.invoice.reply.InvoiceIntegrationReplyPublisher;
import com.rafaelsousa.algashop.billing.infrastructure.utility.BeanValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInvoiceIntegrationReplyPublisher implements InvoiceIntegrationReplyPublisher {
	private final BeanValidationUtil beanValidationUtil;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
    public void send(String replyChannel, String correlationId, OutboundIntegrationReply reply) {
		beanValidationUtil.validate(reply);
		Objects.requireNonNull(replyChannel);
		Objects.requireNonNull(correlationId);

		SendResult<String, Object> result;
		try {
			ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(replyChannel, reply.getAggregateId(), reply);
			producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));

			result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new ReplyPublishingException("Interrupted while publishing", reply, ex);
		} catch (TimeoutException | ExecutionException | KafkaException ex) {
			throw new ReplyPublishingException("Failed to publish reply", reply, ex);
		}

		RecordMetadata metadata = result.getRecordMetadata();

		log.info(
			"Published {} to {}-{} at offset {}",
			reply.getClass().getSimpleName(),
			metadata.topic(),
			metadata.partition(),
			metadata.offset());
	}
}
