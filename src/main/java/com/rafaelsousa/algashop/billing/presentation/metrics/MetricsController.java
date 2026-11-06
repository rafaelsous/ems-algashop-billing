package com.rafaelsousa.algashop.billing.presentation.metrics;

import com.rafaelsousa.algashop.billing.application.metrics.MetricsQueryService;
import com.rafaelsousa.algashop.billing.application.metrics.PaymentsOverviewOutput;
import com.rafaelsousa.algashop.billing.infrastructure.security.check.SecurityAnnotations.CanReadMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
public class MetricsController {
	private final MetricsQueryService metricsQueryService;

	@CanReadMetrics
	@GetMapping("/payments-overview")
	public List<PaymentsOverviewOutput> getPaymentsOverview(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonthStart,
	                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonthEnd) {
		if (yearMonthStart == null) {
			yearMonthStart = Year.now().atMonth(1);
		}

		if (yearMonthEnd == null) {
			yearMonthEnd = Year.now().atMonth(12);
		}

		return metricsQueryService.getPaymentsOverview(yearMonthStart, yearMonthEnd);
	}
}
