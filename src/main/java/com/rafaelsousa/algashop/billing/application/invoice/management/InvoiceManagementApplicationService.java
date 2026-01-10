package com.rafaelsousa.algashop.billing.application.invoice.management;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.commons.Address;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.invoice.*;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceManagementApplicationService {
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Transactional
    public UUID generate(IssueInvoiceInput issueInvoiceInput) {
        PaymentSettingsInput paymentSettings = issueInvoiceInput.getPaymentSettings();

        if (paymentSettings.getMethod().equals(PaymentMethod.CREDIT_CARD)) {
            verifyCreditCartId(issueInvoiceInput.getCustomerId(), paymentSettings.getCreditCardId());
        }

        Payer payer = convertToPayer(issueInvoiceInput.getPayer());
        Set<LineItem> items = convertToLineItems(issueInvoiceInput.getItems());

        Invoice invoice = invoiceService.issue(issueInvoiceInput.getOrderId(),
                issueInvoiceInput.getCustomerId(), payer, items);
        invoice.changePaymentSettings(paymentSettings.getMethod(), paymentSettings.getCreditCardId());

        invoiceRepository.saveAndFlush(invoice);

        return invoice.getId();
    }

    @Transactional
    public void processPayment(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        PaymentRequest paymentRequest = toPaymentRequest(invoice);

        Payment payment;
        try {
            payment = paymentGatewayService.capture(paymentRequest);
        } catch (Exception ex) {
            String errorMessage = "Payment capture failed for invoice %s".formatted(invoiceId);
            log.error(errorMessage, ex);

            invoice.cancel(errorMessage);
            invoiceRepository.saveAndFlush(invoice);

            return;
        }

        invoiceService.assignPayment(invoice, payment);
        invoiceRepository.saveAndFlush(invoice);
    }

    @Transactional
    public void updatePaymentStatus(UUID invoiceId, PaymentStatus paymentStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        invoice.updatePaymentStatus(paymentStatus);
        invoiceRepository.saveAndFlush(invoice);
    }

    private PaymentRequest toPaymentRequest(Invoice invoice) {
        return PaymentRequest.builder()
                .invoiceId(invoice.getId())
                .payer(invoice.getPayer())
                .method(invoice.getPaymentSettings().getMethod())
                .amount(invoice.getTotalAmount())
                .creditCardId(invoice.getPaymentSettings().getCreditCardId())
                .build();
    }

    private Set<LineItem> convertToLineItems(List<LineItemInput> itemInputs) {
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

    private void verifyCreditCartId(UUID customerId, UUID creditCardId) {
        if (Objects.isNull(creditCardId)) {
            throw new DomainException("Credit card ID is required");
        }

        if (!creditCardRepository.existsByIdAndCustomerId(creditCardId, customerId)) {
            throw new CreditCardNotFoundException(creditCardId);
        }
    }
}