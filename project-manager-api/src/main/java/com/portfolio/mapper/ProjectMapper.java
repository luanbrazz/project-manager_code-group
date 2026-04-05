package com.portfolio.mapper;

import com.portfolio.dto.response.ProjectMemberResponse;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.RiskLevel;
import com.portfolio.service.RiskCalculatorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final ModelMapper modelMapper;
    private final RiskCalculatorService riskCalculatorService;

    public ProjectResponse toResponse(Project project) {
        ProjectResponse base = modelMapper.map(project, ProjectResponse.class);

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
                base.id(),
                base.name(),
                base.startDate(),
                base.expectedEndDate(),
                base.actualEndDate(),
                base.budget(),
                base.description(),
                base.managerId(),
                base.status(),
                risk,
                members,
                base.createdAt(),
                base.updatedAt()
        );
    }

    public ProjectMemberResponse toMemberResponse(ProjectMember member) {
        return modelMapper.map(member, ProjectMemberResponse.class);
    }
}