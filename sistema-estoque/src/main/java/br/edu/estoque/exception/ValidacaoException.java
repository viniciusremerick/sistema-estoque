package br.edu.estoque.exception;

/** Dados informados pelo usuário violam uma regra de negócio. */
public class ValidacaoException extends Exception {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
