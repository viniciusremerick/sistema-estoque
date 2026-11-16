package br.edu.estoque.service;

import br.edu.estoque.dao.UsuarioDao;
import br.edu.estoque.exception.AutenticacaoException;
import br.edu.estoque.model.Usuario;
import br.edu.estoque.util.SenhaUtil;
import java.util.Optional;

/** Regras de login/logout do administrador. */
public class AutenticacaoService {

    private static final String MENSAGEM_INVALIDA = "Usuário ou senha inválidos.";

    private final UsuarioDao usuarioDao;
    private final Sessao sessao;

    public AutenticacaoService(UsuarioDao usuarioDao, Sessao sessao) {
        this.usuarioDao = usuarioDao;
        this.sessao = sessao;
    }

    public Usuario login(String username, char[] senha) throws AutenticacaoException {
        if (username == null || username.isBlank() || senha == null || senha.length == 0) {
            throw new AutenticacaoException("Informe usuário e senha.");
        }
        Optional<Usuario> encontrado = usuarioDao.buscarPorUsername(username.trim());
        // Mesma mensagem para "usuário inexistente" e "senha errada" (não revela quais usuários existem).
        if (encontrado.isEmpty() || !SenhaUtil.verificar(senha, encontrado.get().getSenhaHash())) {
            throw new AutenticacaoException(MENSAGEM_INVALIDA);
        }
        sessao.iniciar(encontrado.get());
        return encontrado.get();
    }

    public void logout() {
        sessao.encerrar();
    }

    public String getUsuarioLogado() {
        Usuario usuario = sessao.getUsuario();
        return usuario == null ? "" : usuario.getUsername();
    }
}
