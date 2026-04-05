package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dto.request.ProjectCreateRequest;
import com.portfolio.dto.request.StatusChangeRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import com.portfolio.exception.GlobalExceptionHandler;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.exception.StatusTransitionException;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProjectController — Camada HTTP")
class ProjectControllerTest {

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
                "Descrição do projeto",
                3L,
                ProjectStatus.EM_ANALISE,
                RiskLevel.BAIXO,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /api/projects: deve retornar 201 com projeto criado")
    void create_deveRetornar201() throws Exception {
        var req = new ProjectCreateRequest(
                "Sistema de RH", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 4, 1), null,
                new BigDecimal("80000"), "Descrição", 3L
        );

        when(projectService.create(any())).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sistema de RH"))
                .andExpect(jsonPath("$.status").value("EM_ANALISE"))
                .andExpect(jsonPath("$.riskLevel").value("BAIXO"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/projects: deve retornar 400 quando nome estiver vazio")
    void create_deveRetornar400_semNome() throws Exception {
        var reqSemNome = new ProjectCreateRequest(
                "", LocalDate.now(), LocalDate.now().plusMonths(2),
                null, new BigDecimal("50000"), null, 1L
        );

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqSemNome)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/projects: deve retornar 400 quando budget for negativo")
    void create_deveRetornar400_budgetNegativo() throws Exception {
        var req = new ProjectCreateRequest(
                "Projeto X", LocalDate.now(), LocalDate.now().plusMonths(2),
                null, new BigDecimal("-1000"), null, 1L
        );

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.budget").exists());
    }

    @Test
    @DisplayName("POST /api/projects: deve retornar 401 sem autenticação")
    void create_deveRetornar401_semAutenticacao() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects: deve retornar 200 com página de projetos")
    void findAll_deveRetornar200() throws Exception {
        var page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Sistema de RH"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects?status=EM_ANALISE: deve filtrar por status")
    void findAll_comFiltroDeStatus_deveRetornar200() throws Exception {
        var page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(isNull(), eq(ProjectStatus.EM_ANALISE), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/projects").param("status", "EM_ANALISE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("EM_ANALISE"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects/{id}: deve retornar 200 com projeto")
    void findById_deveRetornar200() throws Exception {
        when(projectService.findById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.riskLevel").value("BAIXO"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/projects/{id}: deve retornar 404 para projeto inexistente")
    void findById_deveRetornar404() throws Exception {
        when(projectService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Projeto não encontrado com ID: 99."));

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Projeto não encontrado com ID: 99."))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/projects/{id}: deve retornar 204 ao excluir")
    void delete_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/projects/{id}/status: deve retornar 200 com novo status")
    void changeStatus_deveRetornar200() throws Exception {
        var req = new StatusChangeRequest(ProjectStatus.ANALISE_REALIZADA);
        var updated = new ProjectResponse(1L, "Sistema de RH", null, null, null,
                null, null, 3L, ProjectStatus.ANALISE_REALIZADA, RiskLevel.BAIXO,
                List.of(), null, null);

        when(projectService.changeStatus(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/projects/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANALISE_REALIZADA"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/projects/{id}/status: deve retornar 422 para transição inválida")
    void changeStatus_deveRetornar422_transicaoInvalida() throws Exception {
        var req = new StatusChangeRequest(ProjectStatus.ENCERRADO);

        when(projectService.changeStatus(eq(1L), any()))
                .thenThrow(new StatusTransitionException("Transição inválida: 'em análise' para 'encerrado'."));

        mockMvc.perform(patch("/api/projects/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Transição inválida: 'em análise' para 'encerrado'."));
    }
}