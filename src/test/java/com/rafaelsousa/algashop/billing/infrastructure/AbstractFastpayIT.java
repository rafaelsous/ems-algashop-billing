package com.rafaelsousa.algashop.billing.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import com.rafaelsousa.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.Collections;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Import(FastpayCreditCardTokenizationApiClientConfig.class)
public abstract class AbstractFastpayIT {

    @Autowired
    protected FastpayCreditCardTokenizationApiClient tokenizationApiClient;

    @Autowired
    protected CreditCardProviderServiceFastpayImpl creditCardProvider;

    protected static final UUID VALID_CUSTOMER_ID = UUID.randomUUID();
    protected static final String ALWASYS_PAID_CARD_NUMBER = "4622943127011022";

    protected  static WireMockServer wireMockFastpay;

    public static void startMock() {
        wireMockFastpay = new WireMockServer(options()
                .port(8788)
                .usingFilesUnderDirectory("src/test/resources/wiremock/fastpay")
                .extensions(new ResponseTemplateTransformer(
                        TemplateEngine.defaultTemplateEngine(),
                        true,
                        new ClasspathFileSource("src/test/resources/wiremock/fastpay"),
                        Collections.emptyList()
                ))
        );

        wireMockFastpay.start();
    }

    public static void stopMock() {
        wireMockFastpay.stop();
    }

    protected LimitedCreditCard registerCard() {
        FastpayTokenizationInput tokenizationInput = FastpayTokenizationInput.builder()
                .number(ALWASYS_PAID_CARD_NUMBER)
                .cvv("333")
                .holderName("John Doe")
                .holderDocument("123234")
                .expMonth(7)
                .expYear(Year.now().plusYears(7).getValue())
                .build();

        FastpayTokenizedCreditCardModel response = tokenizationApiClient.tokenize(tokenizationInput);

        return creditCardProvider.register(VALID_CUSTOMER_ID, response.getTokenizedCard());
    }
}