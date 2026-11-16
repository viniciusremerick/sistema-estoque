package br.edu.estoque.view.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Cartão do dashboard: título pequeno e um número grande. */
public class CartaoIndicador extends JPanel {

    private final JLabel valor = new JLabel("—");

    public CartaoIndicador(String titulo, Color cor) {
        super(new BorderLayout(0, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, cor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Tema.BORDA),
                        BorderFactory.createEmptyBorder(20, 22, 20, 22))));

        add(Tema.subtitulo(titulo), BorderLayout.NORTH);
        valor.setFont(Tema.FONTE_NUMERO);
        valor.setForeground(cor);
        add(valor, BorderLayout.CENTER);
    }

    public void setValor(String texto) {
        valor.setText(texto);
    }
}
