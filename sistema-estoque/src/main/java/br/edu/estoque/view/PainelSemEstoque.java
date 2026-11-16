package br.edu.estoque.view;

import br.edu.estoque.model.Roupa;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.view.componentes.Async;
import br.edu.estoque.view.componentes.BotaoModerno;
import br.edu.estoque.view.componentes.DialogoQuantidade;
import br.edu.estoque.view.componentes.Mensagens;
import br.edu.estoque.view.componentes.TabelaRoupas;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Tela 5 — somente produtos com quantidade igual a zero. */
public class PainelSemEstoque extends JPanel implements Atualizavel {

    private final EstoqueService estoque;
    private final TabelaRoupas tabela = new TabelaRoupas();
    private final JLabel alerta = new JLabel(" ");

    public PainelSemEstoque(EstoqueService estoque) {
        super(new BorderLayout(0, 16));
        this.estoque = estoque;
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel topo = new JPanel(new BorderLayout(0, 12));
        topo.setOpaque(false);
        topo.add(Tema.titulo("Produtos sem estoque"), BorderLayout.NORTH);

        alerta.setOpaque(true);
        alerta.setFont(Tema.FONTE_NEGRITO);
        alerta.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        topo.add(alerta, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        rolagem.getViewport().setBackground(Color.WHITE);
        add(rolagem, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        BotaoModerno atualizarLista = BotaoModerno.secundario("Atualizar lista");
        atualizarLista.addActionListener(e -> atualizar());
        BotaoModerno repor = new BotaoModerno("Repor estoque", Tema.PRIMARIA);
        repor.addActionListener(e -> reporSelecionada());
        botoes.add(atualizarLista);
        botoes.add(repor);
        add(botoes, BorderLayout.SOUTH);
    }

    @Override
    public void atualizar() {
        Async.executar(estoque::listarSemEstoque, this::exibir, erro -> Mensagens.tratar(this, erro));
    }

    private void exibir(List<Roupa> roupas) {
        tabela.setRoupas(roupas);
        if (roupas.isEmpty()) {
            alerta.setText("Nenhum produto zerado. Todo o estoque está abastecido.");
            alerta.setBackground(new Color(0xD1FAE5));
            alerta.setForeground(new Color(0x065F46));
        } else {
            int total = roupas.size();
            alerta.setText("Atenção: " + total + (total == 1 ? " peça está" : " peças estão") + " sem estoque.");
            alerta.setBackground(new Color(0xFEE2E2));
            alerta.setForeground(new Color(0x991B1B));
        }
    }

    private void reporSelecionada() {
        Optional<Roupa> selecionada = tabela.getRoupaSelecionada();
        if (selecionada.isEmpty()) {
            Mensagens.aviso(this, "Selecione um produto na tabela para repor o estoque.");
            return;
        }
        DialogoQuantidade.atualizar(this, estoque, selecionada.get(), this::atualizar);
    }
}
