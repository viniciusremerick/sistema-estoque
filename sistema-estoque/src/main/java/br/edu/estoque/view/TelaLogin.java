package br.edu.estoque.view;

import br.edu.estoque.exception.AutenticacaoException;
import br.edu.estoque.service.AutenticacaoService;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.view.componentes.Async;
import br.edu.estoque.view.componentes.BotaoModerno;
import br.edu.estoque.view.componentes.Mensagens;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/** Tela 1 — autenticação do administrador. */
public class TelaLogin extends JFrame {

    private final AutenticacaoService autenticacao;
    private final EstoqueService estoque;

    private final JTextField campoUsuario = Tema.campo(20);
    private final JPasswordField campoSenha = new JPasswordField(20);
    private final JLabel rotuloErro = new JLabel(" ");
    private final BotaoModerno botaoEntrar = new BotaoModerno("Entrar", Tema.PRIMARIA);

    public TelaLogin(AutenticacaoService autenticacao, EstoqueService estoque) {
        super("Sistema de Estoque — Login");
        this.autenticacao = autenticacao;
        this.estoque = estoque;
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(820, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarPainelMarca(), BorderLayout.WEST);
        raiz.add(criarPainelFormulario(), BorderLayout.CENTER);
        setContentPane(raiz);

        getRootPane().setDefaultButton(botaoEntrar);
        botaoEntrar.addActionListener(e -> entrar());
    }

    private JPanel criarPainelMarca() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Tema.SIDEBAR);
        painel.setPreferredSize(new Dimension(320, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(150, 34, 0, 34));

        JLabel nome = new JLabel("Estoque Moda");
        nome.setFont(Tema.FONTE_TITULO.deriveFont(30f));
        nome.setForeground(Color.WHITE);
        JLabel descricao = new JLabel("<html>Controle de estoque da sua loja de roupas, simples e organizado.</html>");
        descricao.setForeground(new Color(0xCBD5E1));

        painel.add(nome);
        painel.add(javax.swing.Box.createVerticalStrut(14));
        painel.add(descricao);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 60, 4, 60);

        campoSenha.setBorder(Tema.bordaCampo());

        painel.add(Tema.titulo("Entrar"), c);
        c.insets = new Insets(0, 60, 22, 60);
        painel.add(Tema.subtitulo("Acesso restrito a administradores"), c);

        c.insets = new Insets(4, 60, 4, 60);
        painel.add(Tema.rotulo("Usuário"), c);
        painel.add(campoUsuario, c);
        c.insets = new Insets(14, 60, 4, 60);
        painel.add(Tema.rotulo("Senha"), c);
        c.insets = new Insets(4, 60, 4, 60);
        painel.add(campoSenha, c);

        rotuloErro.setForeground(Tema.PERIGO);
        c.insets = new Insets(10, 60, 4, 60);
        painel.add(rotuloErro, c);

        c.insets = new Insets(10, 60, 4, 60);
        painel.add(botaoEntrar, c);
        return painel;
    }

    private void entrar() {
        String usuario = campoUsuario.getText();
        char[] senha = campoSenha.getPassword();

        rotuloErro.setText(" ");
        botaoEntrar.setEnabled(false);

        Async.executar(
                () -> {
                    try {
                        return autenticacao.login(usuario, senha);
                    } finally {
                        Arrays.fill(senha, '\0'); // não deixa a senha em memória
                    }
                },
                admin -> abrirPainelPrincipal(),
                erro -> {
                    botaoEntrar.setEnabled(true);
                    if (erro instanceof AutenticacaoException) {
                        rotuloErro.setText(erro.getMessage());
                        campoSenha.setText("");
                        campoSenha.requestFocusInWindow();
                    } else {
                        Mensagens.tratar(this, erro);
                    }
                });
    }

    private void abrirPainelPrincipal() {
        new TelaPrincipal(autenticacao, estoque).setVisible(true);
        dispose();
    }
}
