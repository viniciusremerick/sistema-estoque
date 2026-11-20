package br.com.loja.estoque;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;
import br.com.loja.estoque.view.CadastroPanel;
import br.com.loja.estoque.view.EstoquePanel;
import br.com.loja.estoque.view.ZeradasPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::abrirJanela);
    }

    private static void abrirJanela() {
        AuthService auth = new AuthService();
        RoupaService service = new RoupaService(auth);

        fazerLoginDeTeste(auth);

        EstoquePanel estoque = new EstoquePanel(service);
        ZeradasPanel zeradas = new ZeradasPanel(service);
        CadastroPanel cadastro = new CadastroPanel(service);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Estoque", estoque);
        abas.addTab("Peças zeradas", zeradas);
        abas.addTab("Cadastrar roupa", cadastro);

        // Ao trocar de aba, as listas são recarregadas
        abas.addChangeListener(e -> {
            estoque.pesquisar();
            zeradas.carregar();
        });

        JFrame janela = new JFrame("Estoque de Roupas (teste da Etapa 9)");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(950, 520);
        janela.setLocationRelativeTo(null);
        janela.add(abas);
        janela.setVisible(true);
    }

    // Login provisório; a tela de login de verdade vem na Etapa 10
    private static void fazerLoginDeTeste(AuthService auth) {
        JTextField campoUsuario = new JTextField();
        JPasswordField campoSenha = new JPasswordField();
        Object[] campos = {"Usuário:", campoUsuario, "Senha:", campoSenha};

        int opcao = JOptionPane.showConfirmDialog(null, campos,
                "Login do administrador (teste)", JOptionPane.OK_CANCEL_OPTION);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                auth.login(campoUsuario.getText(), new String(campoSenha.getPassword()));
            } catch (EstoqueException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),
                        "Login", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}