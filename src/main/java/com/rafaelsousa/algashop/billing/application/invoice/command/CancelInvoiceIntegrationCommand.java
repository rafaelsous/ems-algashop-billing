package com.rafaelsousa.algashop.billing.application.invoice.command;

import com.rafaelsousa.algashop.billing.application.InboundIntegrationCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelInvoiceIntegrationCommand implements InboundIntegrationCommand {

    @NotBlank
    private String orderId;
}
