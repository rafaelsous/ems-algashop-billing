package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import com.rafaelsousa.algashop.billing.domain.model.IdGenerator;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditCard {

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
            throw new IllegalArgumentException("Last numbers cannot be empty");
        }

        if (!StringUtils.hasText(brand)) {
            throw new IllegalArgumentException("Brand cannot be empty");
        }

        if (expMonth < 1 || expMonth > 12) {
            throw new IllegalArgumentException("Invalid expiration month");
        }

        if (expYear < OffsetDateTime.now().getYear()) {
            throw new IllegalArgumentException("Invalid expiration year");
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
            throw new IllegalArgumentException("Gateway code cannot be empty");
        }

        this.gatewayCode = gatewayCode;
    }
}