package br.edu.estoque.model;

/** Números exibidos no dashboard. */
public record ResumoEstoque(long totalProdutos, long totalPecas, long produtosZerados) {
}
