package br.edu.estoque.exception;

/** Falha técnica de acesso ao banco (conexão, SQL, configuração). */
public class DaoException extends RuntimeException {

    private final String sqlState;

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.sqlState = (causa instanceof java.sql.SQLException sql) ? sql.getSQLState() : null;
    }

    public DaoException(String mensagem) {
        super(mensagem);
        this.sqlState = null;
    }

    public String getSqlState() {
        return sqlState;
    }

    /** SQLState 23505 = violação de UNIQUE no PostgreSQL. */
    public boolean isViolacaoDeUnicidade() {
        return "23505".equals(sqlState);
    }
}
