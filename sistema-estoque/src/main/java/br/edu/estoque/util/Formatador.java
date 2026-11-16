package br.edu.estoque.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Formatação de valores para exibição. */
public final class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Formatador() {
    }

    public static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor);
    }

    public static String inteiro(long valor) {
        return NumberFormat.getIntegerInstance(PT_BR).format(valor);
    }
}
