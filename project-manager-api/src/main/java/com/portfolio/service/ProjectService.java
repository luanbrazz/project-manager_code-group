package com.portfolio.service;

import com.portfolio.dto.request.MemberAllocationRequest;
import com.portfolio.dto.request.ProjectCreateRequest;
import com.portfolio.dto.request.ProjectUpdateRequest;
import com.portfolio.dto.request.StatusChangeRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponse create(ProjectCreateRequest request);

    Page<ProjectResponse> findAll(String name, ProjectStatus status, Pageable pageable);

    ProjectResponse findById(Long id);

    ProjectResponse update(Long id, ProjectUpdateRequest request);

    void delete(Long id);

    ProjectResponse changeStatus(Long id, StatusChangeRequest request);

    ProjectResponse allocateMember(Long projectId, MemberAllocationRequest request);

    ProjectResponse removeMember(Long projectId, Long memberId);
}