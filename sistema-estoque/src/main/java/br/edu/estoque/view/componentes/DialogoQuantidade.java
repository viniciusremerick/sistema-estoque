package br.edu.estoque.view.componentes;

import br.edu.estoque.model.Roupa;
import br.edu.estoque.service.EstoqueService;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.OptionalInt;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/** Fluxo completo de "atualizar quantidade": pergunta o valor, grava no banco e avisa o resultado. */
public final class DialogoQuantidade {

    private DialogoQuantidade() {
    }

    public static void atualizar(Component pai, EstoqueService servico, Roupa roupa, Runnable aoConcluir) {
        solicitar(pai, roupa).ifPresent(novaQuantidade ->
                Async.executar(
                        () -> {
                            servico.atualizarQuantidade(roupa.getId(), novaQuantidade);
                            return null;
                        },
                        resultado -> {
                            Mensagens.sucesso(pai, "Estoque de " + roupa.getDescricaoResumida()
                                    + " atualizado para " + novaQuantidade + ".");
                            aoConcluir.run();
                        },
                        erro -> Mensagens.tratar(pai, erro)));
    }

    private static OptionalInt solicitar(Component pai, Roupa roupa) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(roupa.getQuantidade(), 0, Integer.MAX_VALUE, 1));

        JPanel painel = new JPanel(new GridLayout(0, 1, 0, 6));
        painel.add(Tema.rotulo(roupa.getDescricaoResumida()));
        painel.add(new JLabel("Quantidade atual: " + roupa.getQuantidade()));
        painel.add(new JLabel("Nova quantidade:"));
        painel.add(spinner);

        int opcao = JOptionPane.showConfirmDialog(pai, painel, "Atualizar quantidade",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) {
            return OptionalInt.empty();
        }
        try {
            spinner.commitEdit();
        } catch (ParseException e) {
            Mensagens.aviso(pai, "Quantidade inválida. Informe um número inteiro maior ou igual a zero.");
            return OptionalInt.empty();
        }
        return OptionalInt.of(((Number) spinner.getValue()).intValue());
    }
}
