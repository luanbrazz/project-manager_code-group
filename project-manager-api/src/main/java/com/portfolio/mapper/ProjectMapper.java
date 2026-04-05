package com.portfolio.mapper;

import com.portfolio.dto.response.ProjectMemberResponse;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.RiskLevel;
import com.portfolio.service.RiskCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final RiskCalculatorService riskCalculatorService;

    public ProjectResponse toResponse(Project project) {
        RiskLevel risk = riskCalculatorService.calculate(
                project.getBudget(),
                project.getStartDate(),
                project.getExpectedEndDate()
        );

        List<ProjectMemberResponse> members = project.getMembers()
                .stream()
                .map(this::toMemberResponse)
                .toList();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getExpectedEndDate(),
                project.getActualEndDate(),
                project.getBudget(),
                project.getDescription(),
                project.getManagerId(),
                project.getStatus(),
                risk,
                members,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public ProjectMemberResponse toMemberResponse(ProjectMember member) {
        return new ProjectMemberResponse(
                member.getId(),
                member.getMemberId(),
                member.getAllocatedAt()
        );
    }
}