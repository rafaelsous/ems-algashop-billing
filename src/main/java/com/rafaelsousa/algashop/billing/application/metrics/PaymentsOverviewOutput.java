package com.rafaelsousa.algashop.billing.application.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsOverviewOutput {
	private YearMonth yearMonth;
	private String paymentMethod;
	private Integer quantity;
	private BigDecimal totalAmount;
}
