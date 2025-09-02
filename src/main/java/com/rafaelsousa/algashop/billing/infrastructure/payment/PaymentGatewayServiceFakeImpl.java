package com.rafaelsousa.algashop.billing.infrastructure.payment;

import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentGatewayServiceFakeImpl implements PaymentGatewayService {

    @Override
    public Payment capture(PaymentRequest request) {
        return Payment.builder()
                .invoiceId(request.getInvoiceId())
                .method(request.getMethod())
                .gatewayCode(UUID.randomUUID().toString())
                .status(PaymentStatus.PAID)
                .build();
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return Payment.builder()
                .invoiceId(UUID.randomUUID())
                .method(PaymentMethod.GATEWAY_BALANCE)
                .gatewayCode(UUID.randomUUID().toString())
                .status(PaymentStatus.PAID)
                .build();
    }
}