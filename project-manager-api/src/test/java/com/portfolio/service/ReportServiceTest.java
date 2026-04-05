package com.portfolio.service;

import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.repository.ProjectMemberRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService — Geração de relatório de portfólio")
class ReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository memberRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("deve retornar contagem de projetos por status corretamente")
    void generatePortfolioReport_deveRetornarContagemPorStatus() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{ProjectStatus.EM_ANALISE, 3L},
                new Object[]{ProjectStatus.INICIADO, 1L}
        ));
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(0.0);
        when(memberRepository.countDistinctMembers()).thenReturn(0L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.projectCountByStatus())
                .containsEntry(ProjectStatus.EM_ANALISE, 3L)
                .containsEntry(ProjectStatus.INICIADO, 1L);
    }

    @Test
    @DisplayName("deve retornar total orçado por status corretamente")
    void generatePortfolioReport_deveRetornarOrcamentoPorStatus() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of(
                new Object[]{ProjectStatus.EM_ANALISE, new BigDecimal("150000.00")},
                new Object[]{ProjectStatus.ENCERRADO, new BigDecimal("800000.00")}
        ));
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(0.0);
        when(memberRepository.countDistinctMembers()).thenReturn(0L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.totalBudgetByStatus())
                .containsEntry(ProjectStatus.EM_ANALISE, new BigDecimal("150000.00"))
                .containsEntry(ProjectStatus.ENCERRADO, new BigDecimal("800000.00"));
    }

    @Test
    @DisplayName("deve retornar média de duração dos projetos encerrados")
    void generatePortfolioReport_deveRetornarMediaDeDuracao() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(45.5);
        when(memberRepository.countDistinctMembers()).thenReturn(0L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.avgDurationDaysClosedProjects()).isEqualTo(45.5);
    }

    @Test
    @DisplayName("deve retornar total de membros únicos alocados")
    void generatePortfolioReport_deveRetornarTotalDeMembrosUnicos() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(0.0);
        when(memberRepository.countDistinctMembers()).thenReturn(7L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.totalUniqueMembersAllocated()).isEqualTo(7L);
    }

    @Test
    @DisplayName("deve retornar mapas vazios quando não há projetos")
    void generatePortfolioReport_semProjetos_deveRetornarMapasVazios() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of());
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(0.0);
        when(memberRepository.countDistinctMembers()).thenReturn(0L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.projectCountByStatus()).isEmpty();
        assertThat(result.totalBudgetByStatus()).isEmpty();
        assertThat(result.avgDurationDaysClosedProjects()).isEqualTo(0.0);
        assertThat(result.totalUniqueMembersAllocated()).isZero();
    }

    @Test
    @DisplayName("deve consolidar múltiplos status no relatório completo")
    void generatePortfolioReport_completo_deveConsolidarTodosOsDados() {
        when(projectRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{ProjectStatus.EM_ANALISE,    2L},
                new Object[]{ProjectStatus.EM_ANDAMENTO,  3L},
                new Object[]{ProjectStatus.ENCERRADO,     5L},
                new Object[]{ProjectStatus.CANCELADO,     1L}
        ));
        when(projectRepository.sumBudgetGroupedByStatus()).thenReturn(List.of(
                new Object[]{ProjectStatus.EM_ANALISE,   new BigDecimal("200000")},
                new Object[]{ProjectStatus.EM_ANDAMENTO, new BigDecimal("1500000")},
                new Object[]{ProjectStatus.ENCERRADO,    new BigDecimal("3000000")}
        ));
        when(projectRepository.avgDurationDaysOfClosedProjects()).thenReturn(120.0);
        when(memberRepository.countDistinctMembers()).thenReturn(12L);

        PortfolioReportResponse result = reportService.generatePortfolioReport();

        assertThat(result.projectCountByStatus()).hasSize(4);
        assertThat(result.totalBudgetByStatus()).hasSize(3);
        assertThat(result.avgDurationDaysClosedProjects()).isEqualTo(120.0);
        assertThat(result.totalUniqueMembersAllocated()).isEqualTo(12L);
    }
}