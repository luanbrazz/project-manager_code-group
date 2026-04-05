package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dto.request.MemberAllocationRequest;
import com.portfolio.dto.request.ProjectUpdateRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import com.portfolio.exception.BusinessException;
import com.portfolio.exception.GlobalExceptionHandler;
import com.portfolio.exception.MemberAllocationException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProjectController — Testes Complementares")
class ProjectControllerComplementarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        projectResponse = new ProjectResponse(
                1L, "Sistema de RH",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 4, 1),
                null,
                new BigDecimal("80000"),
                "Descrição",
                3L,
                ProjectStatus.EM_ANALISE,
                RiskLevel.BAIXO,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/projects/{id}: deve retornar 200 com projeto atualizado")
    void update_deveRetornar200() throws Exception {
        var req = new ProjectUpdateRequest(
                "Sistema de RH Atualizado", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 1), null, new BigDecimal("90000"),
                "Nova descrição", 3L
        );

        when(projectService.update(eq(1L), any())).thenReturn(projectResponse);

        mockMvc.perform(put("/api/projects/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/projects/{id}: deve retornar 404 para projeto inexistente")
    void update_deveRetornar404() throws Exception {
        var req = new ProjectUpdateRequest("Nome", LocalDate.now(), LocalDate.now().plusMonths(2),
                null, new BigDecimal("50000"), null, 1L);

        when(projectService.update(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Projeto não encontrado com ID: 99."));

        mockMvc.perform(put("/api/projects/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{id}: deve retornar 204 quando excluído com sucesso")
    void delete_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{id}: deve retornar 400 para status bloqueado")
    void delete_deveRetornar400_statusBloqueado() throws Exception {
        doThrow(new BusinessException("Projeto com status 'iniciado' não pode ser excluído."))
                .when(projectService).delete(1L);

        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Projeto com status 'iniciado' não pode ser excluído."));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{id}: deve retornar 404 para projeto inexistente")
    void delete_deveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Projeto não encontrado com ID: 99."))
                .when(projectService).delete(99L);

        mockMvc.perform(delete("/api/projects/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/projects/{id}/members: deve retornar 200 ao alocar membro")
    void allocateMember_deveRetornar200() throws Exception {
        var req = new MemberAllocationRequest(1L);
        when(projectService.allocateMember(eq(1L), any())).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects/1/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/projects/{id}/members: deve retornar 422 para gerente")
    void allocateMember_deveRetornar422_gerente() throws Exception {
        var req = new MemberAllocationRequest(3L);
        when(projectService.allocateMember(eq(1L), any()))
                .thenThrow(new MemberAllocationException(
                        "Apenas membros com atribuição 'funcionário' podem ser alocados."));

        mockMvc.perform(post("/api/projects/1/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/projects/{id}/members: deve retornar 422 para membro já alocado")
    void allocateMember_deveRetornar422_jaAlocado() throws Exception {
        var req = new MemberAllocationRequest(1L);
        when(projectService.allocateMember(eq(1L), any()))
                .thenThrow(new MemberAllocationException("Membro com ID 1 já está alocado neste projeto."));

        mockMvc.perform(post("/api/projects/1/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{projectId}/members/{memberId}: deve retornar 204 No Content")
    void removeMember_deveRetornar204() throws Exception {
        when(projectService.removeMember(1L, 1L)).thenReturn(projectResponse);

        mockMvc.perform(delete("/api/projects/1/members/1").with(csrf()));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{projectId}/members/{memberId}: deve retornar 404 para membro não alocado")
    void removeMember_deveRetornar404() throws Exception {
        when(projectService.removeMember(1L, 99L))
                .thenThrow(new ResourceNotFoundException("Membro com ID 99 não está alocado no projeto 1."));

        mockMvc.perform(delete("/api/projects/1/members/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects?name=RH: deve filtrar por nome")
    void findAll_comFiltroNome_deveRetornar200() throws Exception {
        var page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(eq("RH"), isNull(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/projects").param("name", "RH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Sistema de RH"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects com paginação")
    void findAll_comPaginacao_deveRetornar200() throws Exception {
        var page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/projects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/projects/{id}: deve retornar 401 sem autenticação")
    void update_deveRetornar401() throws Exception {
        mockMvc.perform(put("/api/projects/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id}: deve retornar 401 sem autenticação")
    void delete_deveRetornar401() throws Exception {
        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}