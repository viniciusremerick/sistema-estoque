package br.com.loja.estoque.model;

import java.math.BigDecimal;

public class Roupa {

    private int id;
    private String nome;
    private String categoria;
    private Tamanho tamanho;
    private String cor;
    private BigDecimal preco;
    private int quantidade;

    // Construtor vazio
    public Roupa() {
    }

    // Para cadastrar uma peça nova (ainda sem id)
    public Roupa(String nome, String categoria, Tamanho tamanho,
                 String cor, BigDecimal preco, int quantidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.cor = cor;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    // Para montar uma peça que veio do banco (com id)
    public Roupa(int id, String nome, String categoria, Tamanho tamanho,
                 String cor, BigDecimal preco, int quantidade) {
        this(nome, categoria, tamanho, cor, preco, quantidade);
        this.id = id;
    }

    // Comportamento do próprio objeto
    public boolean estaZerada() {
        return quantidade == 0;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Tamanho getTamanho() { return tamanho; }
    public void setTamanho(Tamanho tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | %s | R$ %.2f | Estoque: %d",
                id, nome, categoria, tamanho, cor, preco, quantidade);
    }
}