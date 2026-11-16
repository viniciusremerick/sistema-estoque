package br.edu.estoque.connection;

import br.edu.estoque.exception.DaoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Ponto único de criação de conexões JDBC com o PostgreSQL. */
public final class ConexaoFactory {

    private static ConfiguracaoBanco configuracao;

    private ConexaoFactory() {
    }

    /** Abre uma nova conexão; quem chamar deve fechá-la (try-with-resources). */
    public static Connection obterConexao() {
        try {
            ConfiguracaoBanco cfg = configuracao();
            return DriverManager.getConnection(cfg.getUrl(), cfg.getUsuario(), cfg.getSenha());
        } catch (SQLException e) {
            throw new DaoException("Não foi possível conectar ao PostgreSQL. "
                    + "Verifique se o servidor está ativo e se a configuração está correta.", e);
        }
    }

    private static synchronized ConfiguracaoBanco configuracao() {
        if (configuracao == null) {
            configuracao = ConfiguracaoBanco.carregar();
        }
        return configuracao;
    }
}
