package br.com.loja.estoque.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoBD {

    private static final String ARQUIVO_CONFIG = "db.properties";
    private static final Properties CONFIG = carregarConfig();

    // Construtor privado: a classe só tem métodos estáticos
    private ConexaoBD() {
    }

    private static Properties carregarConfig() {
        Properties props = new Properties();
        try (InputStream entrada = ConexaoBD.class
                .getClassLoader()
                .getResourceAsStream(ARQUIVO_CONFIG)) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "Arquivo " + ARQUIVO_CONFIG + " não encontrado em src/main/resources.");
            }
            props.load(entrada);

        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler " + ARQUIVO_CONFIG, e);
        }
        return props;
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.usuario"),
                CONFIG.getProperty("db.senha"));
    }
}