package br.com.loja.estoque.view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class FormularioUtil {

    private FormularioUtil() {
    }

    // Monta uma linha do formulário: rótulo à esquerda, campo à direita
    public static void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridy = linha;

        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        painel.add(new JLabel(rotulo), c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        painel.add(campo, c);
    }
}