package com.rafaelsousa.algashop.billing.infrastructure.persistence.reply;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PendingReplyRepository extends JpaRepository<PendingReply, UUID> {
	Optional<PendingReply> findByInvoiceId(UUID invoiceId);
	void deleteByInvoiceId(UUID invoiceId);
}
