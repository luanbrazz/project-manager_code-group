package com.portfolio.service.impl;

import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.repository.ProjectMemberRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public PortfolioReportResponse generatePortfolioReport() {
        return new PortfolioReportResponse(
                buildCountByStatus(),
                buildBudgetByStatus(),
                projectRepository.avgDurationDaysOfClosedProjects(),
                memberRepository.countDistinctMembers()
        );
    }

    private Map<ProjectStatus, Long> buildCountByStatus() {
        Map<ProjectStatus, Long> result = new EnumMap<>(ProjectStatus.class);
        List<Object[]> rows = projectRepository.countGroupedByStatus();
        for (Object[] row : rows) {
            result.put((ProjectStatus) row[0], (Long) row[1]);
        }
        return result;
    }

    private Map<ProjectStatus, BigDecimal> buildBudgetByStatus() {
        Map<ProjectStatus, BigDecimal> result = new EnumMap<>(ProjectStatus.class);
        List<Object[]> rows = projectRepository.sumBudgetGroupedByStatus();
        for (Object[] row : rows) {
            result.put((ProjectStatus) row[0], (BigDecimal) row[1]);
        }
        return result;
    }
}