package br.edu.estoque.service;

import br.edu.estoque.dao.RoupaDao;
import br.edu.estoque.exception.AutenticacaoException;
import br.edu.estoque.exception.DaoException;
import br.edu.estoque.exception.ValidacaoException;
import br.edu.estoque.model.FiltroRoupa;
import br.edu.estoque.model.ResumoEstoque;
import br.edu.estoque.model.Roupa;
import br.edu.estoque.util.Validador;
import java.util.List;

/** Regras de negócio do estoque: validação, autorização e orquestração do DAO. */
public class EstoqueService {

    private final RoupaDao roupaDao;
    private final Sessao sessao;

    public EstoqueService(RoupaDao roupaDao, Sessao sessao) {
        this.roupaDao = roupaDao;
        this.sessao = sessao;
    }

    // ---------- operações administrativas (exigem login) ----------

    /** RF-01 */
    public Roupa cadastrarRoupa(String nome, String categoria, String tamanho, String cor,
                                String preco, String quantidade)
            throws ValidacaoException, AutenticacaoException {
        exigirAdministrador();
        Roupa roupa = new Roupa(
                Validador.textoObrigatorio(nome, "Nome", 100),
                Validador.textoObrigatorio(categoria, "Categoria", 50),
                Validador.tamanho(tamanho),
                Validador.textoObrigatorio(cor, "Cor", 30),
                Validador.preco(preco),
                Validador.quantidade(quantidade));
        try {
            roupa.setId(roupaDao.inserir(roupa));
            return roupa;
        } catch (DaoException e) {
            if (e.isViolacaoDeUnicidade()) {
                throw new ValidacaoException("Já existe uma peça \"" + roupa.getDescricaoResumida()
                        + "\" cadastrada. Use \"Atualizar quantidade\" na tela de estoque.");
            }
            throw e;
        }
    }

    /** RF-05 */
    public void atualizarQuantidade(long idRoupa, long novaQuantidade)
            throws ValidacaoException, AutenticacaoException {
        exigirAdministrador();
        int quantidade = Validador.quantidade(novaQuantidade);
        if (!roupaDao.atualizarQuantidade(idRoupa, quantidade)) {
            throw new ValidacaoException("Produto não encontrado. Atualize a lista e tente novamente.");
        }
    }

    // ---------- consultas ----------

    /** RF-02 */
    public List<Roupa> listar(FiltroRoupa filtro) {
        return roupaDao.listar(filtro == null ? FiltroRoupa.semFiltros() : filtro);
    }

    /** RF-04 */
    public List<Roupa> listarSemEstoque() {
        return roupaDao.listarSemEstoque();
    }

    public List<String> listarCategorias() {
        return roupaDao.listarCategorias();
    }

    public ResumoEstoque obterResumo() {
        return roupaDao.obterResumo();
    }

    private void exigirAdministrador() throws AutenticacaoException {
        if (!sessao.isAutenticado()) {
            throw new AutenticacaoException("Operação restrita a administradores. Faça login.");
        }
    }
}
