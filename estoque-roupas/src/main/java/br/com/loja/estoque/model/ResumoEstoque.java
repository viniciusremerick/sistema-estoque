package br.com.loja.estoque.model;

import java.math.BigDecimal;

// Um "record" é uma classe de dados imutável: o Java gera construtor e
// métodos de leitura automaticamente (ex.: resumo.totalPecas())
public record ResumoEstoque(int totalPecas,
                            int totalUnidades,
                            int pecasZeradas,
                            BigDecimal valorTotal) {
}