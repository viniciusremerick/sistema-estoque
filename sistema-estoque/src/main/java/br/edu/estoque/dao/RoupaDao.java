package br.edu.estoque.dao;

import br.edu.estoque.model.FiltroRoupa;
import br.edu.estoque.model.ResumoEstoque;
import br.edu.estoque.model.Roupa;
import java.util.List;

/** Contrato de acesso a dados das roupas. */
public interface RoupaDao {

    /** Grava a roupa e devolve o ID gerado pelo banco. */
    long inserir(Roupa roupa);

    /** Lista roupas aplicando (de forma combinada) os filtros informados. */
    List<Roupa> listar(FiltroRoupa filtro);

    /** Lista somente as roupas com quantidade igual a zero. */
    List<Roupa> listarSemEstoque();

    /** @return true se alguma linha foi alterada (false = ID inexistente). */
    boolean atualizarQuantidade(long id, int novaQuantidade);

    List<String> listarCategorias();

    ResumoEstoque obterResumo();
}
