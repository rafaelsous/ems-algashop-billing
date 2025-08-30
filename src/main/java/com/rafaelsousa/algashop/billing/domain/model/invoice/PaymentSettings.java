package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.billing.domain.model.IdGenerator;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentSettings {

    @EqualsAndHashCode.Include
    private UUID id;
    private UUID creditCardId;
    private String gatewayCode;
    private PaymentMethod method;

    static PaymentSettings brandNew(PaymentMethod paymentMethod, UUID creditCardId) {
        Objects.requireNonNull(paymentMethod);

        if (PaymentMethod.CREDIT_CARD.equals(paymentMethod)) {
            Objects.requireNonNull(creditCardId);
        }

        return new PaymentSettings(
                IdGenerator.generateTimeBasedUUID(),
                creditCardId,
                null,
                paymentMethod
        );
    }

    void assignGatewayCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_PAYMENT_SETTINGS_GATEWAY_CODE_CANNOT_BE_EMPTY);
        }

        if (StringUtils.hasText(this.gatewayCode)) {
            throw new IllegalStateException(ErrorMessages.ERROR_PAYMENT_SETTINGS_GATEWAY_CODE_ALREADY_ASSIGNED);
        }

        this.setGatewayCode(code);
    }
}