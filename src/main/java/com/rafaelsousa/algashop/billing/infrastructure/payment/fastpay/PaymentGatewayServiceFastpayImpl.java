package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import com.rafaelsousa.algashop.billing.domain.model.commons.Address;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCard;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.rafaelsousa.algashop.billing.domain.model.invoice.Payer;
import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.Payment;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.rafaelsousa.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import com.rafaelsousa.algashop.billing.infrastructure.payment.AlgaShopPaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
public class PaymentGatewayServiceFastpayImpl implements PaymentGatewayService {
    private final FastpayPaymentApiClient fastpayPaymentApiClient;
    private final CreditCardRepository creditCardRepository;
    private final AlgaShopPaymentProperties algaShopPaymentProperties;

    @Override
    public Payment capture(PaymentRequest request) {
        FastpayPaymentInput input = convertToInput(request);
        FastpayPaymentResponse response = fastpayPaymentApiClient.capture(input);

        return convertToPayment(response);
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        FastpayPaymentResponse response = fastpayPaymentApiClient.findById(gatewayCode);

        return convertToPayment(response);
    }

    private FastpayPaymentInput convertToInput(PaymentRequest request) {
        Payer payer = request.getPayer();
        Address address = payer.getAddress();

        var builder = FastpayPaymentInput.builder()
                .totalAmount(request.getAmount())
                .fullName(payer.getFullName())
                .document(payer.getDocument())
                .phone(payer.getPhone())
                .addressLine1(address.getStreet().concat(", ").concat(address.getNumber()))
                .addressLine2(address.getComplement())
                .referenceCode(request.getInvoiceId().toString())
                .replyToUrl(algaShopPaymentProperties.getFastpay().getWebhookUrl())
                .zipCode(address.getZipCode());

        if (Objects.requireNonNull(request.getMethod()) == PaymentMethod.CREDIT_CARD) {
            builder.method(FastpayPaymentMethod.CREDIT.name());
            CreditCard creditCard = creditCardRepository.findById(request.getCreditCardId())
                    .orElseThrow(() -> new CreditCardNotFoundException(request.getCreditCardId()));

            builder.creditCardId(creditCard.getGatewayCode());
        } else if (request.getMethod() == PaymentMethod.GATEWAY_BALANCE) {
            builder.method(FastpayPaymentMethod.GATEWAY_BALANCE.name());
        }

        return builder.build();
    }

    private Payment convertToPayment(FastpayPaymentResponse response) {
        var builder = Payment.builder()
                .gatewayCode(response.getId())
                .invoiceId(UUID.fromString(response.getReferenceCode()));

        FastpayPaymentMethod fastpayPaymentMethod;
        try {
            fastpayPaymentMethod = FastpayPaymentMethod.valueOf(response.getMethod());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unknown payment method: " + response.getMethod());
        }

        FastpayPaymentStatus fastpayPaymentStatus;
        try {
            fastpayPaymentStatus = FastpayPaymentStatus.valueOf(response.getStatus());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unknown payment status: " + response.getStatus());
        }

        PaymentMethod paymentMethod = FastpayEnumConverter.convert(fastpayPaymentMethod);
        PaymentStatus paymentStatus = FastpayEnumConverter.convert(fastpayPaymentStatus);

        builder.method(paymentMethod);
        builder.status(paymentStatus);

        return builder.build();
    }
}