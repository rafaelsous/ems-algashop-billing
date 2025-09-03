package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;

import java.util.UUID;

public class CreditCardNotFoundException extends DomainException {
    public CreditCardNotFoundException(UUID creditCardId) {
        super(ErrorMessages.ERROR_CREDIT_CARD_NOT_FOUND.formatted(creditCardId));
    }
}