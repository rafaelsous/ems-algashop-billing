package com.rafaelsousa.algashop.billing.application.invoice.reply;

import com.rafaelsousa.algashop.billing.application.OutboundIntegrationReply;

public interface InvoiceIntegrationReplyPublisher {
	void send(String replyChannel, String correlationId, OutboundIntegrationReply reply);
}
