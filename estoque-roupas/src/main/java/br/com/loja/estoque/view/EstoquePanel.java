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

        // Rodapé: total à esquerda, ações do administrador à direita
        JButton botaoQuantidade = new JButton("Atualizar quantidade");
        JButton botaoExcluir = new JButton("Excluir peça selecionada");

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.add(botaoQuantidade);
        acoes.add(botaoExcluir);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(rotuloTotal, BorderLayout.WEST);
        rodape.add(acoes, BorderLayout.EAST);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        // Eventos: "quando isso acontecer, execute aquilo"
        botaoBuscar.addActionListener(e -> pesquisar());
        botaoLimpar.addActionListener(e -> limpar());
        botaoQuantidade.addActionListener(e -> atualizarQuantidadeSelecionada());
        botaoExcluir.addActionListener(e -> excluirSelecionada());
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

    // RF-05: atualização da quantidade da peça selecionada
    private void atualizarQuantidadeSelecionada() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma peça na tabela para atualizar a quantidade.",
                    "Atualizar quantidade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Roupa roupa = modelo.getRoupa(linha);

        if (AcoesEstoque.atualizarQuantidade(this, service, roupa)) {
            pesquisar();
        }
    }

    // RF-06: exclusão da peça selecionada, com confirmação
    private void excluirSelecionada() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma peça na tabela para excluir.",
                    "Excluir peça", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // A tabela não tem ordenação por clique, então a linha da tela
        // é a mesma linha do modelo
        Roupa roupa = modelo.getRoupa(linha);

        int resposta = JOptionPane.showConfirmDialog(this,
                "Excluir \"" + roupa.getNome() + "\" (" + roupa.getTamanho()
                        + ", " + roupa.getCor() + ")?\nEsta ação não pode ser desfeita.",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.excluir(roupa.getId());
            pesquisar(); // recarrega a lista sem a peça excluída
            JOptionPane.showMessageDialog(this, "Peça excluída com sucesso.",
                    "Excluir peça", JOptionPane.INFORMATION_MESSAGE);
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível excluir", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpar() {
        campoNome.setText("");
        campoCategoria.setText("");
        comboTamanho.setSelectedIndex(0);
        pesquisar();
    }
}