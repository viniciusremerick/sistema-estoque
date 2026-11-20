package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.ResumoEstoque;
import br.com.loja.estoque.service.RoupaService;
import br.com.loja.estoque.util.Formatador;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class InicioPanel extends JPanel {

    private final RoupaService service;

    private final JLabel valorPecas = criarValor();
    private final JLabel valorUnidades = criarValor();
    private final JLabel valorZeradas = criarValor();
    private final JLabel valorTotal = criarValor();

    public InicioPanel(RoupaService service) {
        this.service = service;
        montarTela();
        atualizar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Painel inicial");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JPanel cartoes = new JPanel(new GridLayout(1, 4, 12, 0));
        cartoes.add(criarCartao("Peças cadastradas", valorPecas));
        cartoes.add(criarCartao("Unidades em estoque", valorUnidades));
        cartoes.add(criarCartao("Peças zeradas", valorZeradas));
        cartoes.add(criarCartao("Valor total em estoque", valorTotal));

        JPanel topo = new JPanel(new BorderLayout(0, 16));
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(cartoes, BorderLayout.CENTER);

        JLabel nota = new JLabel("O valor total considera preço × quantidade de cada peça.");

        add(topo, BorderLayout.NORTH);
        add(nota, BorderLayout.SOUTH);
    }

    // Público: a janela chama ao abrir esta tela
    public void atualizar() {
        try {
            ResumoEstoque resumo = service.obterResumo();

            valorPecas.setText(String.valueOf(resumo.totalPecas()));
            valorUnidades.setText(String.valueOf(resumo.totalUnidades()));
            valorZeradas.setText(String.valueOf(resumo.pecasZeradas()));
            valorZeradas.setForeground(resumo.pecasZeradas() > 0
                    ? new Color(180, 0, 0)
                    : new Color(0, 120, 0));
            valorTotal.setText(Formatador.moeda(resumo.valorTotal()));
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro ao carregar o painel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JLabel criarValor() {
        JLabel rotulo = new JLabel("-", SwingConstants.CENTER);
        rotulo.setFont(rotulo.getFont().deriveFont(Font.BOLD, 24f));
        return rotulo;
    }

    private JPanel criarCartao(String titulo, JLabel valor) {
        JPanel cartao = new JPanel(new BorderLayout(0, 8));
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));
        cartao.add(new JLabel(titulo, SwingConstants.CENTER), BorderLayout.NORTH);
        cartao.add(valor, BorderLayout.CENTER);
        return cartao;
    }
}