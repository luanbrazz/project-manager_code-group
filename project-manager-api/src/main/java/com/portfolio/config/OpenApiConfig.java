package com.portfolio.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio de Projetos — API")
                        .version("1.0.0")
                        .description("""
                                Sistema de gerenciamento do portfólio de projetos.
                                
                                **Credenciais:** `admin` / `admin123`
                                
                                Clique em **Authorize** (cadeado) antes de testar os endpoints.
                                
                                **Fluxo de teste sugerido:**
                                1. POST /mock/members — criar membros (funcionário/gerente)
                                2. POST /api/projects — criar projeto informando managerId
                                3. POST /api/projects/{id}/members — alocar membro
                                4. PATCH /api/projects/{id}/status — avançar status
                                5. GET /api/reports/portfolio — ver relatório
                                """)
                );
    }
}