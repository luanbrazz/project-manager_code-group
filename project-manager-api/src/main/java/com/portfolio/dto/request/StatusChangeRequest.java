package com.portfolio.dto.request;

import com.portfolio.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(

        @NotNull(message = "{validation.newStatus.notNull}")
        @Schema(
                description = "Novo status do projeto. Deve respeitar a sequência: " +
                        "EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → " +
                        "INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO. " +
                        "CANCELADO pode ser aplicado a qualquer momento.",
                example = "ANALISE_REALIZADA"
        )
        ProjectStatus newStatus
) {
}