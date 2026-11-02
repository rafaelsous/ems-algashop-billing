package com.rafaelsousa.algashop.billing.infrastructure.kafka;

import com.rafaelsousa.algashop.billing.application.InboundIntegrationCommand;
import com.rafaelsousa.algashop.billing.application.invoice.command.CancelInvoiceIntegrationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaBeanValidationConfigurerTest {
	private LocalValidatorFactoryBean validator;
	private KafkaListenerEndpointRegistrar registrar;

@BeforeEach
	void setup() {
		validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		registrar = new KafkaListenerEndpointRegistrar();
		KafkaBeanValidationConfigurer kafkaBeanValidationConfigurer = new KafkaBeanValidationConfigurer(validator);
		kafkaBeanValidationConfigurer.configureKafkaListeners(registrar);
	}

	@Test
	void shouldRegisterValidator() {
		assertThat(registrar.getValidator()).isSameAs(validator);
	}

	@Test
	void shouldShowViolationWhenGivenInvalidPayload() {
		CancelInvoiceIntegrationCommand invalidCommand = new CancelInvoiceIntegrationCommand("");
		Errors errors = new BeanPropertyBindingResult(invalidCommand, "cancelInvoiceIntegrationCommand");

		validateCommandPayload(invalidCommand, errors);

		assertThat(errors).satisfies(errs -> {
			assertThat(errs.hasErrors()).isTrue();
			assertThat(errs.getFieldError("orderId")).isNotNull();
		});
	}

	@Test
	void shouldNotShowViolationWhenGivenValidPayload() {
		CancelInvoiceIntegrationCommand validCommand = new CancelInvoiceIntegrationCommand("0S3PK28SZDZ8K");
		Errors errors = new BeanPropertyBindingResult(validCommand, "cancelInvoiceIntegrationCommand");

		validateCommandPayload(validCommand, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	private void validateCommandPayload(InboundIntegrationCommand command, Errors errors) {
		assert registrar.getValidator() != null;
		registrar.getValidator().validate(command, errors);
	}
}
