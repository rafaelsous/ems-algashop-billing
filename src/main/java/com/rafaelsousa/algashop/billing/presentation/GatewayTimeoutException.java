package com.rafaelsousa.algashop.billing.presentation;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException() {
    }

    public GatewayTimeoutException(String message) {
        super(message);
    }

    public GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
