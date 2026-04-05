package com.portfolio.repository;

import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectIdAndMemberId(Long projectId, Long memberId);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    @Query("""
            SELECT COUNT(pm)
            FROM ProjectMember pm
            WHERE pm.memberId = :memberId
              AND pm.project.status NOT IN :excludedStatuses
            """)
    long countActiveAllocationsByMemberId(
            @Param("memberId") Long memberId,
            @Param("excludedStatuses") Set<ProjectStatus> excludedStatuses
    );

    @Query("SELECT COUNT(DISTINCT pm.memberId) FROM ProjectMember pm")
    long countDistinctMembers();
}