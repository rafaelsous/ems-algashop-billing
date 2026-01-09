package com.rafaelsousa.algashop.billing.presentation.creditcard;

import com.rafaelsousa.algashop.billing.application.creditcard.management.CreditCardManagementeApplicationService;
import com.rafaelsousa.algashop.billing.application.creditcard.management.TokenizedCreditCardInput;
import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/{customerId}/credit-cards")
public class CreditCardController {
    private final CreditCardQueryService creditCardQueryService;
    private final CreditCardManagementeApplicationService creditCardManagementeApplicationService;

    @GetMapping
    public List<CreditCardOutput> findByCustomerId(@PathVariable UUID customerId) {
        return creditCardQueryService.findByCustomer(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardOutput create(@PathVariable UUID customerId, @RequestBody @Valid TokenizedCreditCardInput input) {
        input.setCustomerId(customerId);
        UUID creditCardId = creditCardManagementeApplicationService.register(input);

        return creditCardQueryService.findOne(customerId, creditCardId);
    }

    @GetMapping("/{creditCardId}")
    public CreditCardOutput findById(@PathVariable UUID customerId, @PathVariable UUID creditCardId) {
        return creditCardQueryService.findOne(customerId, creditCardId);
    }

    @DeleteMapping("/{creditCardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID customerId, @PathVariable UUID creditCardId) {
        creditCardManagementeApplicationService.delete(customerId, creditCardId);
    }
}