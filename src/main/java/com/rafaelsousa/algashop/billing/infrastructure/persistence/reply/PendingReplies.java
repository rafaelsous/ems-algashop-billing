package com.rafaelsousa.algashop.billing.infrastructure.persistence.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PendingReplies {
	private final PendingReplyRepository pendingReplyRepository;

	@Transactional
	public void register(UUID invoiceId, String orderId, String correlationId, String replyTopic) {
		PendingReply pendingReply = new PendingReply(invoiceId, orderId, correlationId, replyTopic);
		pendingReplyRepository.save(pendingReply);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public Optional<PendingReply> findByInvoiceId(UUID invoiceId) {
		return pendingReplyRepository.findByInvoiceId(invoiceId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void remove(UUID invoiceId) {
		pendingReplyRepository.deleteByInvoiceId(invoiceId);
	}
}
