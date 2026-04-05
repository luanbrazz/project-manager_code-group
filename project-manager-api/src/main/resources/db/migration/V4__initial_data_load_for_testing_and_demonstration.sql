INSERT INTO mock_members (name, role)
VALUES ('Fernanda Torres', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Ricardo Alves', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Camila Nunes', 'analista');
INSERT INTO mock_members (name, role)
VALUES ('Marcos Ribeiro', 'consultor');
INSERT INTO mock_members (name, role)
VALUES ('Beatriz Carvalho', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Lucas Mendes', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Sofia Gomes', 'gerente');
INSERT INTO mock_members (name, role)
VALUES ('Thiago Barbosa', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Juliana Castro', 'funcionário');
INSERT INTO mock_members (name, role)
VALUES ('Eduardo Ferreira', 'analista');

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Atualização do site institucional',
        '2025-04-01', '2025-06-01',
        75000.00,
        'Redesign e atualização de conteúdo do portal institucional da empresa.',
        3, 'EM_ANALISE', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Implantação de BI corporativo',
        '2025-05-01', '2025-08-01',
        320000.00,
        'Implantação de plataforma de Business Intelligence com dashboards para diretoria.',
        12, 'EM_ANALISE', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Migração de e-mail corporativo',
        '2025-03-01', '2025-05-15',
        45000.00,
        'Migração das contas de e-mail para o Microsoft 365.',
        3, 'ANALISE_REALIZADA', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Desenvolvimento do app mobile',
        '2025-01-01', '2025-10-31',
        480000.00,
        'Aplicativo mobile para clientes: iOS e Android. Inclui integração com ERP.',
        12, 'ANALISE_APROVADA', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Sistema ERP — módulo financeiro',
        '2024-09-01', '2025-06-30',
        850000.00,
        'Implementação do módulo financeiro do ERP SAP: contas a pagar, receber e conciliação.',
        3, 'INICIADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Automação de processos de RH',
        '2025-02-01', '2025-06-01',
        210000.00,
        'Automatização do processo de folha de pagamento e gestão de férias.',
        12, 'INICIADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Migração para cloud AWS',
        '2024-11-01', '2025-08-31',
        1200000.00,
        'Migração completa da infraestrutura on-premise para AWS. Inclui treinamento de equipe.',
        3, 'PLANEJADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Documentação técnica de APIs',
        '2025-04-01', '2025-06-15',
        38000.00,
        'Elaboração e publicação da documentação técnica (OpenAPI) de todas as APIs internas.',
        12, 'PLANEJADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Plataforma de e-learning',
        '2024-06-01', '2025-03-31',
        650000.00,
        'Desenvolvimento de LMS próprio com cursos, avaliações e emissão de certificados.',
        3, 'EM_ANDAMENTO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Integração com marketplace',
        '2025-01-15', '2025-05-15',
        250000.00,
        'Integração do sistema de vendas com os principais marketplaces nacionais.',
        12, 'EM_ANDAMENTO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Dashboard de indicadores operacionais',
        '2025-03-01', '2025-05-31',
        95000.00,
        'Criação de painel de indicadores para o time de operações.',
        3, 'EM_ANDAMENTO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, actual_end_date, budget, description, manager_id, status,
                      created_at)
VALUES ('Migração do banco legado',
        '2024-01-02', '2024-05-31', '2024-06-10',
        430000.00,
        'Migração do banco Oracle para PostgreSQL. Inclui reescrita de stored procedures.',
        3, 'ENCERRADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, actual_end_date, budget, description, manager_id, status,
                      created_at)
VALUES ('Certificação ISO 27001',
        '2023-07-01', '2024-01-31', '2024-03-15',
        185000.00,
        'Processo de certificação de segurança da informação ISO 27001.',
        12, 'ENCERRADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, actual_end_date, budget, description, manager_id, status,
                      created_at)
VALUES ('Novo sistema de PDV',
        '2023-03-01', '2023-12-31', '2024-01-20',
        920000.00,
        'Sistema de ponto de venda com integração fiscal e gestão de estoque em tempo real.',
        3, 'ENCERRADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, actual_end_date, budget, description, manager_id, status,
                      created_at)
VALUES ('Treinamento em metodologias ágeis',
        '2024-08-01', '2024-09-30', '2024-09-25',
        28000.00,
        'Capacitação de 40 colaboradores em Scrum e Kanban.',
        12, 'ENCERRADO', NOW());



INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Portal do fornecedor v2',
        '2024-04-01', '2024-10-31',
        360000.00,
        'Nova versão do portal de fornecedores. Cancelado por mudança de estratégia.',
        3, 'CANCELADO', NOW());

INSERT INTO projects (name, start_date, expected_end_date, budget, description, manager_id, status, created_at)
VALUES ('Chatbot de atendimento',
        '2024-02-01', '2024-06-30',
        120000.00,
        'Chatbot para atendimento de primeiro nível. Cancelado após POC negativo.',
        12, 'CANCELADO', NOW());

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (1, 1, '2025-04-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (1, 2, '2025-04-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (2, 4, '2025-05-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (2, 6, '2025-05-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (2, 7, '2025-05-02');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (3, 5, '2025-03-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (4, 10, '2025-01-10');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (4, 11, '2025-01-10');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (4, 13, '2025-01-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (4, 14, '2025-01-15');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (5, 1, '2024-09-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (5, 2, '2024-09-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (5, 4, '2024-09-05');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (5, 5, '2024-09-05');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (5, 6, '2024-09-10');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (6, 7, '2025-02-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (6, 10, '2025-02-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (7, 11, '2024-11-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (7, 13, '2024-11-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (7, 14, '2024-11-05');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (8, 2, '2025-04-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 1, '2024-06-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 4, '2024-06-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 6, '2024-06-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 7, '2024-06-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 10, '2024-07-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (9, 11, '2024-07-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (10, 5, '2025-01-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (10, 13, '2025-01-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (10, 14, '2025-01-20');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (11, 2, '2025-03-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (11, 7, '2025-03-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (12, 1, '2024-01-02');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (12, 5, '2024-01-02');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (12, 11, '2024-01-10');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (12, 14, '2024-01-10');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (13, 4, '2023-07-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (13, 6, '2023-07-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (13, 13, '2023-07-15');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (14, 2, '2023-03-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (14, 7, '2023-03-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (14, 10, '2023-03-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (14, 11, '2023-03-15');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (14, 13, '2023-04-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (15, 5, '2024-08-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (15, 14, '2024-08-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (16, 6, '2024-04-01');
INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (16, 10, '2024-04-01');

INSERT INTO project_members (project_id, member_id, allocated_at)
VALUES (17, 4, '2024-02-01');
