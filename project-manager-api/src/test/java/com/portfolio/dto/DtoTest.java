package com.portfolio.dto;

import com.portfolio.dto.request.*;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.dto.response.ProjectMemberResponse;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs — validações e estrutura de records")
class DtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("ProjectCreateRequest — validações")
    class ProjectCreateRequestTest {

        private ProjectCreateRequest valid() {
            return new ProjectCreateRequest(
                    "Sistema de RH",
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 1),
                    null,
                    new BigDecimal("80000"),
                    "Descrição",
                    1L
            );
        }

        @Test
        @DisplayName("request válida não deve ter violações")
        void requestValida_semViolacoes() {
            assertThat(validator.validate(valid())).isEmpty();
        }

        @Test
        @DisplayName("nome em branco deve falhar")
        void nomeEmBranco_deveGerarViolacao() {
            var req = new ProjectCreateRequest("", LocalDate.now(),
                    LocalDate.now().plusMonths(1), null, new BigDecimal("1000"), null, 1L);
            Set<ConstraintViolation<ProjectCreateRequest>> violations = validator.validate(req);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }

        @Test
        @DisplayName("startDate nula deve falhar")
        void startDateNula_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", null,
                    LocalDate.now().plusMonths(1), null, new BigDecimal("1000"), null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("startDate"));
        }

        @Test
        @DisplayName("expectedEndDate nula deve falhar")
        void expectedEndDateNula_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", LocalDate.now(),
                    null, null, new BigDecimal("1000"), null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("expectedEndDate"));
        }

        @Test
        @DisplayName("budget nulo deve falhar")
        void budgetNulo_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(1), null, null, null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("budget"));
        }

        @Test
        @DisplayName("budget negativo deve falhar")
        void budgetNegativo_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(1), null, new BigDecimal("-1"), null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("budget"));
        }

        @Test
        @DisplayName("budget zero deve falhar")
        void budgetZero_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(1), null, BigDecimal.ZERO, null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("budget"));
        }

        @Test
        @DisplayName("managerId nulo deve falhar")
        void managerIdNulo_deveGerarViolacao() {
            var req = new ProjectCreateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(1), null, new BigDecimal("1000"), null, null);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("managerId"));
        }
    }


    @Nested
    @DisplayName("ProjectUpdateRequest — validações")
    class ProjectUpdateRequestTest {

        @Test
        @DisplayName("request válida não deve ter violações")
        void requestValida_semViolacoes() {
            var req = new ProjectUpdateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(2), null, new BigDecimal("50000"), "Desc", 1L);
            assertThat(validator.validate(req)).isEmpty();
        }

        @Test
        @DisplayName("nome em branco deve falhar")
        void nomeEmBranco_deveGerarViolacao() {
            var req = new ProjectUpdateRequest("", LocalDate.now(),
                    LocalDate.now().plusMonths(2), null, new BigDecimal("50000"), null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }

        @Test
        @DisplayName("budget negativo deve falhar")
        void budgetNegativo_deveGerarViolacao() {
            var req = new ProjectUpdateRequest("Nome", LocalDate.now(),
                    LocalDate.now().plusMonths(2), null, new BigDecimal("-500"), null, 1L);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("budget"));
        }
    }


    @Nested
    @DisplayName("StatusChangeRequest — validações")
    class StatusChangeRequestTest {

        @Test
        @DisplayName("status válido não deve ter violações")
        void statusValido_semViolacoes() {
            var req = new StatusChangeRequest(ProjectStatus.ANALISE_REALIZADA);
            assertThat(validator.validate(req)).isEmpty();
        }

        @Test
        @DisplayName("status nulo deve falhar")
        void statusNulo_deveGerarViolacao() {
            var req = new StatusChangeRequest(null);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("newStatus"));
        }
    }


    @Nested
    @DisplayName("MemberAllocationRequest — validações")
    class MemberAllocationRequestTest {

        @Test
        @DisplayName("memberId válido não deve ter violações")
        void memberIdValido_semViolacoes() {
            var req = new MemberAllocationRequest(1L);
            assertThat(validator.validate(req)).isEmpty();
        }

        @Test
        @DisplayName("memberId nulo deve falhar")
        void memberIdNulo_deveGerarViolacao() {
            var req = new MemberAllocationRequest(null);
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("memberId"));
        }
    }


    @Nested
    @DisplayName("MemberCreateRequest — validações")
    class MemberCreateRequestTest {

        @Test
        @DisplayName("request válida não deve ter violações")
        void requestValida_semViolacoes() {
            var req = new MemberCreateRequest("João Silva", "funcionário");
            assertThat(validator.validate(req)).isEmpty();
        }

        @Test
        @DisplayName("nome em branco deve falhar")
        void nomeEmBranco_deveGerarViolacao() {
            var req = new MemberCreateRequest("", "funcionário");
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }

        @Test
        @DisplayName("role em branco deve falhar")
        void roleEmBranco_deveGerarViolacao() {
            var req = new MemberCreateRequest("João", "");
            assertThat(validator.validate(req))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("role"));
        }
    }


    @Nested
    @DisplayName("Response records — instanciação e acesso a campos")
    class ResponseRecordsTest {

        @Test
        @DisplayName("ProjectMemberResponse: campos acessíveis corretamente")
        void projectMemberResponse_camposAcessiveis() {
            var response = new ProjectMemberResponse(10L, 2L, LocalDate.of(2024, 3, 1));
            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.memberId()).isEqualTo(2L);
            assertThat(response.allocatedAt()).isEqualTo(LocalDate.of(2024, 3, 1));
        }

        @Test
        @DisplayName("ProjectMemberResponse: dois records com mesmos valores são iguais")
        void projectMemberResponse_equalsComMesmosValores() {
            var r1 = new ProjectMemberResponse(1L, 2L, LocalDate.of(2024, 1, 1));
            var r2 = new ProjectMemberResponse(1L, 2L, LocalDate.of(2024, 1, 1));
            assertThat(r1).isEqualTo(r2);
        }

        @Test
        @DisplayName("PortfolioReportResponse: campos acessíveis corretamente")
        void portfolioReportResponse_camposAcessiveis() {
            var countMap = Map.of(ProjectStatus.EM_ANALISE, 3L);
            var budgetMap = Map.of(ProjectStatus.EM_ANALISE, new BigDecimal("300000"));
            var response = new PortfolioReportResponse(countMap, budgetMap, 90.0, 5L);

            assertThat(response.projectCountByStatus()).isEqualTo(countMap);
            assertThat(response.totalBudgetByStatus()).isEqualTo(budgetMap);
            assertThat(response.avgDurationDaysClosedProjects()).isEqualTo(90.0);
            assertThat(response.totalUniqueMembersAllocated()).isEqualTo(5L);
        }

        @Test
        @DisplayName("PortfolioReportResponse: com valores nulos não lança exceção")
        void portfolioReportResponse_comValoresNulos_naoLancaExcecao() {
            var response = new PortfolioReportResponse(null, null, null, null);
            assertThat(response.avgDurationDaysClosedProjects()).isNull();
            assertThat(response.totalUniqueMembersAllocated()).isNull();
        }

        @Test
        @DisplayName("MemberResponse: campos acessíveis corretamente")
        void memberResponse_camposAcessiveis() {
            var response = new MemberResponse(1L, "João", "funcionário");
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("João");
            assertThat(response.role()).isEqualTo("funcionário");
        }

        @Test
        @DisplayName("ProjectResponse: todos os campos acessíveis")
        void projectResponse_todosOsCamposAcessiveis() {
            var now = LocalDateTime.now();
            var members = List.of(new ProjectMemberResponse(1L, 2L, LocalDate.now()));
            var response = new ProjectResponse(
                    1L, "Projeto", LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 1), LocalDate.of(2024, 5, 15),
                    new BigDecimal("100000"), "Desc", 3L,
                    ProjectStatus.ENCERRADO, RiskLevel.MEDIO,
                    members, now, now
            );

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Projeto");
            assertThat(response.status()).isEqualTo(ProjectStatus.ENCERRADO);
            assertThat(response.riskLevel()).isEqualTo(RiskLevel.MEDIO);
            assertThat(response.members()).hasSize(1);
            assertThat(response.createdAt()).isEqualTo(now);
        }
    }


    @Nested
    @DisplayName("RiskLevel enum — displayName")
    class RiskLevelTest {

        @Test
        @DisplayName("BAIXO deve ter displayName 'Baixo risco'")
        void baixo_displayName() {
            assertThat(RiskLevel.BAIXO.getDisplayName()).isEqualTo("Baixo risco");
        }

        @Test
        @DisplayName("MEDIO deve ter displayName 'Médio risco'")
        void medio_displayName() {
            assertThat(RiskLevel.MEDIO.getDisplayName()).isEqualTo("Médio risco");
        }

        @Test
        @DisplayName("ALTO deve ter displayName 'Alto risco'")
        void alto_displayName() {
            assertThat(RiskLevel.ALTO.getDisplayName()).isEqualTo("Alto risco");
        }
    }
}