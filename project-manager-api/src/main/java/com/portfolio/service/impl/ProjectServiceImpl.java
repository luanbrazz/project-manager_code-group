package com.portfolio.service.impl;

import com.portfolio.client.MemberClientService;
import com.portfolio.dto.request.MemberAllocationRequest;
import com.portfolio.dto.request.ProjectCreateRequest;
import com.portfolio.dto.request.ProjectUpdateRequest;
import com.portfolio.dto.request.StatusChangeRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.exception.BusinessException;
import com.portfolio.exception.MemberAllocationException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.exception.StatusTransitionException;
import com.portfolio.mapper.ProjectMapper;
import com.portfolio.repository.ProjectMemberRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.repository.ProjectSpecification;
import com.portfolio.service.ProjectService;
import com.portfolio.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private static final int MAX_MEMBERS_PER_PROJECT = 10;
    private static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectMapper projectMapper;
    private final MemberClientService memberClientService;

    @Override
    @Transactional
    public ProjectResponse create(ProjectCreateRequest req) {
        validateMemberExists(req.managerId());

        Project project = Project.builder()
                .name(req.name())
                .startDate(req.startDate())
                .expectedEndDate(req.expectedEndDate())
                .actualEndDate(req.actualEndDate())
                .budget(req.budget())
                .description(req.description())
                .managerId(req.managerId())
                .status(ProjectStatus.EM_ANALISE)
                .build();

        Project saved = projectRepository.save(project);
        log.info("Projeto criado: id={}, nome={}", saved.getId(), saved.getName());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(String name, ProjectStatus status, Pageable pageable) {
        Specification<Project> spec = ProjectSpecification.withFilters(name, status);
        return projectRepository.findAll(spec, pageable).map(projectMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return projectMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectUpdateRequest req) {
        Project project = findOrThrow(id);
        validateMemberExists(req.managerId());

        project.setName(req.name());
        project.setStartDate(req.startDate());
        project.setExpectedEndDate(req.expectedEndDate());
        project.setActualEndDate(req.actualEndDate());
        project.setBudget(req.budget());
        project.setDescription(req.description());
        project.setManagerId(req.managerId());

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = findOrThrow(id);

        if (ProjectStatus.UNDELETABLE.contains(project.getStatus())) {
            throw new BusinessException(
                    MessageUtil.get("error.project.cannotDelete", project.getStatus().getDisplayName())
            );
        }

        projectRepository.delete(project);
        log.info("Projeto excluído: id={}", id);
    }

    @Override
    @Transactional
    public ProjectResponse changeStatus(Long id, StatusChangeRequest req) {
        Project project = findOrThrow(id);
        ProjectStatus current = project.getStatus();
        ProjectStatus next = req.newStatus();

        if (!current.canTransitionTo(next)) {
            ProjectStatus allowed = current.nextInSequence();
            throw new StatusTransitionException(
                    MessageUtil.get("error.project.statusTransition",
                            current.getDisplayName(),
                            next.getDisplayName(),
                            allowed != null ? allowed.getDisplayName() : "cancelado")
            );
        }

        project.setStatus(next);

        if (next == ProjectStatus.ENCERRADO && project.getActualEndDate() == null) {
            project.setActualEndDate(LocalDate.now());
        }

        log.info("Status do projeto {} alterado: {} → {}", id, current, next);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse allocateMember(Long projectId, MemberAllocationRequest req) {
        Project project = findOrThrow(projectId);
        Long memberId = req.memberId();

        MemberResponse member = fetchMemberOrThrow(memberId);

        if (!member.role().canBeAllocated()) {
            throw new MemberAllocationException(
                    MessageUtil.get("error.member.notEmployee",
                            member.name(), member.role().getDisplayName())
            );
        }

        if (memberRepository.existsByProjectIdAndMemberId(projectId, memberId)) {
            throw new MemberAllocationException(
                    MessageUtil.get("error.member.alreadyAllocated", memberId)
            );
        }

        if (project.getMembers().size() >= MAX_MEMBERS_PER_PROJECT) {
            throw new MemberAllocationException(
                    MessageUtil.get("error.member.projectFull", MAX_MEMBERS_PER_PROJECT)
            );
        }

        Set<ProjectStatus> excluded = Set.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO);
        long activeCount = memberRepository.countActiveAllocationsByMemberId(memberId, excluded);
        if (activeCount >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new MemberAllocationException(
                    MessageUtil.get("error.member.tooManyProjects",
                            memberId, activeCount, MAX_ACTIVE_PROJECTS_PER_MEMBER)
            );
        }

        ProjectMember allocation = ProjectMember.builder()
                .project(project)
                .memberId(memberId)
                .allocatedAt(LocalDate.now())
                .build();

        memberRepository.save(allocation);
        log.info("Membro {} alocado no projeto {}", memberId, projectId);
        return projectMapper.toResponse(findOrThrow(projectId));
    }

    @Override
    @Transactional
    public ProjectResponse removeMember(Long projectId, Long memberId) {
        findOrThrow(projectId);

        ProjectMember allocation = memberRepository
                .findByProjectIdAndMemberId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageUtil.get("error.member.notAllocated", memberId, projectId)
                ));

        memberRepository.delete(allocation);
        log.info("Membro {} removido do projeto {}", memberId, projectId);
        return projectMapper.toResponse(findOrThrow(projectId));
    }

    private Project findOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageUtil.get("error.project.notFound", id)
                ));
    }

    private void validateMemberExists(Long managerId) {
        fetchMemberOrThrow(managerId);
    }

    private MemberResponse fetchMemberOrThrow(Long memberId) {
        try {
            return memberClientService.findById(memberId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    MessageUtil.get("error.member.notFound", memberId)
            );
        }
    }
}