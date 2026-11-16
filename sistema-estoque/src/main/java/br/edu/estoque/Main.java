package br.edu.estoque;

import br.edu.estoque.dao.RoupaDaoPostgres;
import br.edu.estoque.dao.UsuarioDaoPostgres;
import br.edu.estoque.service.AutenticacaoService;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.service.Sessao;
import br.edu.estoque.view.TelaLogin;
import br.edu.estoque.view.componentes.Tema;
import javax.swing.SwingUtilities;

/** Ponto de entrada: monta as dependências (DAO → Service → View) e abre a tela de login. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();

            Sessao sessao = new Sessao();
            AutenticacaoService autenticacao = new AutenticacaoService(new UsuarioDaoPostgres(), sessao);
            EstoqueService estoque = new EstoqueService(new RoupaDaoPostgres(), sessao);

            new TelaLogin(autenticacao, estoque).setVisible(true);
        });
    }
}
