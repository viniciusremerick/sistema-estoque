package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CadastroPanel extends JPanel {

    private final RoupaService service;

    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoCategoria = new JTextField(25);
    private final JComboBox<String> comboTamanho =
            new JComboBox<>(new String[]{"P", "M", "G", "GG"});
    private final JTextField campoCor = new JTextField(25);
    private final JTextField campoPreco = new JTextField(10);
    private final JTextField campoQuantidade = new JTextField(10);

    public CadastroPanel(RoupaService service) {
        this.service = service;
        montarTela();
    }

    private void montarTela() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formulario = new JPanel(new GridBagLayout());
        adicionarLinha(formulario, 0, "Nome:", campoNome);
        adicionarLinha(formulario, 1, "Categoria:", campoCategoria);
        adicionarLinha(formulario, 2, "Tamanho:", comboTamanho);
        adicionarLinha(formulario, 3, "Cor:", campoCor);
        adicionarLinha(formulario, 4, "Preço (R$):", campoPreco);
        adicionarLinha(formulario, 5, "Quantidade:", campoQuantidade);

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoLimpar = new JButton("Limpar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botoes.add(botaoSalvar);
        botoes.add(javax.swing.Box.createHorizontalStrut(8));
        botoes.add(botaoLimpar);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 6;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(12, 4, 4, 4);
        formulario.add(botoes, c);

        add(formulario, BorderLayout.NORTH);

        botaoSalvar.addActionListener(e -> salvar());
        botaoLimpar.addActionListener(e -> limpar());
    }

    // Monta uma linha do formulário: rótulo à esquerda, campo à direita
    private void adicionarLinha(JPanel painel, int linha, String rotulo, JComponent campo) {
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

    private void salvar() {
        try {
            service.cadastrar(
                    campoNome.getText(),
                    campoCategoria.getText(),
                    (String) comboTamanho.getSelectedItem(),
                    campoCor.getText(),
                    campoPreco.getText(),
                    campoQuantidade.getText());

            JOptionPane.showMessageDialog(this, "Roupa cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limpar();

        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível cadastrar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpar() {
        campoNome.setText("");
        campoCategoria.setText("");
        comboTamanho.setSelectedIndex(0);
        campoCor.setText("");
        campoPreco.setText("");
        campoQuantidade.setText("");
        campoNome.requestFocusInWindow();
    }
}