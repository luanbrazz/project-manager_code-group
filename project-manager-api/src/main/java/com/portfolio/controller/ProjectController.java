package com.portfolio.controller;

import com.portfolio.dto.request.MemberAllocationRequest;
import com.portfolio.dto.request.ProjectCreateRequest;
import com.portfolio.dto.request.ProjectUpdateRequest;
import com.portfolio.dto.request.StatusChangeRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "Gerenciamento do ciclo de vida dos projetos")
@SecurityRequirement(name = "basicAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Criar projeto",
            description = "Cria um novo projeto com status inicial 'em análise'.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Gerente responsável não encontrado")
    })
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar projetos com paginação e filtros",
            description = "Filtra por nome (parcial, case-insensitive) e/ou status. Paginação via ?page=0&size=10&sort=name")
    public ResponseEntity<Page<ProjectResponse>> findAll(
            @Parameter(description = "Parte do nome do projeto")
            @RequestParam(required = false) String name,

            @Parameter(description = "Status exato do projeto")
            @RequestParam(required = false) ProjectStatus status,

            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.findAll(name, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto",
            description = "Atualiza dados do projeto. Para alterar status use PATCH /{id}/status.")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto",
            description = "Exclui o projeto. Não permitido se status for INICIADO, EM_ANDAMENTO ou ENCERRADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Projeto não pode ser excluído no status atual")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do projeto",
            description = "Avança na sequência lógica de status. Cancelamento é permitido a qualquer momento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    public ResponseEntity<ProjectResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusChangeRequest request) {
        return ResponseEntity.ok(projectService.changeStatus(id, request));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Alocar membro ao projeto",
            description = "Apenas 'funcionários'. Máximo 10 por projeto. Membro pode estar em até 3 projetos ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membro alocado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Regra de alocação violada")
    })
    public ResponseEntity<ProjectResponse> allocateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberAllocationRequest request) {
        return ResponseEntity.ok(projectService.allocateMember(id, request));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @Operation(summary = "Remover membro do projeto")
    @ApiResponse(responseCode = "204", description = "Membro removido com sucesso")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long memberId) {
        projectService.removeMember(id, memberId);
        return ResponseEntity.noContent().build();
    }
}