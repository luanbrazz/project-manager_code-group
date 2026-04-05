package com.portfolio.service;

import com.portfolio.enums.RiskLevel;
import com.portfolio.service.impl.RiskCalculatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RiskCalculatorService — Classificação de risco")
class RiskCalculatorServiceTest {

    private RiskCalculatorService service;

    @BeforeEach
    void setUp() {

        service = new RiskCalculatorServiceImpl();
    }

    @Test
    @DisplayName("BAIXO: orçamento no limite (100k) e prazo no limite (3 meses)")
    void deveRetornarBaixo_quandoOrcamentoEPrazoEstaoNoLimite() {

        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 4, 1);

        RiskLevel result = service.calculate(new BigDecimal("100000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("BAIXO: orçamento pequeno e prazo muito curto")
    void deveRetornarBaixo_quandoOrcamentoPequenoEPrazoCurto() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 2, 1);

        RiskLevel result = service.calculate(new BigDecimal("50000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("BAIXO: orçamento zero e prazo de 1 dia")
    void deveRetornarBaixo_quandoOrcamentoMinimoEPrazoMinimo() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 2);

        RiskLevel result = service.calculate(new BigDecimal("1000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("MÉDIO: orçamento acima de 100k com prazo curto")
    void deveRetornarMedio_quandoOrcamentoAcimaDoLimiteBaixo() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 2, 1);


        RiskLevel result = service.calculate(new BigDecimal("100001"), start, end);

        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("MÉDIO: prazo entre 3 e 6 meses com orçamento baixo")
    void deveRetornarMedio_quandoPrazoNaFaixaMedia() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 5, 1);

        RiskLevel result = service.calculate(new BigDecimal("50000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("MÉDIO: orçamento no teto do médio (500k) e prazo no teto (6 meses)")
    void deveRetornarMedio_quandoOrcamentoEPrazoEstaoNoLimiteSuperior() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 7, 1);

        RiskLevel result = service.calculate(new BigDecimal("500000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("MÉDIO: orçamento de 300k (faixa média) independente do prazo")
    void deveRetornarMedio_quandoOrcamentoNaFaixaMedia() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 2, 1);

        RiskLevel result = service.calculate(new BigDecimal("300000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }


    @Test
    @DisplayName("ALTO: orçamento acima de 500k com prazo curto")
    void deveRetornarAlto_quandoOrcamentoAcimaDoLimite() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 2, 1);

        RiskLevel result = service.calculate(new BigDecimal("500001"), start, end);

        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    @DisplayName("ALTO: prazo acima de 6 meses com orçamento baixo")
    void deveRetornarAlto_quandoPrazoAcimaDoLimite() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 8, 1);

        RiskLevel result = service.calculate(new BigDecimal("50000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    @DisplayName("ALTO: orçamento e prazo ambos altos")
    void deveRetornarAlto_quandoOrcamentoEPrazoUltrapassamLimites() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);

        RiskLevel result = service.calculate(new BigDecimal("1000000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    @DisplayName("ALTO: prazo exatamente 7 meses (1 acima do limite)")
    void deveRetornarAlto_quandoPrazoDeSeteMeses() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 8, 1);

        RiskLevel result = service.calculate(new BigDecimal("100000"), start, end);

        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }
}