package br.edu.estoque.connection;

import br.edu.estoque.exception.DaoException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lê as credenciais do banco fora do código-fonte. Ordem de prioridade:
 * 1) variáveis de ambiente DB_URL / DB_USER / DB_PASSWORD;
 * 2) arquivo apontado por -Ddb.config=caminho;
 * 3) arquivo config/db.properties (relativo à pasta de execução).
 */
public final class ConfiguracaoBanco {

    private static final String ARQUIVO_PADRAO = "config/db.properties";

    private final String url;
    private final String usuario;
    private final String senha;

    private ConfiguracaoBanco(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public static ConfiguracaoBanco carregar() {
        Properties arquivo = lerArquivoSeExistir();
        String url = valor("DB_URL", "db.url", arquivo);
        String usuario = valor("DB_USER", "db.user", arquivo);
        String senha = valor("DB_PASSWORD", "db.password", arquivo);

        if (url == null || usuario == null || senha == null) {
            throw new DaoException("Configuração do banco não encontrada.\n"
                    + "Crie o arquivo " + ARQUIVO_PADRAO + " a partir de config/db.properties.example\n"
                    + "ou defina as variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD.");
        }
        return new ConfiguracaoBanco(url, usuario, senha);
    }

    private static String valor(String variavelAmbiente, String chave, Properties arquivo) {
        String doAmbiente = System.getenv(variavelAmbiente);
        if (doAmbiente != null && !doAmbiente.isBlank()) {
            return doAmbiente;
        }
        String doArquivo = arquivo.getProperty(chave);
        return (doArquivo == null || doArquivo.isBlank()) ? null : doArquivo.trim();
    }

    private static Properties lerArquivoSeExistir() {
        Properties props = new Properties();
        Path caminho = Path.of(System.getProperty("db.config", ARQUIVO_PADRAO));
        if (Files.isReadable(caminho)) {
            try (InputStream in = Files.newInputStream(caminho)) {
                props.load(in);
            } catch (IOException e) {
                throw new DaoException("Não foi possível ler o arquivo de configuração: " + caminho, e);
            }
        }
        return props;
    }

    public String getUrl() { return url; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }
}
