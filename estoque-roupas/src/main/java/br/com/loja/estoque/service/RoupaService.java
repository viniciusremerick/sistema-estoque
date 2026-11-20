package br.com.loja.estoque.service;
import java.util.ArrayList;
import br.com.loja.estoque.dao.RoupaDAO;
import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.exception.ValidacaoException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.model.Tamanho;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RoupaService {

    // NUMERIC(10,2) comporta até 99.999.999,99
    private static final BigDecimal PRECO_MAXIMO = new BigDecimal("99999999.99");

    private final RoupaDAO roupaDAO;
    private final AuthService authService;

    public RoupaService(AuthService authService) {
        this(new RoupaDAO(), authService);
    }

    public RoupaService(RoupaDAO roupaDAO, AuthService authService) {
        this.roupaDAO = roupaDAO;
        this.authService = authService;
    }

    // ========== CADASTRO ==========

    // Versão 1: recebe um objeto Roupa já montado
    public void cadastrar(Roupa roupa) throws EstoqueException {
        authService.exigirAutenticacao();
        validar(roupa);
        roupaDAO.inserir(roupa);
    }

    // Versão 2 (sobrecarga): recebe os textos digitados pelo usuário
    public void cadastrar(String nome, String categoria, String tamanho,
                          String cor, String preco, String quantidade) throws EstoqueException {
        authService.exigirAutenticacao();

        String nomeOk = validarTexto(nome, "Nome", 100);
        String categoriaOk = validarTexto(categoria, "Categoria", 50);
        Tamanho tamanhoOk = converterTamanho(tamanho);
        String corOk = validarTexto(cor, "Cor", 30);
        BigDecimal precoOk = converterPreco(preco);
        int quantidadeOk = converterInteiro(quantidade, "Quantidade");

        cadastrar(new Roupa(nomeOk, categoriaOk, tamanhoOk, corOk, precoOk, quantidadeOk));
    }

    // ========== ALTERAÇÃO ==========

    public void atualizar(Roupa roupa) throws EstoqueException {
        authService.exigirAutenticacao();
        validar(roupa);
        if (roupa.getId() <= 0) {
            throw new ValidacaoException("Roupa não identificada para atualização.");
        }
        roupaDAO.atualizar(roupa);
    }

    public void atualizarQuantidade(int id, int novaQuantidade) throws EstoqueException {
        authService.exigirAutenticacao();
        validarQuantidade(novaQuantidade);
        roupaDAO.atualizarQuantidade(id, novaQuantidade);
    }

    // Sobrecarga: aceita os textos digitados
    public void atualizarQuantidade(String id, String novaQuantidade) throws EstoqueException {
        authService.exigirAutenticacao();
        atualizarQuantidade(
                converterInteiro(id, "Código da roupa"),
                converterInteiro(novaQuantidade, "Quantidade"));
    }

    public void excluir(int id) throws EstoqueException {
        authService.exigirAutenticacao();
        roupaDAO.excluir(id);
    }

    // ========== CONSULTAS (abertas, sem login) ==========

    public Roupa buscarPorId(int id) throws EstoqueException {
        return roupaDAO.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Roupa com código " + id + " não encontrada."));
    }

    public List<Roupa> listarTodos() throws EstoqueException {
        return roupaDAO.listarTodos();
    }

    public List<Roupa> buscarPorNome(String nome) throws EstoqueException {
        if (nome == null || nome.isBlank()) {
            return roupaDAO.listarTodos();
        }
        return roupaDAO.buscarPorNome(nome);
    }

    // Filtros combináveis: qualquer um dos dois pode ficar em branco
    public List<Roupa> filtrar(String tamanho, String categoria) throws EstoqueException {
        boolean temTamanho = tamanho != null && !tamanho.isBlank();
        boolean temCategoria = categoria != null && !categoria.isBlank();

        if (temTamanho && temCategoria) {
            return roupaDAO.filtrar(converterTamanho(tamanho), categoria);
        }
        if (temTamanho) {
            return roupaDAO.filtrar(converterTamanho(tamanho));
        }
        if (temCategoria) {
            return roupaDAO.filtrar(categoria);
        }
        return roupaDAO.listarTodos();
    }

    public List<Roupa> listarZeradas() throws EstoqueException {
        return roupaDAO.listarZeradas();
    }

    // Busca combinada: nome + tamanho + categoria (qualquer um pode ficar em branco)
    public List<Roupa> pesquisar(String nome, String tamanho, String categoria)
            throws EstoqueException {
        List<Roupa> filtradas = filtrar(tamanho, categoria);

        if (nome == null || nome.isBlank()) {
            return filtradas;
        }

        String termo = nome.trim().toLowerCase();
        List<Roupa> resultado = new ArrayList<>();
        for (Roupa roupa : filtradas) {
            if (roupa.getNome().toLowerCase().contains(termo)) {
                resultado.add(roupa);
            }
        }
        return resultado;
    }

    // ========== VALIDAÇÕES (privadas) ==========

    // Valida e também "limpa" o objeto (tira espaços, ajusta casas decimais)
    private void validar(Roupa roupa) throws ValidacaoException {
        if (roupa == null) {
            throw new ValidacaoException("Roupa não informada.");
        }

        roupa.setNome(validarTexto(roupa.getNome(), "Nome", 100));
        roupa.setCategoria(validarTexto(roupa.getCategoria(), "Categoria", 50));
        roupa.setCor(validarTexto(roupa.getCor(), "Cor", 30));

        if (roupa.getTamanho() == null) {
            throw new ValidacaoException("Tamanho é obrigatório (P, M, G ou GG).");
        }

        if (roupa.getPreco() == null) {
            throw new ValidacaoException("Preço é obrigatório.");
        }
        BigDecimal preco = roupa.getPreco().setScale(2, RoundingMode.HALF_UP);
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("O preço deve ser maior que zero.");
        }
        if (preco.compareTo(PRECO_MAXIMO) > 0) {
            throw new ValidacaoException("O preço máximo permitido é R$ 99.999.999,99.");
        }
        roupa.setPreco(preco);

        validarQuantidade(roupa.getQuantidade());
    }

    private String validarTexto(String valor, String campo, int tamanhoMaximo)
            throws ValidacaoException {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(campo + " é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > tamanhoMaximo) {
            throw new ValidacaoException(
                    campo + " deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return limpo;
    }

    private void validarQuantidade(int quantidade) throws ValidacaoException {
        if (quantidade < 0) {
            throw new ValidacaoException("A quantidade não pode ser negativa.");
        }
    }

    // ========== CONVERSÕES (texto -> tipo certo) ==========

    private Tamanho converterTamanho(String texto) throws ValidacaoException {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Tamanho é obrigatório (P, M, G ou GG).");
        }
        try {
            return Tamanho.deTexto(texto);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    private BigDecimal converterPreco(String texto) throws ValidacaoException {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Preço é obrigatório.");
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Preço inválido: use apenas números (ex.: 49,90).");
        }
    }

    private int converterInteiro(String texto, String campo) throws ValidacaoException {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException(campo + " é obrigatório.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException(campo + " inválido: informe um número inteiro.");
        }
    }
}