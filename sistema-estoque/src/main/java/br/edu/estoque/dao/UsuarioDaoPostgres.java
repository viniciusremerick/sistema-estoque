package br.edu.estoque.dao;

import br.edu.estoque.connection.ConexaoFactory;
import br.edu.estoque.exception.DaoException;
import br.edu.estoque.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/** Implementação JDBC/PostgreSQL de {@link UsuarioDao}. */
public class UsuarioDaoPostgres implements UsuarioDao {

    private static final String SQL_BUSCAR =
            "SELECT id, username, senha_hash FROM usuario WHERE username = ?";

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Usuario(rs.getInt("id"),
                        rs.getString("username"), rs.getString("senha_hash")));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar o usuário no banco de dados.", e);
        }
    }
}
