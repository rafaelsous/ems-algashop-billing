package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import com.rafaelsousa.algashop.billing.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;

import java.util.UUID;

public class CreditCardNotFoundException extends DomainEntityNotFoundException {
    public CreditCardNotFoundException(UUID creditCardId) {
        super(ErrorMessages.ERROR_CREDIT_CARD_NOT_FOUND.formatted(creditCardId));
    }
}