package com.rafaelsousa.algashop.billing.application.invoice.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItemInput {
	private String name;
	private BigDecimal amount;
	private Integer quantity;
}