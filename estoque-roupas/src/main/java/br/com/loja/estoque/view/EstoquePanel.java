package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class EstoquePanel extends JPanel {

    private final RoupaService service;

    private final RoupaTableModel modelo = new RoupaTableModel();
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoNome = new JTextField(15);
    private final JComboBox<String> comboTamanho =
            new JComboBox<>(new String[]{"Todos", "P", "M", "G", "GG"});
    private final JTextField campoCategoria = new JTextField(12);
    private final JLabel rotuloTotal = new JLabel(" ");

    public EstoquePanel(RoupaService service) {
        this.service = service;
        montarTela();
        pesquisar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.add(new JLabel("Nome:"));
        filtros.add(campoNome);
        filtros.add(new JLabel("Tamanho:"));
        filtros.add(comboTamanho);
        filtros.add(new JLabel("Categoria:"));
        filtros.add(campoCategoria);

        JButton botaoBuscar = new JButton("Buscar");
        JButton botaoLimpar = new JButton("Limpar");
        filtros.add(botaoBuscar);
        filtros.add(botaoLimpar);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rotuloTotal, BorderLayout.SOUTH);

        // Eventos: "quando isso acontecer, execute aquilo"
        botaoBuscar.addActionListener(e -> pesquisar());
        botaoLimpar.addActionListener(e -> limpar());
        campoNome.addActionListener(e -> pesquisar());       // Enter no campo
        campoCategoria.addActionListener(e -> pesquisar());  // Enter no campo
        comboTamanho.addActionListener(e -> pesquisar());    // trocou o tamanho
    }

    // Público para que outras telas possam pedir "atualize a lista"
    public void pesquisar() {
        String tamanho = "Todos".equals(comboTamanho.getSelectedItem())
                ? ""
                : (String) comboTamanho.getSelectedItem();

        try {
            List<Roupa> lista = service.pesquisar(
                    campoNome.getText(), tamanho, campoCategoria.getText());
            modelo.setRoupas(lista);
            rotuloTotal.setText(lista.size() + " peça(s) encontrada(s)");
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro na consulta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        campoNome.setText("");
        campoCategoria.setText("");
        comboTamanho.setSelectedIndex(0);
        pesquisar();
    }
}