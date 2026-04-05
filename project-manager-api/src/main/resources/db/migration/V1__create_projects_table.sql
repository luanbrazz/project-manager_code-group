CREATE TABLE projects
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(200)   NOT NULL,
    start_date        DATE           NOT NULL,
    expected_end_date DATE           NOT NULL,
    actual_end_date   DATE,
    budget            NUMERIC(15, 2) NOT NULL,
    description       TEXT,
    manager_id        BIGINT         NOT NULL,
    status            VARCHAR(30)    NOT NULL DEFAULT 'EM_ANALISE',
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP
);

COMMENT
ON TABLE projects IS 'Tabela principal de projetos do portfólio.';
COMMENT
ON COLUMN projects.status IS 'Status do projeto: EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA, INICIADO, PLANEJADO, EM_ANDAMENTO, ENCERRADO, CANCELADO';
COMMENT
ON COLUMN projects.manager_id IS 'ID do gerente responsável (referência à API externa de membros)';