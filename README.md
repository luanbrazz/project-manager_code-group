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
| Mapeamento | ModelMapper |
| Testes | JUnit 5 + Mockito + AssertJ |
| Build | Maven |

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ rodando localmente
- (Opcional) Docker para subir o banco

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

O Flyway criará automaticamente as tabelas e inserirá os dados iniciais na primeira execução.

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

Clique no botão **Authorize** (cadeado) no topo da página e informe as credenciais antes de testar os endpoints.

---

## Fluxo de teste sugerido (Postman ou Swagger)

### Passo 1 — Criar membros (via API mockada, sem autenticação)

```http
POST http://localhost:8080/mock/members
Content-Type: application/json

{ "name": "Ana Costa", "role": "funcionário" }
```

```http
POST http://localhost:8080/mock/members
Content-Type: application/json

{ "name": "Carlos Souza", "role": "gerente" }
```

> **Nota:** O V3 do Flyway já insere 5 membros iniciais (IDs 1–5). Você pode usá-los diretamente.
> - IDs 1, 2, 4, 5 → `funcionário`  
> - ID 3 → `gerente`

### Passo 2 — Criar um projeto (com autenticação Basic Auth: admin/admin123)

```http
POST http://localhost:8080/api/projects
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "name": "Sistema de RH",
  "startDate": "2024-01-01",
  "expectedEndDate": "2024-04-01",
  "budget": 80000.00,
  "description": "Projeto de implementação do sistema de RH",
  "managerId": 3
}
```

### Passo 3 — Alocar membros ao projeto

```http
POST http://localhost:8080/api/projects/{id}/members
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{ "memberId": 1 }
```

### Passo 4 — Avançar o status do projeto

```http
PATCH http://localhost:8080/api/projects/{id}/status
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{ "newStatus": "ANALISE_REALIZADA" }
```

**Sequência válida de status:**
```
EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO
```
`CANCELADO` pode ser aplicado a qualquer momento (exceto após ENCERRADO).

### Passo 5 — Listar projetos com filtros e paginação

```http
GET http://localhost:8080/api/projects?name=Sistema&status=EM_ANALISE&page=0&size=10&sort=name
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Passo 6 — Ver relatório do portfólio

```http
GET http://localhost:8080/api/reports/portfolio
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

---

## Endpoints disponíveis

### Projetos — `/api/projects`

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

### Relatório — `/api/reports`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/reports/portfolio` | Relatório resumido do portfólio |

### API de membros (mock) — `/mock/members` (sem autenticação)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/mock/members` | Criar membro |
| `GET` | `/mock/members` | Listar membros |
| `GET` | `/mock/members/{id}` | Buscar membro por ID |

---

## Regras de negócio implementadas

### Classificação de risco (calculada dinamicamente)

| Nível | Orçamento | Prazo |
|---|---|---|
| Baixo | ≤ R$ 100.000 | ≤ 3 meses (ambas as condições) |
| Médio | R$ 100.001 – R$ 500.000 | 3 a 6 meses (qualquer uma) |
| Alto | > R$ 500.000 | > 6 meses (qualquer uma) |

### Status e transições

- A sequência é obrigatória — não é permitido pular etapas
- `CANCELADO` pode ser aplicado de qualquer status ativo
- Projetos com status `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO` não podem ser excluídos

### Alocação de membros

- Apenas membros com atribuição `funcionário` podem ser alocados
- Mínimo: 1 membro por projeto / Máximo: 10 membros por projeto
- Um membro não pode estar em mais de 3 projetos ativos simultaneamente

---

## Executar os testes

```bash
# Rodar todos os testes
mvn test

# Rodar com relatório de cobertura (JaCoCo)
mvn test jacoco:report

# Ver o relatório de cobertura
# Abrir: target/site/jacoco/index.html
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
├── enums/           # ProjectStatus (com máquina de estados), RiskLevel
├── exception/       # Exceções customizadas + GlobalExceptionHandler
├── mapper/          # ProjectMapper, MockMemberMapper (ModelMapper)
├── repository/      # JpaRepository + Specification para filtros dinâmicos
├── service/
│   ├── impl/        # ProjectServiceImpl, ReportServiceImpl, etc.
│   └── *.java       # Interfaces (BaseService, ProjectService, etc.)
└── util/            # MessageUtil (i18n)

src/main/resources/
├── db/migration/    # V1, V2, V3 — scripts Flyway
└── i18n/            # messages.properties (PT-BR), messages_en.properties, messages_es.properties
```

### Decisões de arquitetura

**Por que OpenFeign para o mock de membros?**
O desafio pede uma API externa para membros. O Feign permite declarar a interface de comunicação de forma limpa — o `ProjectService` chama `memberClient.findById(id)` sem saber que o endpoint está na mesma aplicação. Demonstra uso correto de comunicação entre serviços.

**Por que Specification para filtros?**
Evita a explosão de métodos no repository (findByName, findByStatus, findByNameAndStatus...). Com Specification, os filtros são compostos dinamicamente — adicionar um novo filtro não quebra nada.

**Por que a lógica de transição está no enum ProjectStatus?**
SOLID — cada classe tem uma razão para mudar. O enum `ProjectStatus` sabe quais transições são válidas porque é ele que representa o estado. Se a regra de transição mudar, só muda aqui.

**Por que Records para DTOs?**
Records Java (Java 16+) geram automaticamente construtor, getters, equals e toString — zero boilerplate para objetos imutáveis como DTOs.

---

## i18n — Internacionalização

As mensagens de erro suportam 3 idiomas. Para usar, envie o header:

```http
Accept-Language: pt-BR   # Português (padrão)
Accept-Language: en      # English
Accept-Language: es      # Español
```
