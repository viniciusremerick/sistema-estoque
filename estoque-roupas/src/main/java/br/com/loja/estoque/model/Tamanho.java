package br.com.loja.estoque.model;

public enum Tamanho {
    P, M, G, GG;

    // Converte um texto digitado pelo usuário em um Tamanho
    public static Tamanho deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Tamanho não informado.");
        }
        try {
            return Tamanho.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tamanho inválido: " + texto + ". Use P, M, G ou GG.");
        }
    }
}