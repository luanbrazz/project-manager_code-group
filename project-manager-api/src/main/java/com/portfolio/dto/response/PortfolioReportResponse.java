package com.portfolio.dto.response;

import com.portfolio.enums.ProjectStatus;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
        Map<ProjectStatus, Long> projectCountByStatus,
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus,
        Double avgDurationDaysClosedProjects,
        Long totalUniqueMembersAllocated
) {
}