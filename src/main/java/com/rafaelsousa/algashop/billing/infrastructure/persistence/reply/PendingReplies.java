package com.rafaelsousa.algashop.billing.infrastructure.persistence.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PendingReplies {
	private final PendingReplyRepository pendingReplyRepository;

	public void register(UUID invoiceId, String orderId, String correlationId, String replyTopic) {
		PendingReply pendingReply = new PendingReply(invoiceId, orderId, correlationId, replyTopic);
		pendingReplyRepository.save(pendingReply);
	}
}
