package br.edu.estoque.model;

import java.math.BigDecimal;

/** Peça de roupa: produto com tamanho e cor. */
public class Roupa extends Produto {

    private Tamanho tamanho;
    private String cor;

    public Roupa(Long id, String nome, String categoria, Tamanho tamanho, String cor,
                 BigDecimal preco, int quantidade) {
        super(id, nome, categoria, preco, quantidade);
        this.tamanho = tamanho;
        this.cor = cor;
    }

    /** Construtor para peças ainda não gravadas (o ID é gerado pelo banco). */
    public Roupa(String nome, String categoria, Tamanho tamanho, String cor,
                 BigDecimal preco, int quantidade) {
        this(null, nome, categoria, tamanho, cor, preco, quantidade);
    }

    @Override
    public String getDescricaoResumida() {
        return getNome() + " (" + tamanho + ", " + cor + ")";
    }

    public Tamanho getTamanho() { return tamanho; }
    public void setTamanho(Tamanho tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    @Override
    public String toString() {
        return "Roupa{id=" + getId() + ", " + getDescricaoResumida() + ", qtd=" + getQuantidade() + "}";
    }
}
