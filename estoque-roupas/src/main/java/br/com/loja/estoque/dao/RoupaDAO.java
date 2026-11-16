package br.com.loja.estoque.dao;

import br.com.loja.estoque.connection.ConexaoBD;
import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.model.Tamanho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoupaDAO implements Dao<Roupa> {

    private static final String SELECT_BASE =
            "SELECT id, nome, categoria, tamanho, cor, preco, quantidade FROM roupas";

    // ---------- CREATE ----------
    @Override
    public void inserir(Roupa roupa) throws EstoqueException {
        String sql = "INSERT INTO roupas (nome, categoria, tamanho, cor, preco, quantidade) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencherParametros(ps, roupa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roupa.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao inserir roupa.", e);
        }
    }

    // ---------- UPDATE ----------
    @Override
    public void atualizar(Roupa roupa) throws EstoqueException {
        String sql = "UPDATE roupas SET nome = ?, categoria = ?, tamanho = ?, "
                + "cor = ?, preco = ?, quantidade = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencherParametros(ps, roupa);
            ps.setInt(7, roupa.getId());

            if (ps.executeUpdate() == 0) {
                throw new EstoqueException("Roupa com id " + roupa.getId() + " não encontrada.");
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao atualizar roupa.", e);
        }
    }

    public void atualizarQuantidade(int id, int novaQuantidade) throws EstoqueException {
        String sql = "UPDATE roupas SET quantidade = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, novaQuantidade);
            ps.setInt(2, id);

            if (ps.executeUpdate() == 0) {
                throw new EstoqueException("Roupa com id " + id + " não encontrada.");
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao atualizar quantidade.", e);
        }
    }

    // ---------- DELETE ----------
    @Override
    public void excluir(int id) throws EstoqueException {
        String sql = "DELETE FROM roupas WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new EstoqueException("Roupa com id " + id + " não encontrada.");
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao excluir roupa.", e);
        }
    }

    // ---------- READ ----------
    @Override
    public Optional<Roupa> buscarPorId(int id) throws EstoqueException {
        List<Roupa> lista = consultar(SELECT_BASE + " WHERE id = ?", id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Roupa> listarTodos() throws EstoqueException {
        return consultar(SELECT_BASE + " ORDER BY nome, id");
    }

    public List<Roupa> buscarPorNome(String nome) throws EstoqueException {
        return consultar(SELECT_BASE + " WHERE nome ILIKE ? ORDER BY nome, id",
                "%" + nome.trim() + "%");
    }

    public List<Roupa> listarZeradas() throws EstoqueException {
        return consultar(SELECT_BASE + " WHERE quantidade = 0 ORDER BY nome, id");
    }

    // ---------- FILTROS (sobrecarga) ----------
    public List<Roupa> filtrar(Tamanho tamanho) throws EstoqueException {
        return consultar(SELECT_BASE + " WHERE tamanho = ? ORDER BY nome, id",
                tamanho.name());
    }

    public List<Roupa> filtrar(String categoria) throws EstoqueException {
        return consultar(SELECT_BASE + " WHERE LOWER(categoria) = LOWER(?) ORDER BY nome, id",
                categoria.trim());
    }

    public List<Roupa> filtrar(Tamanho tamanho, String categoria) throws EstoqueException {
        return consultar(SELECT_BASE
                        + " WHERE tamanho = ? AND LOWER(categoria) = LOWER(?) ORDER BY nome, id",
                tamanho.name(), categoria.trim());
    }

    // ---------- MÉTODOS AUXILIARES ----------
    private void preencherParametros(PreparedStatement ps, Roupa roupa) throws SQLException {
        ps.setString(1, roupa.getNome());
        ps.setString(2, roupa.getCategoria());
        ps.setString(3, roupa.getTamanho().name());
        ps.setString(4, roupa.getCor());
        ps.setBigDecimal(5, roupa.getPreco());
        ps.setInt(6, roupa.getQuantidade());
    }

    // Executa qualquer SELECT e devolve a lista de roupas encontradas
    private List<Roupa> consultar(String sql, Object... parametros) throws EstoqueException {
        List<Roupa> lista = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarRoupa(rs));
                }
            }
        } catch (SQLException e) {
            throw new EstoqueException("Erro ao consultar roupas.", e);
        }
        return lista;
    }

    // Converte UMA linha do banco em UM objeto Roupa
    private Roupa montarRoupa(ResultSet rs) throws SQLException {
        return new Roupa(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("categoria"),
                Tamanho.valueOf(rs.getString("tamanho")),
                rs.getString("cor"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade"));
    }
}