package br.edu.estoque.model;

import java.math.BigDecimal;

/**
 * Abstração de um item vendido pela loja. Guarda os dados comuns a qualquer produto;
 * cada tipo concreto (ex.: {@link Roupa}) acrescenta seus atributos e define sua descrição.
 */
public abstract class Produto {

    private Long id;
    private String nome;
    private String categoria;
    private BigDecimal preco;
    private int quantidade;

    protected Produto(Long id, String nome, String categoria, BigDecimal preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    /** Descrição curta usada em mensagens e diálogos; cada subtipo a monta do seu jeito. */
    public abstract String getDescricaoResumida();

    public boolean isSemEstoque() {
        return quantidade == 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
