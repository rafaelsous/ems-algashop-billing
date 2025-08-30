package com.rafaelsousa.algashop.billing.domain.model.commons;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
}