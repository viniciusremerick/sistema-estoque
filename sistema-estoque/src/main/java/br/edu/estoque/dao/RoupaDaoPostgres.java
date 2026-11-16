package br.edu.estoque.dao;

import br.edu.estoque.connection.ConexaoFactory;
import br.edu.estoque.exception.DaoException;
import br.edu.estoque.model.FiltroRoupa;
import br.edu.estoque.model.ResumoEstoque;
import br.edu.estoque.model.Roupa;
import br.edu.estoque.model.Tamanho;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementação JDBC/PostgreSQL de {@link RoupaDao}. Todas as consultas usam PreparedStatement. */
public class RoupaDaoPostgres implements RoupaDao {

    private static final String COLUNAS = "id, nome, categoria, tamanho, cor, preco, quantidade";
    private static final String ORDEM = " ORDER BY nome, "
            + "CASE tamanho WHEN 'P' THEN 1 WHEN 'M' THEN 2 WHEN 'G' THEN 3 ELSE 4 END, cor";

    private static final String SQL_INSERIR =
            "INSERT INTO roupa (nome, categoria, tamanho, cor, preco, quantidade) "
                    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String SQL_SEM_ESTOQUE =
            "SELECT " + COLUNAS + " FROM roupa WHERE quantidade = 0" + ORDEM;
    private static final String SQL_ATUALIZAR_QUANTIDADE =
            "UPDATE roupa SET quantidade = ? WHERE id = ?";
    private static final String SQL_CATEGORIAS =
            "SELECT DISTINCT categoria FROM roupa ORDER BY categoria";
    private static final String SQL_RESUMO =
            "SELECT COUNT(*) AS total_produtos, "
                    + "COALESCE(SUM(quantidade), 0) AS total_pecas, "
                    + "COUNT(*) FILTER (WHERE quantidade = 0) AS produtos_zerados "
                    + "FROM roupa";

    @Override
    public long inserir(Roupa roupa) {
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR)) {
            ps.setString(1, roupa.getNome());
            ps.setString(2, roupa.getCategoria());
            ps.setString(3, roupa.getTamanho().name());
            ps.setString(4, roupa.getCor());
            ps.setBigDecimal(5, roupa.getPreco());
            ps.setInt(6, roupa.getQuantidade());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar a roupa no banco de dados.", e);
        }
    }

    @Override
    public List<Roupa> listar(FiltroRoupa filtro) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM roupa WHERE 1 = 1");
        List<String> parametros = new ArrayList<>();

        // Apenas fragmentos fixos são concatenados; todo valor do usuário vai por parâmetro (?).
        if (filtro.nome() != null && !filtro.nome().isBlank()) {
            sql.append(" AND nome ILIKE ?");
            parametros.add("%" + escaparLike(filtro.nome().trim()) + "%");
        }
        if (filtro.tamanho() != null) {
            sql.append(" AND tamanho = ?");
            parametros.add(filtro.tamanho().name());
        }
        if (filtro.categoria() != null && !filtro.categoria().isBlank()) {
            sql.append(" AND categoria = ?");
            parametros.add(filtro.categoria().trim());
        }
        sql.append(ORDEM);

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar o estoque.", e);
        }
    }

    @Override
    public List<Roupa> listarSemEstoque() {
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_SEM_ESTOQUE)) {
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar os produtos sem estoque.", e);
        }
    }

    @Override
    public boolean atualizarQuantidade(long id, int novaQuantidade) {
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_ATUALIZAR_QUANTIDADE)) {
            ps.setInt(1, novaQuantidade);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar a quantidade em estoque.", e);
        }
    }

    @Override
    public List<String> listarCategorias() {
        List<String> categorias = new ArrayList<>();
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_CATEGORIAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
            return categorias;
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar as categorias.", e);
        }
    }

    @Override
    public ResumoEstoque obterResumo() {
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(SQL_RESUMO);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new ResumoEstoque(rs.getLong("total_produtos"),
                    rs.getLong("total_pecas"), rs.getLong("produtos_zerados"));
        } catch (SQLException e) {
            throw new DaoException("Erro ao calcular o resumo do estoque.", e);
        }
    }

    private List<Roupa> mapearLista(PreparedStatement ps) throws SQLException {
        List<Roupa> roupas = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roupas.add(mapear(rs));
            }
        }
        return roupas;
    }

    private Roupa mapear(ResultSet rs) throws SQLException {
        return new Roupa(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("categoria"),
                Tamanho.valueOf(rs.getString("tamanho")),
                rs.getString("cor"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade"));
    }

    /** Neutraliza curingas do LIKE para que "50%" seja buscado literalmente. */
    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
