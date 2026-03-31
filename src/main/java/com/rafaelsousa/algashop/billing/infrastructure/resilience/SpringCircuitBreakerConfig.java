package com.rafaelsousa.algashop.billing.infrastructure.resilience;

import com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay.FastpayPaymentCaptureFailed;
import com.rafaelsousa.algashop.billing.presentation.BadGatewayException;
import com.rafaelsousa.algashop.billing.presentation.GatewayTimeoutException;
import java.time.Duration;

import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;

@Configuration
public class SpringCircuitBreakerConfig {
	public static final String FASTPAY_PAYMENT_API_CB_ID = "fastpayPaymentAPICB";

	@Bean
	public Customizer<FrameworkRetryCircuitBreakerFactory> defaultCustomizer() {

        RetryPolicy retryPolicy = RetryPolicy.builder()
		        .maxRetries(3)
		        .multiplier(2)
		        .delay(Duration.ofSeconds(3))
		        .includes(GatewayTimeoutException.class, BadGatewayException.ServerErrorException.class)
		        .excludes(FastpayPaymentCaptureFailed.class)
		        .build();

	    return factory -> factory.configure(builder -> builder
			    .retryPolicy(retryPolicy)
			    .openTimeout(Duration.ofSeconds(30))
			    .resetTimeout(Duration.ofSeconds(60))
			    .build(), FASTPAY_PAYMENT_API_CB_ID
	    );
	}
}