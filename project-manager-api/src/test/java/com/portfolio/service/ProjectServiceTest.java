package com.portfolio.service;

import com.portfolio.client.MemberClientService;
import com.portfolio.dto.request.MemberAllocationRequest;
import com.portfolio.dto.request.ProjectCreateRequest;
import com.portfolio.dto.request.StatusChangeRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.MemberRole;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import com.portfolio.exception.BusinessException;
import com.portfolio.exception.MemberAllocationException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.exception.StatusTransitionException;
import com.portfolio.mapper.ProjectMapper;
import com.portfolio.repository.ProjectMemberRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.service.impl.ProjectServiceImpl;
import com.portfolio.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService — Regras de negócio")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository memberRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private MemberClientService memberClientService;

    @InjectMocks
    private ProjectServiceImpl service;

    private Project project;
    private ProjectResponse projectResponse;
    private MemberResponse funcionario;
    private MemberResponse gerente;

    @BeforeEach
    void setUp() {

        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        new MessageUtil(messageSource);

        project = Project.builder().id(1L).name("Sistema de RH").startDate(LocalDate.of(2024, 1, 1)).expectedEndDate(LocalDate.of(2024, 4, 1)).budget(new BigDecimal("80000")).managerId(3L).status(ProjectStatus.EM_ANALISE).members(new ArrayList<>()).build();

        projectResponse = new ProjectResponse(1L, "Sistema de RH", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 4, 1), null, new BigDecimal("80000"), null, 3L, ProjectStatus.EM_ANALISE, RiskLevel.BAIXO, List.of(), LocalDateTime.now(), LocalDateTime.now());

        funcionario = new MemberResponse(1L, "João Silva", MemberRole.FUNCIONARIO);
        gerente = new MemberResponse(3L, "Carlos Souza", MemberRole.GERENTE);
    }


    @Test
    @DisplayName("create: deve criar projeto com status EM_ANALISE")
    void create_deveRetornarProjetoComStatusEmAnalise() {
        var req = new ProjectCreateRequest("Sistema de RH", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 4, 1), null, new BigDecimal("80000"), "Descrição", 3L);

        when(memberClientService.findById(3L)).thenReturn(gerente);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = service.create(req);

        assertThat(result.status()).isEqualTo(ProjectStatus.EM_ANALISE);
        verify(projectRepository).save(argThat(p -> p.getStatus() == ProjectStatus.EM_ANALISE));
    }

    @Test
    @DisplayName("create: deve lançar ResourceNotFoundException se gerente não existir")
    void create_deveLancarExcecao_seGerenteNaoExistir() {
        var req = new ProjectCreateRequest("Projeto X", LocalDate.now(), LocalDate.now().plusMonths(2), null, new BigDecimal("50000"), null, 99L);

        when(memberClientService.findById(99L)).thenThrow(new RuntimeException("Not found"));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete: deve excluir projeto com status EM_ANALISE")
    void delete_deveExcluirProjeto_comStatusPermitido() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        service.delete(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("delete: deve lançar BusinessException para projeto INICIADO")
    void delete_deveLancarExcecao_paraProjetoIniciado() {
        project.setStatus(ProjectStatus.INICIADO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(BusinessException.class).hasMessageContaining("iniciado");
    }

    @Test
    @DisplayName("delete: deve lançar BusinessException para projeto EM_ANDAMENTO")
    void delete_deveLancarExcecao_paraProjetoEmAndamento() {
        project.setStatus(ProjectStatus.EM_ANDAMENTO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(BusinessException.class).hasMessageContaining("em andamento");
    }

    @Test
    @DisplayName("delete: deve lançar BusinessException para projeto ENCERRADO")
    void delete_deveLancarExcecao_paraProjetoEncerrado() {
        project.setStatus(ProjectStatus.ENCERRADO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(BusinessException.class).hasMessageContaining("encerrado");
    }

    @Test
    @DisplayName("delete: deve lançar ResourceNotFoundException se projeto não existir")
    void delete_deveLancarExcecao_seProjetoNaoExistir() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    @DisplayName("changeStatus: EM_ANALISE → ANALISE_REALIZADA deve funcionar")
    void changeStatus_transicaoValida_deveAlterar() {
        var req = new StatusChangeRequest(ProjectStatus.ANALISE_REALIZADA);
        var updated = Project.builder().id(1L).status(ProjectStatus.ANALISE_REALIZADA).startDate(LocalDate.now()).expectedEndDate(LocalDate.now().plusMonths(2)).budget(new BigDecimal("80000")).managerId(3L).members(new ArrayList<>()).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(updated);
        when(projectMapper.toResponse(updated)).thenReturn(new ProjectResponse(1L, "Sistema de RH", null, null, null, null, null, 3L, ProjectStatus.ANALISE_REALIZADA, RiskLevel.BAIXO, List.of(), null, null));

        ProjectResponse result = service.changeStatus(1L, req);

        assertThat(result.status()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
        verify(projectRepository).save(argThat(p -> p.getStatus() == ProjectStatus.ANALISE_REALIZADA));
    }

    @Test
    @DisplayName("changeStatus: pular etapas deve lançar StatusTransitionException")
    void changeStatus_pularEtapas_deveLancarExcecao() {
        var req = new StatusChangeRequest(ProjectStatus.INICIADO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.changeStatus(1L, req)).isInstanceOf(StatusTransitionException.class).hasMessageContaining("em análise");
    }

    @Test
    @DisplayName("changeStatus: CANCELADO deve ser permitido de EM_ANALISE")
    void changeStatus_cancelar_deveSerPermitido() {
        var req = new StatusChangeRequest(ProjectStatus.CANCELADO);
        var updated = Project.builder().id(1L).status(ProjectStatus.CANCELADO).startDate(LocalDate.now()).expectedEndDate(LocalDate.now().plusMonths(2)).budget(new BigDecimal("80000")).managerId(3L).members(new ArrayList<>()).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(updated);
        when(projectMapper.toResponse(updated)).thenReturn(new ProjectResponse(1L, "Sistema de RH", null, null, null, null, null, 3L, ProjectStatus.CANCELADO, RiskLevel.BAIXO, List.of(), null, null));

        ProjectResponse result = service.changeStatus(1L, req);

        assertThat(result.status()).isEqualTo(ProjectStatus.CANCELADO);
    }

    @Test
    @DisplayName("changeStatus: projeto ENCERRADO não pode ter status alterado")
    void changeStatus_projetoEncerrado_deveLancarExcecao() {
        project.setStatus(ProjectStatus.ENCERRADO);
        var req = new StatusChangeRequest(ProjectStatus.CANCELADO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.changeStatus(1L, req)).isInstanceOf(StatusTransitionException.class);
    }

    @Test
    @DisplayName("changeStatus: ao encerrar, deve preencher actualEndDate se nula")
    void changeStatus_aoEncerrar_devePreencherActualEndDate() {
        project.setStatus(ProjectStatus.EM_ANDAMENTO);
        project.setActualEndDate(null);
        var req = new StatusChangeRequest(ProjectStatus.ENCERRADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toResponse(any())).thenReturn(projectResponse);

        service.changeStatus(1L, req);

        verify(projectRepository).save(argThat(p -> p.getActualEndDate() != null));
    }

    @Test
    @DisplayName("allocateMember: deve alocar funcionário com sucesso")
    void allocateMember_funcionario_deveAlocarComSucesso() {
        var req = new MemberAllocationRequest(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberClientService.findById(1L)).thenReturn(funcionario);
        when(memberRepository.existsByProjectIdAndMemberId(1L, 1L)).thenReturn(false);
        when(memberRepository.countActiveAllocationsByMemberId(eq(1L), anySet())).thenReturn(0L);
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = service.allocateMember(1L, req);

        assertThat(result).isNotNull();
        verify(memberRepository).save(argThat(pm -> pm.getMemberId().equals(1L) && pm.getProject().getId().equals(1L)));
    }

    @Test
    @DisplayName("allocateMember: gerente NÃO pode ser alocado")
    void allocateMember_gerente_deveLancarExcecao() {
        var req = new MemberAllocationRequest(3L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberClientService.findById(3L)).thenReturn(gerente);

        assertThatThrownBy(() -> service.allocateMember(1L, req)).isInstanceOf(MemberAllocationException.class).hasMessageContaining("gerente");
    }

    @Test
    @DisplayName("allocateMember: membro já alocado deve lançar exceção")
    void allocateMember_jaAlocado_deveLancarExcecao() {
        var req = new MemberAllocationRequest(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberClientService.findById(1L)).thenReturn(funcionario);
        when(memberRepository.existsByProjectIdAndMemberId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.allocateMember(1L, req)).isInstanceOf(MemberAllocationException.class).hasMessageContaining("já está alocado");
    }

    @Test
    @DisplayName("allocateMember: projeto cheio (10 membros) deve lançar exceção")
    void allocateMember_projetoCheio_deveLancarExcecao() {
        var req = new MemberAllocationRequest(1L);

        List<ProjectMember> membros = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            membros.add(ProjectMember.builder().memberId((long) (i + 10)).build());
        }
        project.setMembers(membros);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberClientService.findById(1L)).thenReturn(funcionario);
        when(memberRepository.existsByProjectIdAndMemberId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> service.allocateMember(1L, req)).isInstanceOf(MemberAllocationException.class).hasMessageContaining("limite máximo");
    }

    @Test
    @DisplayName("allocateMember: membro em 3 projetos ativos não pode ser alocado")
    void allocateMember_limiteDeProjetosPorMembro_deveLancarExcecao() {
        var req = new MemberAllocationRequest(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberClientService.findById(1L)).thenReturn(funcionario);
        when(memberRepository.existsByProjectIdAndMemberId(1L, 1L)).thenReturn(false);
        when(memberRepository.countActiveAllocationsByMemberId(eq(1L), anySet())).thenReturn(3L);

        assertThatThrownBy(() -> service.allocateMember(1L, req)).isInstanceOf(MemberAllocationException.class).hasMessageContaining("3 projetos ativos");
    }

    @Test
    @DisplayName("removeMember: deve remover membro alocado com sucesso")
    void removeMember_deveRemoverComSucesso() {
        var allocation = ProjectMember.builder().id(10L).project(project).memberId(1L).allocatedAt(LocalDate.now()).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findByProjectIdAndMemberId(1L, 1L)).thenReturn(Optional.of(allocation));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        service.removeMember(1L, 1L);

        verify(memberRepository).delete(allocation);
    }

    @Test
    @DisplayName("removeMember: membro não alocado deve lançar ResourceNotFoundException")
    void removeMember_naoAlocado_deveLancarExcecao() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findByProjectIdAndMemberId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeMember(1L, 99L)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("não está alocado");
    }

    @Test
    @DisplayName("findById: deve retornar projeto existente")
    void findById_deveRetornarProjeto() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        ProjectResponse result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Sistema de RH");
    }

    @Test
    @DisplayName("findById: deve lançar ResourceNotFoundException para ID inexistente")
    void findById_deveLancarExcecao_seNaoExistir() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("99");
    }
}