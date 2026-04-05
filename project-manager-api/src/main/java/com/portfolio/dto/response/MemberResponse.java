package com.portfolio.dto.response;

import com.portfolio.enums.MemberRole;

public record MemberResponse(
        Long id,
        String name,
        MemberRole role
) {}