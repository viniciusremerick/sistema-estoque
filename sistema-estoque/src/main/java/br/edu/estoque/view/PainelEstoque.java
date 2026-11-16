package br.edu.estoque.view;

import br.edu.estoque.model.FiltroRoupa;
import br.edu.estoque.model.Roupa;
import br.edu.estoque.model.Tamanho;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.util.Validador;
import br.edu.estoque.view.componentes.Async;
import br.edu.estoque.view.componentes.BotaoModerno;
import br.edu.estoque.view.componentes.DialogoQuantidade;
import br.edu.estoque.view.componentes.Mensagens;
import br.edu.estoque.view.componentes.TabelaRoupas;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Tela 4 — estoque com busca, filtros combináveis e atualização de quantidade. */
public class PainelEstoque extends JPanel implements Atualizavel {

    private static final String TODOS_TAMANHOS = "Todos";
    private static final String TODAS_CATEGORIAS = "Todas";

    private final EstoqueService estoque;

    private final JTextField campoBusca = Tema.campo(20);
    private final JComboBox<String> comboTamanho =
            new JComboBox<>(new String[]{TODOS_TAMANHOS, "P", "M", "G", "GG"});
    private final JComboBox<String> comboCategoria = new JComboBox<>(new String[]{TODAS_CATEGORIAS});
    private final TabelaRoupas tabela = new TabelaRoupas();
    private final JLabel rotuloTotal = Tema.subtitulo(" ");
    private final Timer atrasoBusca = new Timer(300, e -> atualizar());

    private boolean carregandoFiltros;
    private int ultimaRequisicao;

    private record DadosEstoque(List<String> categorias, List<Roupa> roupas) {
    }

    public PainelEstoque(EstoqueService estoque) {
        super(new BorderLayout(0, 16));
        this.estoque = estoque;
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        atrasoBusca.setRepeats(false);
        Tema.estilizarCombo(comboTamanho);
        Tema.estilizarCombo(comboCategoria);

        add(criarTopo(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
        registrarEventos();
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout(0, 14));
        topo.setOpaque(false);

        JPanel titulos = new JPanel(new BorderLayout(0, 4));
        titulos.setOpaque(false);
        titulos.add(Tema.titulo("Estoque"), BorderLayout.NORTH);
        titulos.add(Tema.subtitulo("Pesquise, filtre e atualize as quantidades. "
                + "Dê dois cliques em uma linha para atualizar."), BorderLayout.CENTER);
        topo.add(titulos, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(Tema.rotulo("Nome:"));
        filtros.add(campoBusca);
        filtros.add(Tema.rotulo("Tamanho:"));
        filtros.add(comboTamanho);
        filtros.add(Tema.rotulo("Categoria:"));
        filtros.add(comboCategoria);

        BotaoModerno limpar = BotaoModerno.secundario("Limpar filtros");
        limpar.addActionListener(e -> limparFiltros());
        filtros.add(limpar);
        topo.add(filtros, BorderLayout.CENTER);
        return topo;
    }

    private JScrollPane criarTabela() {
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        rolagem.getViewport().setBackground(java.awt.Color.WHITE);
        return rolagem;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(rotuloTotal, BorderLayout.WEST);

        BotaoModerno atualizar = new BotaoModerno("Atualizar quantidade", Tema.PRIMARIA);
        atualizar.addActionListener(e -> atualizarQuantidadeSelecionada());
        rodape.add(atualizar, BorderLayout.EAST);
        return rodape;
    }

    private void registrarEventos() {
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atrasoBusca.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { atrasoBusca.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { atrasoBusca.restart(); }
        });
        comboTamanho.addActionListener(e -> aoMudarFiltro());
        comboCategoria.addActionListener(e -> aoMudarFiltro());
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    atualizarQuantidadeSelecionada();
                }
            }
        });
    }

    private void aoMudarFiltro() {
        if (!carregandoFiltros) {
            atualizar();
        }
    }

    @Override
    public void atualizar() {
        FiltroRoupa filtro = montarFiltro();
        int requisicao = ++ultimaRequisicao;
        Async.executar(
                () -> new DadosEstoque(estoque.listarCategorias(), estoque.listar(filtro)),
                dados -> {
                    if (requisicao == ultimaRequisicao) { // ignora respostas antigas
                        exibir(dados);
                    }
                },
                erro -> {
                    if (requisicao == ultimaRequisicao) {
                        Mensagens.tratar(this, erro);
                    }
                });
    }

    private FiltroRoupa montarFiltro() {
        String selecionado = (String) comboTamanho.getSelectedItem();
        Tamanho tamanho = (selecionado == null || TODOS_TAMANHOS.equals(selecionado))
                ? null : Tamanho.valueOf(selecionado);

        String categoria = (String) comboCategoria.getSelectedItem();
        if (TODAS_CATEGORIAS.equals(categoria)) {
            categoria = null;
        }
        return new FiltroRoupa(Validador.nuloSeVazio(campoBusca.getText()), tamanho, categoria);
    }

    private void exibir(DadosEstoque dados) {
        boolean categoriaSumiu = recarregarCategorias(dados.categorias());
        tabela.setRoupas(dados.roupas());
        int total = tabela.getTotalLinhas();
        rotuloTotal.setText(total + (total == 1 ? " produto encontrado" : " produtos encontrados"));
        if (categoriaSumiu) {
            atualizar(); // a categoria filtrada deixou de existir; refaz a consulta sem ela
        }
    }

    /** @return true se a categoria antes selecionada não existe mais e foi trocada por "Todas". */
    private boolean recarregarCategorias(List<String> categorias) {
        String anterior = (String) comboCategoria.getSelectedItem();
        boolean sumiu = anterior != null && !TODAS_CATEGORIAS.equals(anterior)
                && !categorias.contains(anterior);

        carregandoFiltros = true;
        try {
            comboCategoria.removeAllItems();
            comboCategoria.addItem(TODAS_CATEGORIAS);
            categorias.forEach(comboCategoria::addItem);
            comboCategoria.setSelectedItem(sumiu || anterior == null ? TODAS_CATEGORIAS : anterior);
        } finally {
            carregandoFiltros = false;
        }
        return sumiu;
    }

    private void limparFiltros() {
        carregandoFiltros = true;
        try {
            campoBusca.setText("");
            comboTamanho.setSelectedIndex(0);
            comboCategoria.setSelectedItem(TODAS_CATEGORIAS);
        } finally {
            carregandoFiltros = false;
        }
        atrasoBusca.stop();
        atualizar();
    }

    private void atualizarQuantidadeSelecionada() {
        Optional<Roupa> selecionada = tabela.getRoupaSelecionada();
        if (selecionada.isEmpty()) {
            Mensagens.aviso(this, "Selecione um produto na tabela para atualizar a quantidade.");
            return;
        }
        DialogoQuantidade.atualizar(this, estoque, selecionada.get(), this::atualizar);
    }
}
