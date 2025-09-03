package com.rafaelsousa.algashop.billing.application.invoice.management;

import com.rafaelsousa.algashop.billing.domain.model.commons.Address;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.invoice.*;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceManagementeApplicationService {
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Transactional
    public UUID generate(IssueInvoiceInput issueInvoiceInput) {
        PaymentSettingsInput paymentSettings = issueInvoiceInput.getPaymentSettings();
        verifyCreditCartId(paymentSettings.getCreditCardId());

        Payer payer = convertToPayer(issueInvoiceInput.getPayer());
        Set<LineItem> items = convertToLineItems(issueInvoiceInput.getItems());

        Invoice invoice = invoiceService.issue(issueInvoiceInput.getOrderId(),
                issueInvoiceInput.getCustomerId(), payer, items);
        invoice.changePaymentSettings(paymentSettings.getMethod(), paymentSettings.getCreditCardId());

        invoiceRepository.saveAndFlush(invoice);

        return invoice.getId();
    }

    private Set<LineItem> convertToLineItems(Set<LineItemInput> itemInputs) {
        Set<LineItem> lineItems = new LinkedHashSet<>();

        int itemNumber = 1;
        for (LineItemInput itemInput : itemInputs) {
            lineItems.add(LineItem.builder()
                    .number(itemNumber)
                    .name(itemInput.getName())
                    .amount(itemInput.getAmount())
                    .build());

            itemNumber++;
        }

        return lineItems;
    }

    private Payer convertToPayer(PayerData payerData) {
        AddressData addressData = payerData.getAddress();

        return Payer.builder()
                .fullName(payerData.getFullName())
                .document(payerData.getDocument())
                .email(payerData.getEmail())
                .phone(payerData.getPhone())
                .address(Address.builder()
                        .street(addressData.getStreet())
                        .number(addressData.getNumber())
                        .neighborhood(addressData.getNeighborhood())
                        .complement(addressData.getComplement())
                        .city(addressData.getCity())
                        .state(addressData.getState())
                        .zipCode(addressData.getZipCode())
                        .build())
                .build();
    }

    private void verifyCreditCartId(UUID creditCardId) {
        if (Objects.nonNull(creditCardId) && !creditCardRepository.existsById(creditCardId)) {
            throw new CreditCardNotFoundException(creditCardId);
        }
    }
}