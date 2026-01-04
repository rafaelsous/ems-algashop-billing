package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
public class PaymentGatewayServiceFastpayImpl implements PaymentGatewayService {

    @Override
    public Payment capture(PaymentRequest request) {
        return null;
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return null;
    }
}