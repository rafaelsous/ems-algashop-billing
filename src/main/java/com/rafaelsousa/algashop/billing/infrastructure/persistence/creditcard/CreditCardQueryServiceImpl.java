package com.rafaelsousa.algashop.billing.infrastructure.persistence.creditcard;

import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardQueryService;
import com.rafaelsousa.algashop.billing.application.utility.Mapper;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCardQueryServiceImpl implements CreditCardQueryService {
    private final CreditCardRepository creditCardRepository;
    private final Mapper mapper;

    @Override
    public CreditCardOutput findOne(UUID customerId, UUID creditCardId) {
        return creditCardRepository.findByCustomerIdAndId(customerId, creditCardId)
                .map(creditCard -> mapper.convert(creditCard, CreditCardOutput.class))
                .orElseThrow(() -> new CreditCardNotFoundException(creditCardId));
    }

    @Override
    public List<CreditCardOutput> findByCustomer(UUID customerId) {
        return creditCardRepository.findAllByCustomerId(customerId)
                .stream()
                .map(creditCard -> mapper.convert(creditCard, CreditCardOutput.class))
                .toList();
    }
}