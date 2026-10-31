package com.rafaelsousa.algashop.billing.application;

import lombok.Getter;

@Getter
public class ReplyPublishingException extends RuntimeException {

	public ReplyPublishingException(String message, OutboundIntegrationReply reply, Throwable ex) {
		super("%s Reply=%s AggregateId=%s".formatted(message, reply.getClass().getSimpleName(), reply.getAggregateId()), ex);
	}
}
