package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    private final AuthService auth;
    private final RoupaService service;

    private final JTextField campoUsuario = new JTextField(18);
    private final JPasswordField campoSenha = new JPasswordField(18);
    private final JButton botaoEntrar = new JButton("Entrar");
    private final JButton botaoVisitante =
            new JButton("Continuar sem login (somente consulta)");
    private final JButton botaoPrimeiroAcesso =
            new JButton("Primeiro acesso: criar administrador");

    public LoginFrame(AuthService auth, RoupaService service) {
        super("Estoque de Roupas - Login");
        this.auth = auth;
        this.service = service;

        montarTela();
        verificarPrimeiroAcesso();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel titulo = new JLabel("Estoque de Roupas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        JLabel subtitulo = new JLabel("Acesso do administrador", SwingConstants.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);

        c.gridy = 0;
        painel.add(titulo, c);
        c.gridy = 1;
        painel.add(subtitulo, c);

        adicionarLinha(painel, 2, "Usuário:", campoUsuario);
        adicionarLinha(painel, 3, "Senha:", campoSenha);

        c.insets = new Insets(10, 4, 4, 4);
        c.gridy = 4;
        painel.add(botaoEntrar, c);
        c.insets = new Insets(4, 4, 4, 4);
        c.gridy = 5;
        painel.add(botaoVisitante, c);
        c.gridy = 6;
        painel.add(botaoPrimeiroAcesso, c);

        add(painel);

        // Enter em qualquer campo aciona o botão "Entrar"
        getRootPane().setDefaultButton(botaoEntrar);

        botaoEntrar.addActionListener(e -> entrar());
        botaoVisitante.addActionListener(e -> entrarComoVisitante());
        botaoPrimeiroAcesso.addActionListener(e -> criarPrimeiroAdministrador());
    }

    private void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridy = linha;

        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        painel.add(new JLabel(rotulo), c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        painel.add(campo, c);
    }

    // O botão de primeiro acesso só aparece se ainda não existe administrador
    private void verificarPrimeiroAcesso() {
        try {
            botaoPrimeiroAcesso.setVisible(!auth.existeAdministrador());
        } catch (EstoqueException e) {
            botaoPrimeiroAcesso.setVisible(false);
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro ao acessar o banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void entrar() {
        try {
            auth.login(campoUsuario.getText(), new String(campoSenha.getPassword()));
            abrirJanelaPrincipal();
        } catch (EstoqueException e) {
            campoSenha.setText("");
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Login", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void entrarComoVisitante() {
        auth.logout();
        abrirJanelaPrincipal();
    }

    private void abrirJanelaPrincipal() {
        new JanelaPrincipal(auth, service).setVisible(true);
        dispose(); // fecha a tela de login
    }

    private void criarPrimeiroAdministrador() {
        JTextField usuario = new JTextField();
        JPasswordField senha = new JPasswordField();
        JPasswordField confirmar = new JPasswordField();
        Object[] campos = {
                "Usuário (mín. 3 caracteres):", usuario,
                "Senha (mín. 6 caracteres):", senha,
                "Confirmar senha:", confirmar};

        int opcao = JOptionPane.showConfirmDialog(this, campos,
                "Criar administrador", JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        String senhaDigitada = new String(senha.getPassword());
        String senhaConfirmada = new String(confirmar.getPassword());

        if (!senhaDigitada.equals(senhaConfirmada)) {
            JOptionPane.showMessageDialog(this, "As senhas não conferem.",
                    "Criar administrador", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            auth.cadastrarAdministrador(usuario.getText(), senhaDigitada);
            JOptionPane.showMessageDialog(this,
                    "Administrador criado. Agora faça o login.",
                    "Criar administrador", JOptionPane.INFORMATION_MESSAGE);
            campoUsuario.setText(usuario.getText().trim());
            botaoPrimeiroAcesso.setVisible(false);
            pack();
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível criar", JOptionPane.WARNING_MESSAGE);
        }
    }
}