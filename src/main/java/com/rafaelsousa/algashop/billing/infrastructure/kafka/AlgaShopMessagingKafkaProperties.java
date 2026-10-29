package com.rafaelsousa.algashop.billing.infrastructure.kafka;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties("algashop.messaging.kafka")
public class AlgaShopMessagingKafkaProperties {

    @NotBlank
    private String invoiceEventsTopicName;

    @NotBlank
    private String invoiceCommandsTopicName;

    @NotBlank
    private String invoiceEventsConsumerGroup;

    @NotBlank
    private String invoiceCommandsConsumerGroup;
}
