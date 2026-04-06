# Portfolio de Projetos — API

Sistema para gerenciamento do portfólio de projetos de uma empresa, desenvolvido como desafio técnico para vaga de Desenvolvedor Java.

---

## Tecnologias utilizadas

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.5 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL |
| Migrations | Flyway |
| Segurança | Spring Security (Basic Auth em memória) |
| Documentação | SpringDoc OpenAPI 2 (Swagger UI) |
| Cliente HTTP | Spring Cloud OpenFeign |
| Resiliência | Resilience4j (Circuit Breaker + Retry + Cache) |
| Mapeamento | ModelMapper |
| Testes | JUnit 5 + Mockito + AssertJ |
| Cobertura | JaCoCo |
| Build | Maven |

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ rodando localmente
- (Opcional) Docker para subir o banco
- (Opcional) Postman para testar os endpoints

---

## Como rodar

### 1. Criar o banco de dados PostgreSQL

```sql
CREATE DATABASE portfolio_db;
```

Ou via Docker:

```bash
docker run -d \
  --name postgres-portfolio \
  -e POSTGRES_DB=portfolio_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16
```

### 2. Configurar credenciais (se necessário)

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/portfolio_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Executar a aplicação

```bash
# Compilar e rodar
mvn spring-boot:run

# Ou gerar o jar e executar
mvn clean package -DskipTests
java -jar target/project-manager-api-0.0.1-SNAPSHOT.jar
```

O Flyway executa automaticamente as migrations na primeira inicialização, criando as tabelas e inserindo dados de demonstração.

**Dados inseridos automaticamente pelo Flyway:**
- **15 membros** com atribuições variadas (funcionário, gerente, analista, consultor)
- **17 projetos** cobrindo todos os status possíveis e as 3 faixas de risco
- **Alocações** de membros nos projetos

---

## Acessar o Swagger UI

```
http://localhost:8080/swagger-ui.html
```

**Credenciais de acesso:**

| Campo | Valor |
|---|---|
| Usuário | `admin` |
| Senha | `admin123` |

Clique no botão **Authorize** (cadeado) no topo da página antes de testar os endpoints protegidos.

---

## Testar com o Postman

A coleção completa do Postman está incluída no repositório:

```
Desafio Técnico - Gestão de Portfólio de Projetos.postman_collection.json
```

### Como importar

1. Abra o Postman
2. Clique em **Import** (canto superior esquerdo)
3. Arraste o arquivo `.json` ou clique em **Upload Files** e selecione-o
4. A coleção aparecerá no painel lateral

### O que está na coleção

A coleção cobre **todos os cenários de sucesso e de erro** da API, organizada em 5 pastas:

| Pasta | Conteúdo |
|---|---|
| 👤 **Membros (Mock)** | Criar, listar e buscar membros; erros de validação e role inválida |
| 📋 **Projetos** | CRUD completo, filtros, paginação, ordenação, transições de status e alocação de membros |
| 📊 **Relatório** | Relatório de portfólio |
| 🔐 **Segurança** | Credenciais inválidas, endpoints públicos vs protegidos |
| 🔄 **Fluxo Completo** | 11 requests em sequência cobrindo o ciclo de vida inteiro de um projeto |

> **Dica:** Execute o **Fluxo Completo** para ver o ciclo inteiro: criar membro → criar projeto → alocar membro → avançar por todos os 7 status → encerrar → verificar relatório.

---

## Endpoints disponíveis

### Projetos — `/api/projects` 🔒

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/projects` | Criar projeto |
| `GET` | `/api/projects` | Listar com paginação e filtros |
| `GET` | `/api/projects/{id}` | Buscar por ID |
| `PUT` | `/api/projects/{id}` | Atualizar projeto |
| `DELETE` | `/api/projects/{id}` | Excluir projeto |
| `PATCH` | `/api/projects/{id}/status` | Alterar status |
| `POST` | `/api/projects/{id}/members` | Alocar membro |
| `DELETE` | `/api/projects/{id}/members/{memberId}` | Remover membro |

### Relatório — `/api/reports` 🔒

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/reports/portfolio` | Relatório resumido do portfólio |

### API de membros (mock) — `/mock/members` 🌐 público

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/mock/members` | Criar membro |
| `GET` | `/mock/members` | Listar membros |
| `GET` | `/mock/members/{id}` | Buscar membro por ID |

> 🔒 Requer `Authorization: Basic YWRtaW46YWRtaW4xMjM=` (admin/admin123)  
> 🌐 Sem autenticação

---

## Regras de negócio

### Classificação de risco (calculada dinamicamente)

| Nível | Orçamento | Prazo |
|---|---|---|
| **Baixo** | ≤ R$ 100.000 | ≤ 3 meses — **ambas as condições** |
| **Médio** | R$ 100.001 – R$ 500.000 | 3 a 6 meses — **qualquer uma** |
| **Alto** | > R$ 500.000 | > 6 meses — **qualquer uma** |

### Status e transições

Sequência obrigatória — não é permitido pular etapas:

```
EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO
```

- `CANCELADO` pode ser aplicado a **qualquer momento**, exceto após `ENCERRADO`
- Projetos com status `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO` **não podem ser excluídos**
- Ao encerrar, o campo `actualEndDate` é preenchido automaticamente com a data atual (se não informado)

### Membros

- Atribuições válidas: `funcionário`, `gerente`, `consultor`, `analista`
- Apenas membros com atribuição `funcionário` podem ser alocados em projetos
- Máximo de **10 membros** por projeto
- Um membro não pode estar em mais de **3 projetos ativos** simultaneamente

### Erros de validação

Todos os erros retornam um corpo JSON padronizado:

```json
{
  "timestamp": "2025-05-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descrição do erro",
  "path": "/api/projects",
  "fieldErrors": {
    "name": "O nome é obrigatório."
  }
}
```

---

## Resiliência — Circuit Breaker (Resilience4j)

As chamadas à API externa de membros utilizam **Circuit Breaker + Retry**:

1. **Retry**: até 3 tentativas automáticas com intervalo exponencial (500ms → 1s → 2s)
2. **Circuit Breaker**: se ≥ 50% das chamadas nas últimas 5 requisições falharem, o circuito **abre** por 10 segundos
3. **Fallback**: enquanto o circuito está aberto, o sistema consulta o **cache em memória**
4. Se o cache estiver vazio, retorna uma mensagem amigável informando indisponibilidade temporária

---

## Internacionalização (i18n)

As mensagens de erro suportam 3 idiomas. Para escolher, envie o header:

```http
Accept-Language: pt-BR   # Português (padrão)
Accept-Language: en      # English
Accept-Language: es      # Español
```

---

## Executar os testes

```bash
# Rodar todos os testes
mvn test

# Rodar com relatório de cobertura (JaCoCo)
mvn test jacoco:report
```

Após rodar com JaCoCo, abra o relatório em:

```
target/site/jacoco/index.html
```

---

## Arquitetura do projeto

```
src/main/java/com/portfolio/
├── client/          # MemberClient (Feign — consome API externa de membros)
├── config/          # SecurityConfig, OpenApiConfig, ModelMapperConfig
├── controller/      # ProjectController, ReportController, MockMemberController
├── dto/
│   ├── request/     # ProjectCreateRequest, StatusChangeRequest, etc. (Records)
│   └── response/    # ProjectResponse, PortfolioReportResponse, etc. (Records)
├── entity/          # Project, ProjectMember, MockMember
├── enums/           # ProjectStatus (máquina de estados), RiskLevel, MemberRole
├── exception/       # Exceções customizadas + GlobalExceptionHandler
├── mapper/          # ProjectMapper, MockMemberMapper
├── repository/      # JpaRepository + Specification para filtros dinâmicos
├── service/
│   ├── impl/        # ProjectServiceImpl, ReportServiceImpl, MemberClientService, etc.
│   └── *.java       # Interfaces (ProjectService, ReportService, etc.)
└── util/            # MessageUtil (i18n)

src/main/resources/
├── db/migration/    # V1–V4 — scripts Flyway (tabelas + dados de demonstração)
└── i18n/            # messages.properties (PT-BR, EN, ES)
```

### Decisões de arquitetura

**Por que OpenFeign para o mock de membros?**
O desafio pede uma API REST externa para membros. O Feign permite declarar o contrato de comunicação de forma limpa — o `ProjectService` chama `memberClient.findById(id)` sem saber que o endpoint está na mesma aplicação. Demonstra uso correto de comunicação entre serviços.

**Por que MemberClientService com Circuit Breaker?**
O Feign gera um proxy dinâmico — anotar a interface diretamente com `@CircuitBreaker`/`@Retry` não funciona. A classe `MemberClientService` envolve o cliente Feign e aplica as políticas de resiliência. O `ProjectService` continua chamando normalmente, sem conhecer os detalhes de resiliência (Open/Closed do SOLID).

**Por que Specification para filtros?**
Evita a explosão de métodos no repository (findByName, findByStatus, findByNameAndStatus...). Com `Specification`, os predicados são compostos dinamicamente — adicionar um novo filtro não exige novos métodos.

**Por que a lógica de transição está no enum ProjectStatus?**
Single Responsibility. O enum representa o estado e sabe quais transições são válidas. Se a regra de transição mudar, só muda aqui — não no service.

**Por que Records para DTOs?**
Records Java geram automaticamente construtor, getters, `equals` e `toString` — zero boilerplate para objetos imutáveis.

**Por que MemberRole é um enum com @JsonCreator?**
Permite aceitar os valores em português (`"funcionário"`, `"gerente"`) no JSON e persistir o mesmo valor no banco via `AttributeConverter`, sem precisar de mapeamentos extras. Valores inválidos geram 400 Bad Request com mensagem descritiva.