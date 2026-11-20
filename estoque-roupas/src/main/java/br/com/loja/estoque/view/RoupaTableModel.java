package br.com.loja.estoque.view;

import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.util.Formatador;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RoupaTableModel extends AbstractTableModel {

    private static final String[] COLUNAS =
            {"Código", "Nome", "Categoria", "Tamanho", "Cor", "Preço", "Quantidade"};

    private List<Roupa> roupas = new ArrayList<>();

    public void setRoupas(List<Roupa> roupas) {
        this.roupas = roupas;
        fireTableDataChanged(); // avisa a tabela que os dados mudaram
    }

    public Roupa getRoupa(int linha) {
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
    public Object getValueAt(int linha, int coluna) {
        Roupa r = roupas.get(linha);
        return switch (coluna) {
            case 0 -> r.getId();
            case 1 -> r.getNome();
            case 2 -> r.getCategoria();
            case 3 -> r.getTamanho();
            case 4 -> r.getCor();
            case 5 -> Formatador.moeda(r.getPreco());
            case 6 -> r.getQuantidade();
            default -> "";
        };
    }
}