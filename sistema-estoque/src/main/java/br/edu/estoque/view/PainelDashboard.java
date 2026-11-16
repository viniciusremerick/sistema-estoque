package br.edu.estoque.view;

import br.edu.estoque.model.ResumoEstoque;
import br.edu.estoque.service.AutenticacaoService;
import br.edu.estoque.service.EstoqueService;
import br.edu.estoque.util.Formatador;
import br.edu.estoque.view.componentes.Async;
import br.edu.estoque.view.componentes.CartaoIndicador;
import br.edu.estoque.view.componentes.Mensagens;
import br.edu.estoque.view.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Dashboard com os três indicadores do estoque. */
public class PainelDashboard extends JPanel implements Atualizavel {

    private final EstoqueService estoque;
    private final CartaoIndicador cartaoProdutos = new CartaoIndicador("Produtos cadastrados", Tema.PRIMARIA);
    private final CartaoIndicador cartaoPecas = new CartaoIndicador("Peças em estoque", Tema.SUCESSO);
    private final CartaoIndicador cartaoZerados = new CartaoIndicador("Produtos zerados", Tema.PERIGO);

    public PainelDashboard(EstoqueService estoque, AutenticacaoService autenticacao) {
        super(new BorderLayout(0, 24));
        this.estoque = estoque;
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 4));
        cabecalho.setOpaque(false);
        cabecalho.add(Tema.titulo("Dashboard"));
        JLabel boasVindas = Tema.subtitulo("Olá, " + autenticacao.getUsuarioLogado()
                + ". Veja o panorama atual do estoque.");
        cabecalho.add(boasVindas);
        add(cabecalho, BorderLayout.NORTH);

        JPanel cartoes = new JPanel(new GridLayout(1, 3, 20, 0));
        cartoes.setOpaque(false);
        cartoes.add(cartaoProdutos);
        cartoes.add(cartaoPecas);
        cartoes.add(cartaoZerados);

        JPanel faixa = new JPanel(new BorderLayout());
        faixa.setOpaque(false);
        faixa.add(cartoes, BorderLayout.NORTH);
        add(faixa, BorderLayout.CENTER);
    }

    @Override
    public void atualizar() {
        Async.executar(estoque::obterResumo, this::exibir, erro -> Mensagens.tratar(this, erro));
    }

    private void exibir(ResumoEstoque resumo) {
        cartaoProdutos.setValor(Formatador.inteiro(resumo.totalProdutos()));
        cartaoPecas.setValor(Formatador.inteiro(resumo.totalPecas()));
        cartaoZerados.setValor(Formatador.inteiro(resumo.produtosZerados()));
    }
}
