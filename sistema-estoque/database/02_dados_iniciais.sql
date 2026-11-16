-- Execute conectado ao banco sistema_estoque, depois do 01_schema.sql:
--   psql -U postgres -d sistema_estoque -f database/02_dados_iniciais.sql

-- Administrador inicial:  usuário "admin"  /  senha "admin123"
-- (troque a senha depois; veja o README para gerar um novo hash)
INSERT INTO usuario (username, senha_hash) VALUES
    ('admin', 'pbkdf2_sha256$65536$cKpy4+IKVPKh1dMZqMIOQg==$S6EFc4nublXBK1GN1mm2fo0Itt2Lom23g+kjeOagBE8=');

-- Peças de demonstração (algumas com estoque zerado para testar o alerta)
INSERT INTO roupa (nome, categoria, tamanho, cor, preco, quantidade) VALUES
    ('Camiseta Básica Algodão', 'Camiseta', 'P',  'Branca',     39.90, 25),
    ('Camiseta Básica Algodão', 'Camiseta', 'M',  'Branca',     39.90, 40),
    ('Camiseta Básica Algodão', 'Camiseta', 'G',  'Branca',     39.90,  0),
    ('Camiseta Estampada Surf', 'Camiseta', 'M',  'Azul',       59.90, 12),
    ('Camiseta Estampada Surf', 'Camiseta', 'GG', 'Azul',       59.90,  0),
    ('Calça Jeans Slim',        'Calça',    'M',  'Azul Escuro', 129.90, 8),
    ('Calça Jeans Slim',        'Calça',    'G',  'Azul Escuro', 129.90, 5),
    ('Calça Jeans Slim',        'Calça',    'GG', 'Preta',      139.90, 0),
    ('Calça Moletom Jogger',    'Calça',    'P',  'Cinza',       99.90, 14),
    ('Vestido Midi Floral',     'Vestido',  'P',  'Rosa',       159.90,  6),
    ('Vestido Midi Floral',     'Vestido',  'M',  'Rosa',       159.90,  9),
    ('Vestido Longo Festa',     'Vestido',  'G',  'Vinho',      249.90,  3),
    ('Jaqueta Corta-Vento',     'Jaqueta',  'M',  'Preta',      189.90, 10),
    ('Jaqueta Corta-Vento',     'Jaqueta',  'GG', 'Preta',      189.90,  0),
    ('Short Praia Listrado',    'Short',    'M',  'Verde',       69.90, 18),
    ('Short Praia Listrado',    'Short',    'G',  'Verde',       69.90, 22),
    ('Blusa de Frio Tricot',    'Blusa',    'P',  'Bege',       119.90,  7),
    ('Blusa de Frio Tricot',    'Blusa',    'M',  'Bege',       119.90, 11),
    ('Saia Plissada',           'Saia',     'P',  'Preta',       89.90,  4),
    ('Saia Plissada',           'Saia',     'M',  'Preta',       89.90,  0);
