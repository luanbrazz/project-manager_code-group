package com.portfolio.dto.request;

import com.portfolio.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "{validation.newStatus.notNull}")
        ProjectStatus newStatus
) {
}