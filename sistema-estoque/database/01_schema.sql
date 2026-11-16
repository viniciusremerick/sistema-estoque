-- Execute conectado ao banco sistema_estoque:
--   psql -U postgres -d sistema_estoque -f database/01_schema.sql

DROP TABLE IF EXISTS roupa;
DROP TABLE IF EXISTS usuario;

-- Administradores do sistema (senha armazenada apenas como hash PBKDF2)
CREATE TABLE usuario (
    id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    senha_hash  VARCHAR(255) NOT NULL,
    criado_em   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuario_username UNIQUE (username),
    CONSTRAINT ck_usuario_username CHECK (length(trim(username)) > 0)
);

-- Peças de roupa em estoque
CREATE TABLE roupa (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome        VARCHAR(100)  NOT NULL,
    categoria   VARCHAR(50)   NOT NULL,
    tamanho     VARCHAR(2)    NOT NULL,
    cor         VARCHAR(30)   NOT NULL,
    preco       NUMERIC(10,2) NOT NULL,
    quantidade  INTEGER       NOT NULL DEFAULT 0,
    CONSTRAINT ck_roupa_nome       CHECK (length(trim(nome)) > 0),
    CONSTRAINT ck_roupa_categoria  CHECK (length(trim(categoria)) > 0),
    CONSTRAINT ck_roupa_cor        CHECK (length(trim(cor)) > 0),
    CONSTRAINT ck_roupa_tamanho    CHECK (tamanho IN ('P', 'M', 'G', 'GG')),
    CONSTRAINT ck_roupa_preco      CHECK (preco > 0),
    CONSTRAINT ck_roupa_quantidade CHECK (quantidade >= 0),
    -- evita cadastrar duas vezes a mesma peça (mesmo nome, tamanho e cor)
    CONSTRAINT uq_roupa_variacao   UNIQUE (nome, tamanho, cor)
);

CREATE INDEX idx_roupa_categoria  ON roupa (categoria);
CREATE INDEX idx_roupa_tamanho    ON roupa (tamanho);
CREATE INDEX idx_roupa_quantidade ON roupa (quantidade);
