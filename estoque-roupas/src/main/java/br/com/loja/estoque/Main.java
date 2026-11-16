package br.com.loja.estoque;

import br.com.loja.estoque.dao.AdministradorDAO;
import br.com.loja.estoque.dao.RoupaDAO;
import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Administrador;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.model.Tamanho;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        RoupaDAO roupaDAO = new RoupaDAO();
        AdministradorDAO adminDAO = new AdministradorDAO();

        try {
            // INSERT
            Roupa r = new Roupa("Camiseta Básica", "Camisetas",
                    Tamanho.M, "Preta", new BigDecimal("49.90"), 10);
            roupaDAO.inserir(r);
            System.out.println("Inserida com id " + r.getId());

            System.out.println("--- todas ---");
            roupaDAO.listarTodos().forEach(System.out::println);

            // UPDATE
            roupaDAO.atualizarQuantidade(r.getId(), 0);
            System.out.println("--- zeradas ---");
            roupaDAO.listarZeradas().forEach(System.out::println);

            // FILTROS E BUSCA
            System.out.println("--- tamanho M ---");
            roupaDAO.filtrar(Tamanho.M).forEach(System.out::println);
            System.out.println("--- busca 'camis' ---");
            roupaDAO.buscarPorNome("camis").forEach(System.out::println);

            // DELETE
            roupaDAO.excluir(r.getId());
            System.out.println("Roupas restantes: " + roupaDAO.listarTodos().size());

            // ADMINISTRADOR (hash falso, só para teste)
            Administrador a = new Administrador("admin_teste", "hash-de-teste");
            adminDAO.inserir(a);
            System.out.println(adminDAO.buscarPorUsuario("admin_teste").orElse(null));
            adminDAO.excluir(a.getId());

            // ERRO ESPERADO: preço zero é barrado pelo banco
            try {
                roupaDAO.inserir(new Roupa("Calça", "Calças",
                        Tamanho.G, "Azul", BigDecimal.ZERO, 5));
            } catch (EstoqueException ex) {
                System.out.println("Erro esperado: " + ex.getMessage());
                System.out.println("Causa: " + ex.getCause().getMessage());
            }

        } catch (EstoqueException e) {
            System.out.println("Falha: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Causa: " + e.getCause().getMessage());
            }
        }
    }
}