package com.portfolio.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberRole {

    FUNCIONARIO("funcionário"),
    GERENTE("gerente"),
    CONSULTOR("consultor"),
    ANALISTA("analista");

    private final String displayName;

    MemberRole(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static MemberRole fromString(String value) {
        if (value == null) return null;
        for (MemberRole role : values()) {
            if (role.displayName.equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException(
                String.format("Atribuição inválida: '%s'. Valores permitidos: funcionário, gerente, consultor, analista.", value)
        );
    }

    public boolean canBeAllocated() {
        return this == FUNCIONARIO;
    }
}