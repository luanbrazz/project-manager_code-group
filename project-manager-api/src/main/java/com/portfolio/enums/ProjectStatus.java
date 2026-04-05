package com.portfolio.enums;

import java.util.Map;
import java.util.Set;

public enum ProjectStatus {

    EM_ANALISE("em análise"),
    ANALISE_REALIZADA("análise realizada"),
    ANALISE_APROVADA("análise aprovada"),
    INICIADO("iniciado"),
    PLANEJADO("planejado"),
    EM_ANDAMENTO("em andamento"),
    ENCERRADO("encerrado"),
    CANCELADO("cancelado");

    private static final Map<ProjectStatus, ProjectStatus> NEXT = Map.of(
            EM_ANALISE, ANALISE_REALIZADA,
            ANALISE_REALIZADA, ANALISE_APROVADA,
            ANALISE_APROVADA, INICIADO,
            INICIADO, PLANEJADO,
            PLANEJADO, EM_ANDAMENTO,
            EM_ANDAMENTO, ENCERRADO
    );

    public static final Set<ProjectStatus> UNDELETABLE = Set.of(
            INICIADO, EM_ANDAMENTO, ENCERRADO
    );

    public static final Set<ProjectStatus> ACTIVE_FOR_MEMBER_LIMIT = Set.of(
            EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA,
            INICIADO, PLANEJADO, EM_ANDAMENTO
    );

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ProjectStatus nextInSequence() {
        return NEXT.get(this);
    }

    public boolean canTransitionTo(ProjectStatus target) {
        if (this == ENCERRADO || this == CANCELADO) {
            return false;
        }
        if (target == CANCELADO) {
            return true;
        }
        return NEXT.get(this) == target;
    }
}