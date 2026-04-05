package com.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal budget,
        String description,
        Long managerId,
        ProjectStatus status,
        RiskLevel riskLevel,
        List<ProjectMemberResponse> members,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}