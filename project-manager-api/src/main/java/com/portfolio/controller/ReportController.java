package com.portfolio.controller;

import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Relatórios do portfólio de projetos")
@SecurityRequirement(name = "basicAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/portfolio")
    @Operation(
            summary = "Relatório resumido do portfólio",
            description = """
                    Retorna:
                    - Quantidade de projetos por status
                    - Total orçado por status
                    - Média de duração dos projetos encerrados (em dias)
                    - Total de membros únicos alocados
                    """
    )
    public ResponseEntity<PortfolioReportResponse> portfolioReport() {
        return ResponseEntity.ok(reportService.generatePortfolioReport());
    }
}