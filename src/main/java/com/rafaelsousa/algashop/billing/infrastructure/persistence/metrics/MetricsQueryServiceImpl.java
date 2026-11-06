package com.rafaelsousa.algashop.billing.infrastructure.persistence.metrics;

import com.rafaelsousa.algashop.billing.application.metrics.MetricsQueryService;
import com.rafaelsousa.algashop.billing.application.metrics.PaymentsOverviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MetricsQueryServiceImpl implements MetricsQueryService {
    private static final String SALES_OVERVIEW_SQL = """
        SELECT
              to_char(date_trunc('month', i.paid_at), 'yyyy-mm') AS year_month,
              ps.method AS payment_method,
              count(i.id) AS quantity,
              sum(i.total_amount) AS total_amount
          FROM
              invoice i
              JOIN payment_settings ps ON ps.id = i.payment_settings_id
          WHERE
              i.paid_at >= CAST(:startDate AS date)
              AND i.paid_at < CAST(:endDate AS date) + INTERVAL '1 day'
              AND i.status = 'PAID'
          GROUP BY
              1, 2
          ORDER BY
              1, 2
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<PaymentsOverviewOutput> getPaymentsOverview(YearMonth yearMonthStart, YearMonth yearMonthEnd) {
        return jdbcTemplate.query(SALES_OVERVIEW_SQL,
            Map.of("startDate", yearMonthStart.atDay(1), "endDate", yearMonthEnd.atEndOfMonth()),
            (rs, _) -> new PaymentsOverviewOutput(
                    YearMonth.parse(rs.getString("year_month")),
                    rs.getString("payment_method"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("total_amount")
            )
        );
    }
}
