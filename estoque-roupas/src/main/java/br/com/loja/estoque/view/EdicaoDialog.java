package br.com.loja.estoque.view;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.service.RoupaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

public class EdicaoDialog extends JDialog {

    private final RoupaService service;
    private final Roupa roupa;
    private boolean salvo = false;

    private final JTextField campoNome = new JTextField(25);
    private final JTextField campoCategoria = new JTextField(25);
    private final JComboBox<String> comboTamanho =
            new JComboBox<>(new String[]{"P", "M", "G", "GG"});
    private final JTextField campoCor = new JTextField(25);
    private final JTextField campoPreco = new JTextField(10);
    private final JTextField campoQuantidade = new JTextField(10);

    public EdicaoDialog(Window pai, RoupaService service, Roupa roupa) {
        // Modal: bloqueia a janela de trás até esta ser fechada
        super(pai, "Editar peça", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.roupa = roupa;

        montarTela();
        preencher();
        pack();
        setLocationRelativeTo(pai);
    }

    // A tela que abriu o diálogo pergunta se houve alteração
    public boolean foiSalvo() {
        return salvo;
    }

    private void montarTela() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        FormularioUtil.adicionarLinha(formulario, 0, "Nome:", campoNome);
        FormularioUtil.adicionarLinha(formulario, 1, "Categoria:", campoCategoria);
        FormularioUtil.adicionarLinha(formulario, 2, "Tamanho:", comboTamanho);
        FormularioUtil.adicionarLinha(formulario, 3, "Cor:", campoCor);
        FormularioUtil.adicionarLinha(formulario, 4, "Preço (R$):", campoPreco);
        FormularioUtil.adicionarLinha(formulario, 5, "Quantidade:", campoQuantidade);

        JButton botaoSalvar = new JButton("Salvar alterações");
        JButton botaoCancelar = new JButton("Cancelar");

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        botoes.add(botaoCancelar);
        botoes.add(botaoSalvar);

        setLayout(new BorderLayout());
        add(formulario, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(botaoSalvar); // Enter salva

        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> dispose());
    }

    // Mostra os dados atuais da peça nos campos
    private void preencher() {
        campoNome.setText(roupa.getNome());
        campoCategoria.setText(roupa.getCategoria());
        comboTamanho.setSelectedItem(roupa.getTamanho().name());
        campoCor.setText(roupa.getCor());
        campoPreco.setText(roupa.getPreco().toPlainString().replace(".", ","));
        campoQuantidade.setText(String.valueOf(roupa.getQuantidade()));
    }

    private void salvar() {
        try {
            service.atualizar(
                    roupa.getId(),
                    campoNome.getText(),
                    campoCategoria.getText(),
                    (String) comboTamanho.getSelectedItem(),
                    campoCor.getText(),
                    campoPreco.getText(),
                    campoQuantidade.getText());

            salvo = true;
            JOptionPane.showMessageDialog(this, "Peça atualizada com sucesso.",
                    "Editar peça", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (EstoqueException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível salvar", JOptionPane.WARNING_MESSAGE);
        }
    }
}