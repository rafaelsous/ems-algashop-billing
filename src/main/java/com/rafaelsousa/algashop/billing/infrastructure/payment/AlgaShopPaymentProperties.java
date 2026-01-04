package com.rafaelsousa.algashop.billing.infrastructure.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties("algashop.integrations.payment")
public class AlgaShopPaymentProperties {

    @NotNull
    private AlgaShopPaymentProvider provider;

    @NotNull
    private @Valid FastpayProperties fastpay;

    public enum AlgaShopPaymentProvider {
        FAKE,
        FASTPAY,
    }

    @Data
    @Validated
    public static class FastpayProperties {

        @NotBlank
        private String hostname;

        @NotBlank
        private String privateToken;
    }
}