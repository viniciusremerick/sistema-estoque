package br.edu.estoque.service;

import br.edu.estoque.model.Usuario;

/** Guarda o administrador autenticado durante a execução do programa. */
public class Sessao {

    private Usuario usuarioLogado;

    public synchronized void iniciar(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public synchronized void encerrar() {
        this.usuarioLogado = null;
    }

    public synchronized boolean isAutenticado() {
        return usuarioLogado != null;
    }

    public synchronized Usuario getUsuario() {
        return usuarioLogado;
    }
}
