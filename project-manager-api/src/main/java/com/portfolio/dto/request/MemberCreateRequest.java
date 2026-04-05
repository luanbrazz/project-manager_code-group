package com.portfolio.dto.request;

import com.portfolio.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberCreateRequest(

        @NotBlank(message = "{validation.name.notBlank}")
        String name,

        @NotNull(message = "{validation.role.notBlank}")
        @Schema(
                description = "Atribuição do membro",
                allowableValues = {"funcionário", "gerente", "consultor", "analista"},
                example = "funcionário"
        )
        MemberRole role
) {}