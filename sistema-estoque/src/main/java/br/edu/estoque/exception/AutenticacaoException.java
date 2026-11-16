package br.edu.estoque.exception;

/** Credenciais inválidas ou operação administrativa sem login. */
public class AutenticacaoException extends Exception {
    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
