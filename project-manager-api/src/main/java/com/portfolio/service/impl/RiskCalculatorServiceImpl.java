package com.portfolio.service.impl;

import com.portfolio.enums.RiskLevel;
import com.portfolio.service.RiskCalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class RiskCalculatorServiceImpl implements RiskCalculatorService {

    private static final BigDecimal LOW_BUDGET_LIMIT = new BigDecimal("100000");
    private static final BigDecimal HIGH_BUDGET_LIMIT = new BigDecimal("500000");
    private static final long LOW_MONTHS_LIMIT = 3L;
    private static final long HIGH_MONTHS_LIMIT = 6L;

    public RiskLevel calculate(BigDecimal budget, LocalDate startDate, LocalDate expectedEndDate) {
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);

        if (isHighRisk(budget, months)) return RiskLevel.ALTO;
        if (isMediumRisk(budget, months)) return RiskLevel.MEDIO;
        return RiskLevel.BAIXO;
    }

    private boolean isHighRisk(BigDecimal budget, long months) {
        return budget.compareTo(HIGH_BUDGET_LIMIT) > 0 || months > HIGH_MONTHS_LIMIT;
    }

    private boolean isMediumRisk(BigDecimal budget, long months) {
        boolean budgetMedium = budget.compareTo(LOW_BUDGET_LIMIT) > 0
                && budget.compareTo(HIGH_BUDGET_LIMIT) <= 0;
        boolean monthsMedium = months > LOW_MONTHS_LIMIT && months <= HIGH_MONTHS_LIMIT;
        return budgetMedium || monthsMedium;
    }
}