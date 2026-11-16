package br.edu.estoque.dao;

import br.edu.estoque.model.Usuario;
import java.util.Optional;

/** Contrato de acesso a dados dos administradores. */
public interface UsuarioDao {

    Optional<Usuario> buscarPorUsername(String username);
}
