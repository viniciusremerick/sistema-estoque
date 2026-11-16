package br.edu.estoque.view.componentes;

import br.edu.estoque.exception.AutenticacaoException;
import br.edu.estoque.exception.DaoException;
import br.edu.estoque.exception.ValidacaoException;
import java.awt.Component;
import javax.swing.JOptionPane;

/** Diálogos padronizados de sucesso, aviso e erro. */
public final class Mensagens {

    private Mensagens() {
    }

    public static void sucesso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /** Escolhe o tipo de diálogo conforme a natureza da exceção. */
    public static void tratar(Component pai, Exception excecao) {
        if (excecao instanceof ValidacaoException) {
            aviso(pai, excecao.getMessage());
        } else if (excecao instanceof AutenticacaoException) {
            erro(pai, excecao.getMessage());
        } else if (excecao instanceof DaoException) {
            Throwable causa = excecao.getCause();
            String detalhe = (causa != null && causa.getMessage() != null)
                    ? "\n\nDetalhe técnico: " + causa.getMessage().lines().findFirst().orElse("")
                    : "";
            erro(pai, excecao.getMessage() + detalhe);
        } else {
            erro(pai, "Erro inesperado: " + excecao.getMessage());
        }
    }
}
