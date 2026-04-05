package com.portfolio.service;

import com.portfolio.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RiskCalculatorService {

    RiskLevel calculate(BigDecimal budget, LocalDate startDate, LocalDate expectedEndDate);
}