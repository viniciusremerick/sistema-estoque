package br.com.loja.estoque;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;
import br.com.loja.estoque.view.CadastroPanel;
import br.com.loja.estoque.view.EstoquePanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Todo código de tela do Swing deve rodar na thread própria dele
        SwingUtilities.invokeLater(Main::abrirJanela);
    }

    private static void abrirJanela() {
        AuthService auth = new AuthService();
        RoupaService service = new RoupaService(auth);

        fazerLoginDeTeste(auth);

        EstoquePanel estoque = new EstoquePanel(service);
        CadastroPanel cadastro = new CadastroPanel(service);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Estoque", estoque);
        abas.addTab("Cadastrar roupa", cadastro);
        abas.addChangeListener(e -> estoque.pesquisar()); // atualiza ao trocar de aba

        JFrame janela = new JFrame("Estoque de Roupas (teste da Etapa 8)");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(900, 500);
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