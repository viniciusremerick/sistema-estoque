package br.com.loja.estoque.dao;

import br.com.loja.estoque.connection.ConexaoBD;
import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministradorDAO implements Dao<Administrador> {

    private static final String SELECT_BASE =
            "SELECT id, usuario, senha_hash FROM administradores";

    @Override
    public void inserir(Administrador admin) throws EstoqueException {
        String sql = "INSERT INTO administradores (usuario, senha_hash) VALUES (?, ?) RETURNING id";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, admin.getUsuario());
            ps.setString(2, admin.getSenhaHash());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao inserir administrador.", e);
        }
    }

    @Override
    public void atualizar(Administrador admin) throws EstoqueException {
        String sql = "UPDATE administradores SET usuario = ?, senha_hash = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, admin.getUsuario());
            ps.setString(2, admin.getSenhaHash());
            ps.setInt(3, admin.getId());

            if (ps.executeUpdate() == 0) {
                throw new EstoqueException("Administrador com id " + admin.getId() + " não encontrado.");
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao atualizar administrador.", e);
        }
    }

    @Override
    public void excluir(int id) throws EstoqueException {
        String sql = "DELETE FROM administradores WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new EstoqueException("Administrador com id " + id + " não encontrado.");
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao excluir administrador.", e);
        }
    }

    @Override
    public Optional<Administrador> buscarPorId(int id) throws EstoqueException {
        List<Administrador> lista = consultar(SELECT_BASE + " WHERE id = ?", id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    public Optional<Administrador> buscarPorUsuario(String usuario) throws EstoqueException {
        List<Administrador> lista = consultar(SELECT_BASE + " WHERE usuario = ?", usuario);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Administrador> listarTodos() throws EstoqueException {
        return consultar(SELECT_BASE + " ORDER BY usuario");
    }

    private List<Administrador> consultar(String sql, Object... parametros) throws EstoqueException {
        List<Administrador> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Administrador(
                            rs.getInt("id"),
                            rs.getString("usuario"),
                            rs.getString("senha_hash")));
                }
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao consultar administradores.", e);
        }
        return lista;
    }
}