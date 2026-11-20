package br.com.loja.estoque.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatador {

    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private Formatador() {
    }

    public static String moeda(BigDecimal valor) {
        return valor == null ? "" : MOEDA.format(valor);
    }
}