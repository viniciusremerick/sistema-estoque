# Sistema de Estoque — Loja de Roupas

Aplicação desktop (Java 17+ / Swing / JDBC / PostgreSQL / Maven) para controle de estoque de uma loja de roupas.

## Requisitos
- JDK 17 ou superior
- Maven 3.8+
- PostgreSQL 12+ (o script usa `GENERATED ALWAYS AS IDENTITY` e `FILTER`)

## 1. Banco de dados
Na raiz do projeto:

```bash
psql -U postgres -f database/00_criar_banco.sql
psql -U postgres -d sistema_estoque -f database/01_schema.sql
psql -U postgres -d sistema_estoque -f database/02_dados_iniciais.sql
```

> Windows: antes de rodar o `psql`, execute `set PGCLIENTENCODING=UTF8` para os acentos dos dados de teste
> ficarem corretos. Alternativa: abrir os `.sql` no pgAdmin/DBeaver e executar na ordem.

## 2. Configuração da conexão
Copie o modelo e ajuste usuário/senha do **seu** PostgreSQL:

```bash
cp config/db.properties.example config/db.properties      # Windows: copy ...
```

```properties
db.url=jdbc:postgresql://localhost:5432/sistema_estoque
db.user=postgres
db.password=SUA_SENHA
```

Alternativa sem arquivo: variáveis de ambiente `DB_URL`, `DB_USER`, `DB_PASSWORD` (têm prioridade).
O arquivo `config/db.properties` está no `.gitignore` — não o versione.

## 3. Executar
Sempre a partir da **raiz do projeto** (é de onde `config/db.properties` é lido):

```bash
mvn compile exec:java          # executa direto
# ou
mvn package
java -jar target/sistema-estoque.jar
```

No IntelliJ/Eclipse: importe como projeto Maven e execute `br.edu.estoque.Main`
(defina a pasta de trabalho como a raiz do projeto).

## Login inicial
| Usuário | Senha |
|---------|-------|
| `admin` | `admin123` |

Para criar o hash de uma nova senha:

```bash
java -cp target/sistema-estoque.jar br.edu.estoque.util.SenhaUtil minhaNovaSenha
```
```sql
UPDATE usuario SET senha_hash = '<hash gerado>' WHERE username = 'admin';
```

## Estrutura
```
sistema-estoque/
├── pom.xml
├── config/db.properties.example
├── database/                      00_criar_banco.sql · 01_schema.sql · 02_dados_iniciais.sql
└── src/main/java/br/edu/estoque/
    ├── Main.java                  monta as dependências e abre o login
    ├── model/                     Produto (abstrata) → Roupa · Tamanho · Usuario · FiltroRoupa · ResumoEstoque
    ├── dao/                       RoupaDao / UsuarioDao (interfaces) + implementações PostgreSQL (PreparedStatement)
    ├── service/                   EstoqueService · AutenticacaoService · Sessao (regras de negócio e autorização)
    ├── view/                      TelaLogin · TelaPrincipal · PainelDashboard · PainelCadastro · PainelEstoque · PainelSemEstoque
    │   └── componentes/           Tema · BotaoModerno · TabelaRoupas · DialogoQuantidade · Async · Mensagens ...
    ├── connection/                ConfiguracaoBanco · ConexaoFactory (conexão JDBC centralizada)
    ├── util/                      Validador · SenhaUtil (PBKDF2-HMAC-SHA256) · Formatador
    └── exception/                 ValidacaoException · AutenticacaoException · DaoException
```

## Requisitos funcionais
| RF | Onde |
|----|------|
| RF-01 Cadastrar roupa | tela *Cadastrar roupa* → `EstoqueService.cadastrarRoupa` → `RoupaDaoPostgres.inserir` |
| RF-02 Buscar e filtrar | tela *Estoque* (nome, tamanho P/M/G/GG, categoria — combináveis) → `RoupaDaoPostgres.listar` |
| RF-03 Login do admin | `TelaLogin` + `AutenticacaoService` (hash PBKDF2); o `EstoqueService` recusa alterações sem sessão |
| RF-04 Peças zeradas | tela *Produtos sem estoque* → `listarSemEstoque` (`quantidade = 0`) |
| RF-05 Atualizar peças | tela *Estoque* (botão ou duplo clique na linha) → `atualizarQuantidade` |

## Regras de validação
Campos obrigatórios · preço > 0 (aceita `49,90`, `49.90`, `R$ 1.249,90`) · quantidade ≥ 0 e inteira ·
tamanho apenas P, M, G ou GG · a mesma combinação nome + tamanho + cor não pode ser cadastrada duas vezes.
As mesmas regras existem como `CHECK`/`UNIQUE` no banco (defesa em profundidade).
