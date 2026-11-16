package br.edu.estoque.model;

/**
 * Filtros combináveis da listagem. Qualquer campo nulo/vazio significa "sem filtro".
 */
public record FiltroRoupa(String nome, Tamanho tamanho, String categoria) {

    public static FiltroRoupa semFiltros() {
        return new FiltroRoupa(null, null, null);
    }
}
