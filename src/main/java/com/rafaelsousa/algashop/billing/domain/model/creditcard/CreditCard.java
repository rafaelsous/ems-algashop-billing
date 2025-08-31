package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.billing.domain.model.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CreditCard {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID customerId;
    private OffsetDateTime createdAt;
    private String lastNumbers;
    private String brand;
    private Integer expMonth;
    private Integer expYear;
    private String gatewayCode;

    public static CreditCard brandNew(UUID customerId, String lastNumbers, String brand,
                                      Integer expMonth, Integer expYear, String gatewayCreditCardCode) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(expMonth);
        Objects.requireNonNull(expYear);

        if (!StringUtils.hasText(lastNumbers)) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_CREDIT_CARD_LAST_NUMBERS_CANNOT_BE_EMPTY);
        }

        if (!StringUtils.hasText(brand)) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_CREDIT_CARD_BRAND_CANNOT_BE_EMPTY);
        }

        if (expMonth < 1 || expMonth > 12) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_CREDIT_CARD_INVALID_EXPIRATION_MONTH);
        }

        if (expYear < OffsetDateTime.now().getYear()) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_CREDIT_CARD_INVALID_EXPIRATION_YEAR);
        }

        return new CreditCard(
                IdGenerator.generateTimeBasedUUID(),
                customerId,
                OffsetDateTime.now(),
                lastNumbers,
                brand,
                expMonth,
                expYear,
                gatewayCreditCardCode
        );
    }

    public void setGatewayCode(String gatewayCode) {
        if (!StringUtils.hasText(gatewayCode)) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_CREDIT_CARD_GATEWAY_CODE_CANNOT_BE_EMPTY);
        }

        this.gatewayCode = gatewayCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}