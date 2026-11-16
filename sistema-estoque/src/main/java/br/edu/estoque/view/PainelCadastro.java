package br.edu.estoque.view;

import br.edu.estoque.model.Roupa;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.util.Formatador;
import br.edu.estoque.view.componentes.Async;
import br.edu.estoque.view.componentes.BotaoModerno;
import br.edu.estoque.view.componentes.Mensagens;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Tela 3 — formulário de cadastro de roupas. */
public class PainelCadastro extends JPanel implements Atualizavel {

    private static final List<String> CATEGORIAS_SUGERIDAS =
            List.of("Camiseta", "Calça", "Vestido", "Jaqueta", "Short", "Saia", "Blusa");

    private final EstoqueService estoque;

    private final JTextField campoNome = Tema.campo(30);
    private final JComboBox<String> comboCategoria = new JComboBox<>();
    private final JComboBox<String> comboTamanho = new JComboBox<>(new String[]{"P", "M", "G", "GG"});
    private final JTextField campoCor = Tema.campo(15);
    private final JTextField campoPreco = Tema.campo(10);
    private final JTextField campoQuantidade = Tema.campo(10);
    private final BotaoModerno botaoCadastrar = new BotaoModerno("Cadastrar", Tema.PRIMARIA);
    private final BotaoModerno botaoLimpar = BotaoModerno.secundario("Limpar");

    public PainelCadastro(EstoqueService estoque) {
        super(new BorderLayout(0, 20));
        this.estoque = estoque;
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel cabecalho = new JPanel(new BorderLayout(0, 4));
        cabecalho.setOpaque(false);
        cabecalho.add(Tema.titulo("Cadastrar roupa"), BorderLayout.NORTH);
        cabecalho.add(Tema.subtitulo("Preencha todos os campos. Preço no formato 49,90."), BorderLayout.CENTER);
        add(cabecalho, BorderLayout.NORTH);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(criarFormulario(), BorderLayout.NORTH);
        add(envoltorio, BorderLayout.CENTER);

        Tema.estilizarCombo(comboCategoria);
        Tema.estilizarCombo(comboTamanho);
        comboCategoria.setEditable(true);
        preencherCategorias(List.of());

        botaoCadastrar.addActionListener(e -> cadastrar());
        botaoLimpar.addActionListener(e -> limpar());
    }

    private JPanel criarFormulario() {
        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(Tema.bordaCartao());

        adicionar(cartao, "Nome da peça", campoNome, 0, 0, 2);
        adicionar(cartao, "Categoria", comboCategoria, 0, 2, 1);
        adicionar(cartao, "Tamanho", comboTamanho, 1, 2, 1);
        adicionar(cartao, "Cor", campoCor, 0, 4, 1);
        adicionar(cartao, "Preço (R$)", campoPreco, 1, 4, 1);
        adicionar(cartao, "Quantidade em estoque", campoQuantidade, 0, 6, 1);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botoes.setOpaque(false);
        botoes.add(botaoCadastrar);
        botoes.add(javax.swing.Box.createHorizontalStrut(10));
        botoes.add(botaoLimpar);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(22, 6, 0, 6);
        cartao.add(botoes, c);
        return cartao;
    }

    /** Coloca rótulo (linha y) e componente (linha y+1) na coluna x. */
    private void adicionar(JPanel painel, String rotulo, JComponent componente, int x, int y, int largura) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridwidth = largura;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(y == 0 ? 0 : 14, 6, 4, 6);
        c.gridy = y;
        painel.add(Tema.rotulo(rotulo), c);

        c.gridy = y + 1;
        c.insets = new Insets(0, 6, 0, 6);
        painel.add(componente, c);
    }

    @Override
    public void atualizar() {
        Async.executar(estoque::listarCategorias, this::preencherCategorias,
                erro -> Mensagens.tratar(this, erro));
    }

    /** Une as categorias sugeridas com as já existentes no banco, sem perder o texto digitado. */
    private void preencherCategorias(List<String> doBanco) {
        String digitado = Objects.toString(comboCategoria.getEditor().getItem(), "");
        TreeSet<String> todas = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        todas.addAll(CATEGORIAS_SUGERIDAS);
        todas.addAll(doBanco);

        comboCategoria.removeAllItems();
        todas.forEach(comboCategoria::addItem);
        comboCategoria.setSelectedItem(digitado.isBlank() ? null : digitado);
    }

    private void cadastrar() {
        String categoria = Objects.toString(comboCategoria.getEditor().getItem(), "");
        String tamanho = (String) comboTamanho.getSelectedItem();
        String nome = campoNome.getText();
        String cor = campoCor.getText();
        String preco = campoPreco.getText();
        String quantidade = campoQuantidade.getText();

        botaoCadastrar.setEnabled(false);
        Async.executar(
                () -> estoque.cadastrarRoupa(nome, categoria, tamanho, cor, preco, quantidade),
                this::aoCadastrar,
                erro -> {
                    botaoCadastrar.setEnabled(true);
                    Mensagens.tratar(this, erro);
                });
    }

    private void aoCadastrar(Roupa roupa) {
        botaoCadastrar.setEnabled(true);
        Mensagens.sucesso(this, "Peça cadastrada com sucesso!\n\n"
                + "ID: " + roupa.getId() + "\n"
                + roupa.getDescricaoResumida() + "\n"
                + "Preço: " + Formatador.moeda(roupa.getPreco())
                + " | Quantidade: " + roupa.getQuantidade());
        limpar();
        atualizar(); // a nova categoria já aparece na lista
    }

    private void limpar() {
        campoNome.setText("");
        comboCategoria.setSelectedItem(null);
        comboCategoria.getEditor().setItem("");
        comboTamanho.setSelectedIndex(0);
        campoCor.setText("");
        campoPreco.setText("");
        campoQuantidade.setText("");
        campoNome.requestFocusInWindow();
    }
}
