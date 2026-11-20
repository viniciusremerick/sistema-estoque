package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class ZeradasPanel extends JPanel {

    private final RoupaService service;

    private final RoupaTableModel modelo = new RoupaTableModel();
    private final JTable tabela = new JTable(modelo);
    private final JLabel rotuloAlerta = new JLabel(" ");
    private final JButton botaoRepor =
            new JButton("Atualizar quantidade da peça selecionada");

    public ZeradasPanel(RoupaService service) {
        this.service = service;
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        rotuloAlerta.setFont(rotuloAlerta.getFont().deriveFont(Font.BOLD, 15f));

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton botaoAtualizarLista = new JButton("Atualizar lista");

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(botaoAtualizarLista);
        rodape.add(botaoRepor);

        add(rotuloAlerta, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        botaoAtualizarLista.addActionListener(e -> carregar());
        botaoRepor.addActionListener(e -> atualizarQuantidadeSelecionada());
    }

    // Público: a janela chama este método ao abrir a aba
    public void carregar() {
        try {
            List<Roupa> lista = service.listarZeradas();
            modelo.setRoupas(lista);

            if (lista.isEmpty()) {
                rotuloAlerta.setForeground(new Color(0, 120, 0));
                rotuloAlerta.setText("Nenhuma peça zerada no momento.");
            } else {
                rotuloAlerta.setForeground(new Color(180, 0, 0));
                rotuloAlerta.setText("Atenção: " + lista.size() + " peça(s) sem estoque!");
            }
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro na consulta", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setModoAdministrador(boolean administrador) {
        botaoRepor.setEnabled(administrador);
    }

    private void atualizarQuantidadeSelecionada() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma peça na tabela para atualizar a quantidade.",
                    "Atualizar quantidade", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Roupa roupa = modelo.getRoupa(linha);

        // Se a quantidade deixou de ser zero, a peça sai desta lista ao recarregar
        if (AcoesEstoque.atualizarQuantidade(this, service, roupa)) {
            carregar();
        }
    }
}
