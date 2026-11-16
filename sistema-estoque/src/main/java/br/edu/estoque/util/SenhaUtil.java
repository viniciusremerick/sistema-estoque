package br.edu.estoque.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash seguro de senhas com PBKDF2-HMAC-SHA256 (nativo do JDK, sem biblioteca extra).
 * Formato armazenado: {@code pbkdf2_sha256$iteracoes$salt(base64)$hash(base64)}.
 */
public final class SenhaUtil {

    private static final String PREFIXO = "pbkdf2_sha256";
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERACOES);
        return String.join("$", PREFIXO, String.valueOf(ITERACOES),
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash));
    }

    public static boolean verificar(char[] senha, String hashArmazenado) {
        if (senha == null || hashArmazenado == null) {
            return false;
        }
        String[] partes = hashArmazenado.split("\\$");
        if (partes.length != 4 || !PREFIXO.equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(senha, salt, iteracoes);
            return MessageDigest.isEqual(esperado, calculado); // comparação em tempo constante
        } catch (IllegalArgumentException e) {
            return false; // hash malformado no banco
        }
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível: " + ALGORITMO, e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Uso: java -cp sistema-estoque.jar br.edu.estoque.util.SenhaUtil minhaNovaSenha */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: SenhaUtil <senha>");
            return;
        }
        System.out.println(gerarHash(args[0]));
    }
}
