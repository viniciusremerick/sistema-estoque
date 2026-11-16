package br.edu.estoque.view;

import br.edu.estoque.service.AutenticacaoService;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/** Tela 2 — painel principal com menu lateral que alterna entre as demais telas. */
public class TelaPrincipal extends JFrame {

    private static final String DASHBOARD = "dashboard";
    private static final String CADASTRO = "cadastro";
    private static final String ESTOQUE = "estoque";
    private static final String ZERADOS = "zerados";

    private final AutenticacaoService autenticacao;
    private final EstoqueService estoque;

    private final CardLayout cartoes = new CardLayout();
    private final JPanel areaConteudo = new JPanel(cartoes);
    private final Map<String, Atualizavel> paineis = new LinkedHashMap<>();
    private final Map<String, ItemMenu> itensMenu = new LinkedHashMap<>();

    public TelaPrincipal(AutenticacaoService autenticacao, EstoqueService estoque) {
        super("Sistema de Estoque — Loja de Roupas");
        this.autenticacao = autenticacao;
        this.estoque = estoque;
        montarTela();
        mostrar(DASHBOARD);
    }

    private void montarTela() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);

        registrarPainel(DASHBOARD, new PainelDashboard(estoque, autenticacao));
        registrarPainel(CADASTRO, new PainelCadastro(estoque));
        registrarPainel(ESTOQUE, new PainelEstoque(estoque));
        registrarPainel(ZERADOS, new PainelSemEstoque(estoque));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarMenuLateral(), BorderLayout.WEST);
        raiz.add(areaConteudo, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private void registrarPainel(String nome, Atualizavel painel) {
        paineis.put(nome, painel);
        areaConteudo.add((JPanel) painel, nome);
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.SIDEBAR);
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(28, 14, 20, 14));

        JLabel marca = new JLabel("Estoque Moda");
        marca.setFont(Tema.FONTE_TITULO.deriveFont(20f));
        marca.setForeground(Color.WHITE);
        marca.setBorder(BorderFactory.createEmptyBorder(0, 10, 28, 0));
        menu.add(marca);

        adicionarItem(menu, DASHBOARD, "Dashboard");
        adicionarItem(menu, CADASTRO, "Cadastrar roupa");
        adicionarItem(menu, ESTOQUE, "Estoque");
        adicionarItem(menu, ZERADOS, "Produtos sem estoque");

        menu.add(javax.swing.Box.createVerticalGlue());

        JLabel usuario = new JLabel("Administrador: " + autenticacao.getUsuarioLogado());
        usuario.setForeground(new Color(0x94A3B8));
        usuario.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        menu.add(usuario);

        ItemMenu sair = new ItemMenu("Sair");
        sair.addActionListener(e -> sair());
        menu.add(sair);
        return menu;
    }

    private void adicionarItem(JPanel menu, String nome, String texto) {
        ItemMenu item = new ItemMenu(texto);
        item.addActionListener(e -> mostrar(nome));
        itensMenu.put(nome, item);
        menu.add(item);
        menu.add(javax.swing.Box.createVerticalStrut(4));
    }

    private void mostrar(String nome) {
        itensMenu.forEach((chave, item) -> item.setSelecionado(chave.equals(nome)));
        cartoes.show(areaConteudo, nome);
        paineis.get(nome).atualizar(); // sempre mostra dados atuais do banco
    }

    private void sair() {
        autenticacao.logout();
        new TelaLogin(autenticacao, estoque).setVisible(true);
        dispose();
    }

    /** Botão do menu lateral com destaque para o item ativo. */
    private static final class ItemMenu extends JButton {

        private boolean selecionado;

        ItemMenu(String texto) {
            super(texto);
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selecionado) {
                g2.setColor(Tema.PRIMARIA);
            } else if (getModel().isRollover()) {
                g2.setColor(Tema.SIDEBAR_HOVER);
            } else {
                g2.setColor(Tema.SIDEBAR);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
