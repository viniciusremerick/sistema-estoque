package br.com.loja.estoque.dao;

import br.com.loja.estoque.exception.EstoqueException;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    void inserir(T objeto) throws EstoqueException;

    void atualizar(T objeto) throws EstoqueException;

    void excluir(int id) throws EstoqueException;

    Optional<T> buscarPorId(int id) throws EstoqueException;

    List<T> listarTodos() throws EstoqueException;
}