package br.com.loja.estoque.exception;

public class EstoqueException extends Exception {

    public EstoqueException(String mensagem) {
        super(mensagem);
    }

    public EstoqueException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}