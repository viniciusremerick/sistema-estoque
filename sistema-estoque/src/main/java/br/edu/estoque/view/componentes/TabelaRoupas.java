package br.edu.estoque.view.componentes;

import br.edu.estoque.model.Roupa;
import br.edu.estoque.util.Formatador;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Tabela somente leitura de roupas, reutilizada nas telas de estoque e de produtos zerados. */
public class TabelaRoupas extends JTable {

    private static final int COL_ID = 0;
    private static final int COL_PRECO = 5;
    private static final int COL_QUANTIDADE = 6;

    private final ModeloRoupas modelo;

    public TabelaRoupas() {
        this(new ModeloRoupas());
    }

    private TabelaRoupas(ModeloRoupas modelo) {
        super(modelo);
        this.modelo = modelo;
        configurarAparencia();
    }

    public void setRoupas(List<Roupa> roupas) {
        modelo.setRoupas(roupas);
    }

    public Optional<Roupa> getRoupaSelecionada() {
        int linhaVisual = getSelectedRow();
        if (linhaVisual < 0) {
            return Optional.empty();
        }
        return Optional.of(modelo.getRoupa(convertRowIndexToModel(linhaVisual)));
    }

    public int getTotalLinhas() {
        return modelo.getRowCount();
    }

    private void configurarAparencia() {
        setRowHeight(34);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(Tema.BORDA);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(new Color(0xE0E7FF));
        setSelectionForeground(Tema.TEXTO);
        setDefaultRenderer(Object.class, new Renderizador());
        setDefaultRenderer(Long.class, new Renderizador());
        setDefaultRenderer(Integer.class, new Renderizador());
        setDefaultRenderer(BigDecimal.class, new Renderizador());

        JTableHeader cabecalho = getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setBackground(Tema.SIDEBAR);
        cabecalho.setForeground(Color.WHITE);
        cabecalho.setFont(Tema.FONTE_NEGRITO);

        int[] larguras = {60, 240, 120, 80, 130, 110, 130};
        for (int i = 0; i < larguras.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    // ---------- modelo ----------

    private static final class ModeloRoupas extends AbstractTableModel {

        private static final String[] COLUNAS =
                {"ID", "Nome", "Categoria", "Tamanho", "Cor", "Preço", "Qtd. em estoque"};

        private List<Roupa> roupas = new ArrayList<>();

        void setRoupas(List<Roupa> novas) {
            this.roupas = new ArrayList<>(novas);
            fireTableDataChanged();
        }

        Roupa getRoupa(int linha) {
            return roupas.get(linha);
        }

        @Override
        public int getRowCount() {
            return roupas.size();
        }

        @Override
        public int getColumnCount() {
            return COLUNAS.length;
        }

        @Override
        public String getColumnName(int coluna) {
            return COLUNAS[coluna];
        }

        @Override
        public Class<?> getColumnClass(int coluna) {
            return switch (coluna) {
                case COL_ID -> Long.class;
                case COL_PRECO -> BigDecimal.class;
                case COL_QUANTIDADE -> Integer.class;
                default -> String.class;
            };
        }

        @Override
        public Object getValueAt(int linha, int coluna) {
            Roupa r = roupas.get(linha);
            return switch (coluna) {
                case 0 -> r.getId();
                case 1 -> r.getNome();
                case 2 -> r.getCategoria();
                case 3 -> r.getTamanho().name();
                case 4 -> r.getCor();
                case 5 -> r.getPreco();
                case 6 -> r.getQuantidade();
                default -> "";
            };
        }
    }

    // ---------- renderização ----------

    private static final class Renderizador extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object valor) {
            if (valor instanceof BigDecimal preco) {
                setText(Formatador.moeda(preco));
            } else {
                super.setValue(valor);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, false, linha, coluna);
            int colunaModelo = tabela.convertColumnIndexToModel(coluna);
            boolean numerica = colunaModelo == COL_ID || colunaModelo == COL_PRECO
                    || colunaModelo == COL_QUANTIDADE;

            setHorizontalAlignment(numerica ? SwingConstants.RIGHT : SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setFont(Tema.FONTE_BASE);
            setForeground(Tema.TEXTO);
            if (!selecionada) {
                setBackground(linha % 2 == 0 ? Color.WHITE : Tema.LINHA_ALTERNADA);
            }
            if (colunaModelo == COL_QUANTIDADE && valor instanceof Integer q && q == 0) {
                setForeground(Tema.PERIGO);
                setFont(Tema.FONTE_NEGRITO);
            }
            return this;
        }
    }
}
