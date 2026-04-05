package com.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectUpdateRequest(

        @NotBlank(message = "{validation.name.notBlank}")
        String name,

        @NotNull(message = "{validation.startDate.notNull}")
        LocalDate startDate,

        @NotNull(message = "{validation.expectedEndDate.notNull}")
        LocalDate expectedEndDate,

        LocalDate actualEndDate,

        @NotNull(message = "{validation.budget.notNull}")
        @Positive(message = "{validation.budget.positive}")
        BigDecimal budget,

        String description,

        @NotNull(message = "{validation.managerId.notNull}")
        Long managerId
) {
}