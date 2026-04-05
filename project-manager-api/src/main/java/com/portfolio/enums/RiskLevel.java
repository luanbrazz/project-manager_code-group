package com.portfolio.enums;

public enum RiskLevel {
    BAIXO("Baixo risco"),
    MEDIO("Médio risco"),
    ALTO("Alto risco");

    private final String displayName;

    RiskLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}