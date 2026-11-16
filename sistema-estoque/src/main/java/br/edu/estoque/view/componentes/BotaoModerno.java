package br.edu.estoque.view.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Botão arredondado com estados de hover/pressionado, desenhado manualmente. */
public class BotaoModerno extends JButton {

    private final Color corBase;
    private final boolean contorno;

    public BotaoModerno(String texto, Color corBase) {
        this(texto, corBase, false);
    }

    private BotaoModerno(String texto, Color corBase, boolean contorno) {
        super(texto);
        this.corBase = corBase;
        this.contorno = contorno;
        setForeground(contorno ? Tema.TEXTO : Color.WHITE);
        setFont(Tema.FONTE_NEGRITO);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    /** Botão branco com contorno, para ações secundárias (ex.: "Limpar"). */
    public static BotaoModerno secundario(String texto) {
        return new BotaoModerno(texto, Color.WHITE, true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fundo = corBase;
        if (!isEnabled()) {
            fundo = contorno ? new Color(0xF1F5F9) : new Color(0x94A3B8);
        } else if (getModel().isPressed()) {
            fundo = escurecer(corBase, 40);
        } else if (getModel().isRollover()) {
            fundo = escurecer(corBase, contorno ? 12 : 20);
        }
        g2.setColor(fundo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        if (contorno) {
            g2.setColor(Tema.BORDA.darker());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private static Color escurecer(Color cor, int quantidade) {
        return new Color(Math.max(0, cor.getRed() - quantidade),
                Math.max(0, cor.getGreen() - quantidade),
                Math.max(0, cor.getBlue() - quantidade));
    }
}
