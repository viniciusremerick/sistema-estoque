package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.JOptionPane;
import java.awt.Component;

public class AcoesEstoque {

    private AcoesEstoque() {
    }

    // Pergunta a nova quantidade e atualiza. Devolve true se a atualização ocorreu.
    public static boolean atualizarQuantidade(Component pai, RoupaService service, Roupa roupa) {
        String texto = JOptionPane.showInputDialog(pai,
                "Nova quantidade de \"" + roupa.getNome() + "\" (" + roupa.getTamanho()
                        + ", " + roupa.getCor() + ").\nQuantidade atual: " + roupa.getQuantidade(),
                String.valueOf(roupa.getQuantidade()));

        if (texto == null) { // clicou em Cancelar
            return false;
        }

        try {
            service.atualizarQuantidade(String.valueOf(roupa.getId()), texto);
            JOptionPane.showMessageDialog(pai, "Quantidade atualizada com sucesso.",
                    "Atualizar estoque", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(pai, e.getMessage(),
                    "Não foi possível atualizar", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}