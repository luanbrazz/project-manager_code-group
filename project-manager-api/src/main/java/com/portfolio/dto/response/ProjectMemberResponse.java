package com.portfolio.dto.response;

import java.time.LocalDate;

public record ProjectMemberResponse(
        Long id,
        Long memberId,
        LocalDate allocatedAt
) {
}