package com.portfolio.controller;

import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.exception.GlobalExceptionHandler;
import com.portfolio.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ReportController — Camada HTTP")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/reports/portfolio: deve retornar 200 com relatório completo")
    void portfolio_deveRetornar200ComRelatorio() throws Exception {
        var response = new PortfolioReportResponse(
                Map.of(ProjectStatus.EM_ANALISE, 2L, ProjectStatus.ENCERRADO, 5L),
                Map.of(ProjectStatus.EM_ANALISE, new BigDecimal("200000"),
                        ProjectStatus.ENCERRADO,  new BigDecimal("3000000")),
                120.0,
                12L
        );
        when(reportService.generatePortfolioReport()).thenReturn(response);

        mockMvc.perform(get("/api/reports/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUniqueMembersAllocated").value(12))
                .andExpect(jsonPath("$.avgDurationDaysClosedProjects").value(120.0))
                .andExpect(jsonPath("$.projectCountByStatus.EM_ANALISE").value(2))
                .andExpect(jsonPath("$.projectCountByStatus.ENCERRADO").value(5))
                .andExpect(jsonPath("$.totalBudgetByStatus.EM_ANALISE").value(200000));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/reports/portfolio: deve retornar 200 com mapas vazios quando não há projetos")
    void portfolio_semProjetos_deveRetornarMapasVazios() throws Exception {
        var response = new PortfolioReportResponse(Map.of(), Map.of(), 0.0, 0L);
        when(reportService.generatePortfolioReport()).thenReturn(response);

        mockMvc.perform(get("/api/reports/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUniqueMembersAllocated").value(0))
                .andExpect(jsonPath("$.avgDurationDaysClosedProjects").value(0.0));
    }

    @Test
    @DisplayName("GET /api/reports/portfolio: deve retornar 401 sem autenticação")
    void portfolio_deveRetornar401_semAutenticacao() throws Exception {
        mockMvc.perform(get("/api/reports/portfolio"))
                .andExpect(status().isUnauthorized());
    }
}