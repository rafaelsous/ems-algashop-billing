package com.rafaelsousa.algashop.billing.presentation.creditcard;

import static com.rafaelsousa.algashop.billing.infrastructure.security.check.SecurityAnnotations.*;

import com.rafaelsousa.algashop.billing.application.creditcard.management.CreditCardManagementeApplicationService;
import com.rafaelsousa.algashop.billing.application.creditcard.management.TokenizedCreditCardInput;
import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.rafaelsousa.algashop.billing.application.creditcard.query.CreditCardQueryService;
import com.rafaelsousa.algashop.billing.application.security.SecurityChecks;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/me/credit-cards")
public class MyCreditCardsController {
    private final CreditCardQueryService creditCardQueryService;
    private final CreditCardManagementeApplicationService creditCardManagementeApplicationService;
    private final SecurityChecks securityChecks;

    @GetMapping
    @CanReadMyCreditCards
    public List<CreditCardOutput> findByCustomerId() {
        return creditCardQueryService.findByCustomer(securityChecks.getAuthenticatedUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteMyCreditCards
    public CreditCardOutput create(@RequestBody @Valid TokenizedCreditCardInput input) {
        UUID authenticatedUserId = securityChecks.getAuthenticatedUserId();
        input.setCustomerId(authenticatedUserId);
        UUID creditCardId = creditCardManagementeApplicationService.register(input);

        return creditCardQueryService.findOne(authenticatedUserId, creditCardId);
    }

    @GetMapping("/{creditCardId}")
    @CanReadMyCreditCards
    public CreditCardOutput findById(@PathVariable UUID creditCardId) {
        return creditCardQueryService.findOne(
                securityChecks.getAuthenticatedUserId(), creditCardId);
    }

    @DeleteMapping("/{creditCardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteMyCreditCards
    public void delete(@PathVariable UUID creditCardId) {
        creditCardManagementeApplicationService.delete(
                securityChecks.getAuthenticatedUserId(), creditCardId);
    }
}
