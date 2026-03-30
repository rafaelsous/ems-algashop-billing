package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import com.rafaelsousa.algashop.billing.presentation.BadGatewayException;

public class FastpayPaymentCaptureFailed extends BadGatewayException {
	public FastpayPaymentCaptureFailed() {
	}

	public FastpayPaymentCaptureFailed(String message) {
		super(message);
	}

	public FastpayPaymentCaptureFailed(String message, Throwable cause) {
		super(message, cause);
	}
}