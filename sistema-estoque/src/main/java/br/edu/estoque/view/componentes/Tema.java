package br.edu.estoque.view.componentes;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;

/** Paleta, fontes e fábricas de componentes para manter a aparência consistente. */
public final class Tema {

    public static final Color FUNDO = new Color(0xF4F6FA);
    public static final Color SIDEBAR = new Color(0x1E293B);
    public static final Color SIDEBAR_HOVER = new Color(0x334155);
    public static final Color PRIMARIA = new Color(0x4F46E5);
    public static final Color PERIGO = new Color(0xDC2626);
    public static final Color SUCESSO = new Color(0x059669);
    public static final Color AVISO = new Color(0xD97706);
    public static final Color TEXTO = new Color(0x0F172A);
    public static final Color TEXTO_SUAVE = new Color(0x64748B);
    public static final Color BORDA = new Color(0xE2E8F0);
    public static final Color LINHA_ALTERNADA = new Color(0xF8FAFC);

    public static final Font FONTE_BASE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = FONTE_BASE.deriveFont(Font.BOLD);
    public static final Font FONTE_TITULO = FONTE_BASE.deriveFont(Font.BOLD, 24f);
    public static final Font FONTE_NUMERO = FONTE_BASE.deriveFont(Font.BOLD, 36f);

    private Tema() {
    }

    /** Define o Look and Feel e a fonte padrão. Deve ser chamado antes de criar qualquer janela. */
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // segue com o Look and Feel padrão
        }
        for (Object chave : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(chave) instanceof FontUIResource) {
                UIManager.put(chave, new FontUIResource(FONTE_BASE));
            }
        }
    }

    public static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_TITULO);
        label.setForeground(TEXTO);
        return label;
    }

    public static JLabel subtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO_SUAVE);
        return label;
    }

    public static JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_NEGRITO);
        label.setForeground(TEXTO);
        return label;
    }

    public static Border bordaCampo() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(7, 10, 7, 10));
    }

    public static JTextField campo(int colunas) {
        JTextField campo = new JTextField(colunas);
        campo.setBorder(bordaCampo());
        return campo;
    }

    public static <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(FONTE_BASE);
    }

    /** Painel branco com borda fina, usado como "cartão". */
    public static Border bordaCartao() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(20, 24, 20, 24));
    }
}
