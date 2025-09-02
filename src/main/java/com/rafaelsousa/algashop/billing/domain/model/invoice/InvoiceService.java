package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public Invoice issue(String orderId, UUID customerId, Payer payer, Set<LineItem> items) {
        if (invoiceRepository.existsByOrderId(orderId)) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_ALREADY_EXISTS_FOR_ORDER.formatted(orderId));
        }

        return Invoice.issue(orderId, customerId, payer, items);
    }

    public void assignPayment(Invoice invoice, Payment payment) {
        invoice.assignPaymentGatewayCode(payment.getGatewayCode());

        switch (payment.getStatus()) {
            case FAILED -> invoice.cancel("Payment failed");
            case REFUNDED -> invoice.cancel("Payment refunded");
            case PAID -> invoice.markAsPaid();

            default -> throw new IllegalStateException("Unexpected value: " + payment.getStatus());
        }
    }
}