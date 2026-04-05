CREATE TABLE mock_members
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    role VARCHAR(100) NOT NULL
);

COMMENT
ON TABLE mock_members IS 'Simula o banco de dados da API externa de membros.';
COMMENT
ON COLUMN mock_members.role IS 'Atribuição do membro. Ex: funcionário, gerente. Apenas ''funcionário'' pode ser alocado em projetos.';

INSERT INTO mock_members (name, role)
VALUES ('João Silva', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Maria Oliveira', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Carlos Souza', 'gerente');
INSERT INTO mock_members (name, role)
VALUES ('Ana Costa', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Pedro Lima', 'funcionário');