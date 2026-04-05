package com.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest(
        @NotBlank(message = "{validation.name.notBlank}")
        String name,

        @NotBlank(message = "{validation.role.notBlank}")
        String role
) {
}