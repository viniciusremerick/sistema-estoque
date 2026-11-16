package br.com.loja.estoque.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.loja.estoque.dao.AdministradorDAO;
import br.com.loja.estoque.exception.AutenticacaoException;
import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.exception.ValidacaoException;
import br.com.loja.estoque.model.Administrador;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class AuthService {

    private static final int CUSTO_BCRYPT = 12;
    private static final int SENHA_MINIMA = 6;
    private static final int SENHA_MAXIMA_BYTES = 72; // limite do algoritmo BCrypt

    private final AdministradorDAO administradorDAO;
    private Administrador administradorLogado; // a "sessão"

    public AuthService() {
        this(new AdministradorDAO());
    }

    public AuthService(AdministradorDAO administradorDAO) {
        this.administradorDAO = administradorDAO;
    }

    // ========== CADASTRO DE ADMINISTRADOR ==========

    public void cadastrarAdministrador(String usuario, String senha) throws EstoqueException {
        // O primeiro administrador é livre; os demais exigem login
        if (existeAdministrador()) {
            exigirAutenticacao();
        }

        String usuarioOk = validarUsuario(usuario);
        validarSenha(senha);

        if (administradorDAO.buscarPorUsuario(usuarioOk).isPresent()) {
            throw new ValidacaoException("Já existe um administrador com esse usuário.");
        }

        String hash = BCrypt.withDefaults().hashToString(CUSTO_BCRYPT, senha.toCharArray());
        administradorDAO.inserir(new Administrador(usuarioOk, hash));
    }

    public boolean existeAdministrador() throws EstoqueException {
        return !administradorDAO.listarTodos().isEmpty();
    }

    // ========== LOGIN / LOGOUT ==========

    public void login(String usuario, String senha) throws EstoqueException {
        if (usuario == null || usuario.isBlank() || senha == null || senha.isEmpty()) {
            throw new ValidacaoException("Informe usuário e senha.");
        }

        Optional<Administrador> encontrado = administradorDAO.buscarPorUsuario(usuario.trim());

        boolean credenciaisValidas = encontrado.isPresent()
                && senhaConfere(senha, encontrado.get().getSenhaHash());

        if (!credenciaisValidas) {
            // Mesma mensagem para usuário inexistente e senha errada
            throw new AutenticacaoException("Usuário ou senha inválidos.");
        }

        administradorLogado = encontrado.get();
    }

    public void logout() {
        administradorLogado = null;
    }

    public boolean estaAutenticado() {
        return administradorLogado != null;
    }

    public Administrador getAdministradorLogado() {
        return administradorLogado;
    }

    // Chamado pelos outros Services antes de operações restritas
    public void exigirAutenticacao() throws AutenticacaoException {
        if (!estaAutenticado()) {
            throw new AutenticacaoException("Acesso negado: faça login como administrador.");
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    private boolean senhaConfere(String senhaDigitada, String hashGuardado) {
        if (senhaDigitada.getBytes(StandardCharsets.UTF_8).length > SENHA_MAXIMA_BYTES) {
            return false;
        }
        return BCrypt.verifyer()
                .verify(senhaDigitada.toCharArray(), hashGuardado)
                .verified;
    }

    private String validarUsuario(String usuario) throws ValidacaoException {
        if (usuario == null || usuario.isBlank()) {
            throw new ValidacaoException("Usuário é obrigatório.");
        }
        String limpo = usuario.trim();
        if (limpo.length() < 3 || limpo.length() > 50) {
            throw new ValidacaoException("O usuário deve ter entre 3 e 50 caracteres.");
        }
        return limpo;
    }

    private void validarSenha(String senha) throws ValidacaoException {
        if (senha == null || senha.isEmpty()) {
            throw new ValidacaoException("Senha é obrigatória.");
        }
        if (senha.length() < SENHA_MINIMA) {
            throw new ValidacaoException(
                    "A senha deve ter pelo menos " + SENHA_MINIMA + " caracteres.");
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > SENHA_MAXIMA_BYTES) {
            throw new ValidacaoException("A senha é longa demais (máximo de 72 bytes).");
        }
    }
}