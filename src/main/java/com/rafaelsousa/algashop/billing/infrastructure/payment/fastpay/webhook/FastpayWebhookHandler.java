package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay.webhook;

import com.rafaelsousa.algashop.billing.application.invoice.management.InvoiceManagementeApplicationService;
import com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay.FastpayEnumConverter;
import com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay.FastpayPaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastpayWebhookHandler {
    private final InvoiceManagementeApplicationService invoiceManagementeApplicationService;

    public void process(FastpayPaymentWebhookEvent event) {
        log.info("Processing webhook event {}", event);

        invoiceManagementeApplicationService.updatePaymentStatus(
                UUID.fromString(event.getReferenceCode()),
                FastpayEnumConverter.convert(FastpayPaymentStatus.valueOf(event.getStatus()))
        );
    }
}