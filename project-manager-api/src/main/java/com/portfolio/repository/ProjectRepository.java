package com.portfolio.repository;

import com.portfolio.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT p.status, SUM(p.budget) FROM Project p GROUP BY p.status")
    List<Object[]> sumBudgetGroupedByStatus();

    @Query(value = """
            SELECT COALESCE(
                AVG((actual_end_date - start_date)::numeric), 
                0.0)
            FROM projects 
            WHERE status = 'ENCERRADO' 
              AND actual_end_date IS NOT NULL
            """, nativeQuery = true)
    Double avgDurationDaysOfClosedProjects();

}