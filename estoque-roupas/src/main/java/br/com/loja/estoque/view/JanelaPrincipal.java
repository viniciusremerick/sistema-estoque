package br.com.loja.estoque.view;

import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class JanelaPrincipal extends JFrame {

    private static final String TELA_INICIO = "inicio";
    private static final String TELA_ESTOQUE = "estoque";
    private static final String TELA_ZERADAS = "zeradas";
    private static final String TELA_CADASTRO = "cadastro";

    private final AuthService auth;
    private final RoupaService service;

    private final CardLayout cartoes = new CardLayout();
    private final JPanel areaConteudo = new JPanel(cartoes);

    private final InicioPanel inicio;
    private final EstoquePanel estoque;
    private final ZeradasPanel zeradas;
    private final CadastroPanel cadastro;

    private final JLabel rotuloUsuario = new JLabel();
    private final JButton botaoSessao = new JButton();
    private final JButton botaoCadastro = new JButton("Cadastrar roupa");

    public JanelaPrincipal(AuthService auth, RoupaService service) {
        super("Estoque de Roupas");
        this.auth = auth;
        this.service = service;

        inicio = new InicioPanel(service);
        estoque = new EstoquePanel(service);
        zeradas = new ZeradasPanel(service);
        cadastro = new CadastroPanel(service);

        montarTela();
        aplicarPerfil();
        mostrar(TELA_INICIO);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        setLayout(new BorderLayout());

        // ----- Cabeçalho -----
        JLabel titulo = new JLabel("Sistema de Estoque de Roupas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JPanel sessao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sessao.add(rotuloUsuario);
        sessao.add(botaoSessao);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(sessao, BorderLayout.EAST);

        // ----- Menu lateral -----
        JButton botaoInicio = new JButton("Início");
        JButton botaoEstoque = new JButton("Estoque");
        JButton botaoZeradas = new JButton("Peças zeradas");

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.add(botaoInicio);
        menu.add(botaoEstoque);
        menu.add(botaoZeradas);
        menu.add(botaoCadastro);

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lateral.add(menu, BorderLayout.NORTH);

        // ----- Área de conteúdo (uma tela por vez) -----
        areaConteudo.add(inicio, TELA_INICIO);
        areaConteudo.add(estoque, TELA_ESTOQUE);
        areaConteudo.add(zeradas, TELA_ZERADAS);
        areaConteudo.add(cadastro, TELA_CADASTRO);

        add(cabecalho, BorderLayout.NORTH);
        add(lateral, BorderLayout.WEST);
        add(areaConteudo, BorderLayout.CENTER);

        // ----- Eventos -----
        botaoInicio.addActionListener(e -> mostrar(TELA_INICIO));
        botaoEstoque.addActionListener(e -> mostrar(TELA_ESTOQUE));
        botaoZeradas.addActionListener(e -> mostrar(TELA_ZERADAS));
        botaoCadastro.addActionListener(e -> mostrar(TELA_CADASTRO));
        botaoSessao.addActionListener(e -> trocarSessao());
    }

    // Ajusta a tela conforme quem está logado
    private void aplicarPerfil() {
        boolean administrador = auth.estaAutenticado();

        if (administrador) {
            rotuloUsuario.setText("Administrador: "
                    + auth.getAdministradorLogado().getUsuario());
            botaoSessao.setText("Sair");
        } else {
            rotuloUsuario.setText("Visitante (somente consulta)");
            botaoSessao.setText("Fazer login");
        }

        botaoCadastro.setEnabled(administrador);
        botaoCadastro.setToolTipText(administrador
                ? null
                : "Disponível apenas para administradores");

        estoque.setModoAdministrador(administrador);
        zeradas.setModoAdministrador(administrador);
    }

    // Mostra uma tela, atualizando antes os dados que ela exibe
    private void mostrar(String tela) {
        switch (tela) {
            case TELA_INICIO -> inicio.atualizar();
            case TELA_ESTOQUE -> estoque.pesquisar();
            case TELA_ZERADAS -> zeradas.carregar();
            default -> { }
        }
        cartoes.show(areaConteudo, tela);
    }

    // "Sair" (se logado) ou "Fazer login" (se visitante): volta para o login
    private void trocarSessao() {
        auth.logout();
        dispose();
        new LoginFrame(auth, service).setVisible(true);
    }
}