package com.portfolio.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.ProjectController;
import com.portfolio.service.ProjectService;
import com.portfolio.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler — mapeamento de exceções para HTTP")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        new MessageUtil(messageSource);
    }

    @Test
    @WithMockUser
    @DisplayName("ResourceNotFoundException deve retornar 404 com body padronizado")
    void resourceNotFoundException_deveRetornar404() throws Exception {
        when(projectService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Projeto não encontrado com ID: 99."));

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Projeto não encontrado com ID: 99."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/projects/99"));
    }

    @Test
    @WithMockUser
    @DisplayName("StatusTransitionException deve retornar 422 com body padronizado")
    void statusTransitionException_deveRetornar422() throws Exception {
        when(projectService.changeStatus(anyLong(), any()))
                .thenThrow(new StatusTransitionException("Transição inválida."));

        mockMvc.perform(patch("/api/projects/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newStatus\": \"ENCERRADO\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Transição inválida."));
    }

    @Test
    @WithMockUser
    @DisplayName("MemberAllocationException deve retornar 422 com body padronizado")
    void memberAllocationException_deveRetornar422() throws Exception {
        when(projectService.allocateMember(anyLong(), any()))
                .thenThrow(new MemberAllocationException("Membro já alocado."));

        mockMvc.perform(post("/api/projects/1/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": 1}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Membro já alocado."));
    }

    @Test
    @WithMockUser
    @DisplayName("BusinessException deve retornar 400 com body padronizado")
    void businessException_deveRetornar400() throws Exception {
        doThrow(new BusinessException("Projeto com status 'iniciado' não pode ser excluído."))
                .when(projectService).delete(1L);

        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Projeto com status 'iniciado' não pode ser excluído."));
    }

    @Test
    @WithMockUser
    @DisplayName("MethodArgumentNotValidException deve retornar 400 com mapa de fieldErrors")
    void methodArgumentNotValidException_deveRetornar400ComFieldErrors() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"budget\": -1, \"managerId\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Exception genérica deve retornar 500 com mensagem padrão")
    void exceptionGenerica_deveRetornar500() throws Exception {
        when(projectService.findById(1L))
                .thenThrow(new RuntimeException("Erro inesperado interno."));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
}