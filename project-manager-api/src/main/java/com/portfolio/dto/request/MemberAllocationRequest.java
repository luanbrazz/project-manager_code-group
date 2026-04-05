package com.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberAllocationRequest(
        @NotNull(message = "{validation.memberId.notNull}")
        Long memberId
) {
}