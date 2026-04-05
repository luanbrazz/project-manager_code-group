package com.portfolio.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProjectStatus — Máquina de estados")
class ProjectStatusTest {

    @Test
    @DisplayName("EM_ANALISE → ANALISE_REALIZADA deve ser permitida")
    void emAnalise_canTransitionTo_analiseRealizada() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ANALISE_REALIZADA)).isTrue();
    }

    @Test
    @DisplayName("ANALISE_REALIZADA → ANALISE_APROVADA deve ser permitida")
    void analiseRealizada_canTransitionTo_analiseAprovada() {
        assertThat(ProjectStatus.ANALISE_REALIZADA.canTransitionTo(ProjectStatus.ANALISE_APROVADA)).isTrue();
    }

    @Test
    @DisplayName("ANALISE_APROVADA → INICIADO deve ser permitida")
    void analiseAprovada_canTransitionTo_iniciado() {
        assertThat(ProjectStatus.ANALISE_APROVADA.canTransitionTo(ProjectStatus.INICIADO)).isTrue();
    }

    @Test
    @DisplayName("INICIADO → PLANEJADO deve ser permitida")
    void iniciado_canTransitionTo_planejado() {
        assertThat(ProjectStatus.INICIADO.canTransitionTo(ProjectStatus.PLANEJADO)).isTrue();
    }

    @Test
    @DisplayName("PLANEJADO → EM_ANDAMENTO deve ser permitida")
    void planejado_canTransitionTo_emAndamento() {
        assertThat(ProjectStatus.PLANEJADO.canTransitionTo(ProjectStatus.EM_ANDAMENTO)).isTrue();
    }

    @Test
    @DisplayName("EM_ANDAMENTO → ENCERRADO deve ser permitida")
    void emAndamento_canTransitionTo_encerrado() {
        assertThat(ProjectStatus.EM_ANDAMENTO.canTransitionTo(ProjectStatus.ENCERRADO)).isTrue();
    }


    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"EM_ANALISE", "ANALISE_REALIZADA",
            "ANALISE_APROVADA", "INICIADO", "PLANEJADO", "EM_ANDAMENTO"})
    @DisplayName("CANCELADO pode ser aplicado de qualquer status ativo")
    void cancelamento_e_permitido_de_qualquer_status_ativo(ProjectStatus status) {
        assertThat(status.canTransitionTo(ProjectStatus.CANCELADO)).isTrue();
    }

    @Test
    @DisplayName("ENCERRADO NÃO pode ser cancelado")
    void encerrado_nao_pode_ser_cancelado() {
        assertThat(ProjectStatus.ENCERRADO.canTransitionTo(ProjectStatus.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("CANCELADO NÃO pode ser cancelado novamente")
    void cancelado_nao_pode_ser_cancelado_novamente() {
        assertThat(ProjectStatus.CANCELADO.canTransitionTo(ProjectStatus.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("EM_ANALISE NÃO pode pular para INICIADO")
    void emAnalise_naoPode_pularParaIniciado() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.INICIADO)).isFalse();
    }

    @Test
    @DisplayName("EM_ANALISE NÃO pode pular para ENCERRADO")
    void emAnalise_naoPode_pularParaEncerrado() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ENCERRADO)).isFalse();
    }

    @Test
    @DisplayName("ANALISE_APROVADA NÃO pode pular para EM_ANDAMENTO")
    void analiseAprovada_naoPode_pularParaEmAndamento() {
        assertThat(ProjectStatus.ANALISE_APROVADA.canTransitionTo(ProjectStatus.EM_ANDAMENTO)).isFalse();
    }

    @Test
    @DisplayName("INICIADO NÃO pode voltar para EM_ANALISE")
    void iniciado_naoPode_voltarParaEmAnalise() {
        assertThat(ProjectStatus.INICIADO.canTransitionTo(ProjectStatus.EM_ANALISE)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"INICIADO", "EM_ANDAMENTO", "ENCERRADO"})
    @DisplayName("INICIADO, EM_ANDAMENTO e ENCERRADO devem estar no conjunto UNDELETABLE")
    void statusesBloqueadores_devem_estar_em_UNDELETABLE(ProjectStatus status) {
        assertThat(ProjectStatus.UNDELETABLE).contains(status);
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"EM_ANALISE", "ANALISE_REALIZADA",
            "ANALISE_APROVADA", "PLANEJADO", "CANCELADO"})
    @DisplayName("Demais status NÃO devem bloquear exclusão")
    void statusesNaoBloqueadores_nao_devem_estar_em_UNDELETABLE(ProjectStatus status) {
        assertThat(ProjectStatus.UNDELETABLE).doesNotContain(status);
    }


    @Test
    @DisplayName("nextInSequence de EM_ANALISE deve retornar ANALISE_REALIZADA")
    void nextInSequence_deEmAnalise_retornaAnaliseRealizada() {
        assertThat(ProjectStatus.EM_ANALISE.nextInSequence()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
    }

    @Test
    @DisplayName("nextInSequence de ENCERRADO deve retornar null (sem próximo)")
    void nextInSequence_deEncerrado_retornaNull() {
        assertThat(ProjectStatus.ENCERRADO.nextInSequence()).isNull();
    }

    @Test
    @DisplayName("nextInSequence de CANCELADO deve retornar null (sem próximo)")
    void nextInSequence_deCancelado_retornaNull() {
        assertThat(ProjectStatus.CANCELADO.nextInSequence()).isNull();
    }
}