package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.commons.Address;
import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payer {
    private String fullName;
    private String document;
    private String phone;
    private String email;
    private Address address;
}