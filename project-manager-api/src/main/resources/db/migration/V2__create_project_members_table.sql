CREATE TABLE project_members
(
    id           BIGSERIAL PRIMARY KEY,
    project_id   BIGINT NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    member_id    BIGINT NOT NULL,
    allocated_at DATE   NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT uq_project_member UNIQUE (project_id, member_id)
);

COMMENT
ON TABLE project_members IS 'Associação entre projetos e membros alocados.';
COMMENT
ON COLUMN project_members.member_id IS 'ID do membro na API externa de membros (não é FK para tabela local).';