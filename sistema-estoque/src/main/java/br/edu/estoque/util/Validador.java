package br.edu.estoque.util;

import br.edu.estoque.exception.ValidacaoException;
import br.edu.estoque.model.Tamanho;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/** Validações e conversões dos dados digitados pelo usuário. */
public final class Validador {

    private static final BigDecimal PRECO_MAXIMO = new BigDecimal("99999999.99"); // NUMERIC(10,2)
    private static final Pattern DECIMAL_COM_PONTO = Pattern.compile("\\d+\\.\\d{1,2}");

    private Validador() {
    }

    /** Remove espaços das pontas, exige conteúdo e respeita o tamanho máximo da coluna. */
    public static String textoObrigatorio(String valor, String campo, int tamanhoMaximo) throws ValidacaoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new ValidacaoException("O campo \"" + campo + "\" aceita no máximo "
                    + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }

    public static Tamanho tamanho(String valor) throws ValidacaoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Tamanho\" é obrigatório.");
        }
        try {
            return Tamanho.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException("Tamanho inválido. Use somente P, M, G ou GG.");
        }
    }

    /**
     * Aceita "49,90", "49.90", "R$ 1.249,90" e "1249". Convenção brasileira: vírgula é o
     * separador decimal; ponto só é decimal quando seguido de 1 ou 2 dígitos ("49.9", "49.90").
     */
    public static BigDecimal preco(String valor) throws ValidacaoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Preço\" é obrigatório.");
        }
        String texto = valor.trim().replace("R$", "").replace(" ", "");
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        } else if (texto.contains(".") && !DECIMAL_COM_PONTO.matcher(texto).matches()) {
            texto = texto.replace(".", ""); // ponto como separador de milhar
        }
        BigDecimal preco;
        try {
            preco = new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Preço inválido. Exemplo: 49,90");
        }
        if (preco.signum() <= 0) {
            throw new ValidacaoException("O preço deve ser maior que zero.");
        }
        preco = preco.setScale(2, RoundingMode.HALF_UP);
        if (preco.signum() <= 0) {
            throw new ValidacaoException("O preço deve ser maior que zero.");
        }
        if (preco.compareTo(PRECO_MAXIMO) > 0) {
            throw new ValidacaoException("Preço acima do limite permitido.");
        }
        return preco;
    }

    public static int quantidade(String valor) throws ValidacaoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Quantidade\" é obrigatório.");
        }
        long numero;
        try {
            numero = Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Quantidade inválida. Informe um número inteiro.");
        }
        return quantidade(numero);
    }

    public static int quantidade(long numero) throws ValidacaoException {
        if (numero < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
        if (numero > Integer.MAX_VALUE) {
            throw new ValidacaoException("Quantidade acima do limite permitido.");
        }
        return (int) numero;
    }

    /** Transforma texto vazio em null (usado nos filtros opcionais). */
    public static String nuloSeVazio(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }
}
