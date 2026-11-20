package br.com.loja.estoque;

import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;
import br.com.loja.estoque.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Visual nativo do sistema operacional (se não der, usa o padrão do Java)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // segue com o visual padrão
        }

        SwingUtilities.invokeLater(() -> {
            // Um único AuthService, compartilhado: é ele que guarda quem está logado
            AuthService auth = new AuthService();
            RoupaService service = new RoupaService(auth);

            new LoginFrame(auth, service).setVisible(true);
        });
    }
}