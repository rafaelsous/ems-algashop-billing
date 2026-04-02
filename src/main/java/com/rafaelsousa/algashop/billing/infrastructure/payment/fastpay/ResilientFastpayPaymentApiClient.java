package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import com.rafaelsousa.algashop.billing.presentation.BadGatewayException;
import com.rafaelsousa.algashop.billing.presentation.GatewayTimeoutException;
import java.net.SocketTimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfig;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class ResilientFastpayPaymentApiClient {
	private final FastpayPaymentApiClient fastpayPaymentApiClient;
	private final FrameworkRetryCircuitBreaker circuitBreaker;

	public ResilientFastpayPaymentApiClient(CircuitBreakerFactory<FrameworkRetryConfig, FrameworkRetryConfigBuilder> circuitBreakerFactory,
	                                        FastpayPaymentApiClient fastpayPaymentApiClient) {
		this.fastpayPaymentApiClient = fastpayPaymentApiClient;
		this.circuitBreaker = (FrameworkRetryCircuitBreaker) circuitBreakerFactory.create("fastpayPaymentAPICB");
	}

	@ConcurrencyLimit(10)
	public FastpayPaymentResponse capture(FastpayPaymentInput fastpayPaymentInput) {
		log.info("Trying to capture payment on Fastpay Payment API");

		try {
			return circuitBreaker.run(() -> {
				try {
					return processCapture(fastpayPaymentInput);
				} catch (GatewayTimeoutException | BadGatewayException ex) {
					throw new FastpayPaymentCaptureFailed("Fail to capture payment of reference code %s"
							.formatted(fastpayPaymentInput.getReferenceCode()), ex);
				}
			});
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	@ConcurrencyLimit(10)
	public FastpayPaymentResponse findByCode(String gatewayCode) {
		log.info("Trying to find payment by code on Fastpay Payment API");

		try {
			return circuitBreaker.run(() -> processFindByCode(gatewayCode));
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	@ConcurrencyLimit(10)
	public void refund(String paymentId) {
		log.info("Trying to refund payment on Fastpay Payment API");

		try {
			circuitBreaker.run(() -> {
				processRefund(paymentId);
				
				return Void.TYPE;
			});
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	@ConcurrencyLimit(10)
	public void cancel(String paymentId) {
		log.info("Trying to cancel payment on Fastpay Payment API");

		try {
			circuitBreaker.run(() -> {
				processCancel(paymentId);
				
				return Void.TYPE;
			});
		} catch (NoFallbackAvailableException ex) {
			throw unwrapException(ex);
		}
	}

	private FastpayPaymentResponse processCapture(FastpayPaymentInput fastpayPaymentInput) {
		try {
			log.info("Capturing payment on Fastpay Payment API");

            return fastpayPaymentApiClient.capture(fastpayPaymentInput);
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private FastpayPaymentResponse processFindByCode(String gatewayCode) {
		try {
			log.info("Finding payment by code on Fastpay Payment API");
			return fastpayPaymentApiClient.findById(gatewayCode);
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private void processRefund(String paymentId) {
		try {
			log.info("Refunding payment on Fastpay Payment API");
			fastpayPaymentApiClient.refund(paymentId);
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private void processCancel(String paymentId) {
		try {
			log.info("Cancelling payment by code on Fastpay Payment API");
			fastpayPaymentApiClient.cancel(paymentId);
		} catch (RestClientException ex) {
			throw translateException(ex);
		}
	}

	private RuntimeException unwrapException(NoFallbackAvailableException ex) {
		if (ex.getCause() instanceof RetryException re) {
			if (re.getCause() instanceof GatewayTimeoutException gte) {
				return gte;
			}

			if (re.getCause() instanceof BadGatewayException bge) {
				return bge;
			}
		}

		return ex;
	}

	private RuntimeException translateException(RestClientException ex) {
		if (ex.getCause() instanceof SocketTimeoutException || ex instanceof ResourceAccessException) {
			return new GatewayTimeoutException("Rapidex API Timeout", ex);
		}

		if (ex instanceof HttpClientErrorException) {
			return new BadGatewayException.ClientErrorException("Rapidex API Bad Gateway", ex);
		}

		if (ex instanceof HttpServerErrorException) {
			return new BadGatewayException.ServerErrorException("Rapidex Bad Gateway", ex);
		}

		return new BadGatewayException("Rapidex Bad Gateway", ex);
	}
}