package br.com.loja.estoque;

import br.com.loja.estoque.exception.EstoqueException;
import br.com.loja.estoque.model.Roupa;
import br.com.loja.estoque.service.AuthService;
import br.com.loja.estoque.service.RoupaService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        AuthService auth = new AuthService();
        RoupaService service = new RoupaService(auth);

        try {
            // 1. Sem login, não pode cadastrar
            tentarCadastrar(service, "1. sem login          ");

            // 2. Primeiro acesso: cria o administrador
            if (!auth.existeAdministrador()) {
                System.out.println("Primeiro acesso. Crie o administrador.");
                System.out.print("Usuário (mín. 3 caracteres): ");
                String usuario = entrada.nextLine();
                System.out.print("Senha (mín. 6 caracteres): ");
                String senha = entrada.nextLine();
                auth.cadastrarAdministrador(usuario, senha);
                System.out.println("Administrador criado.");
            }

            // 3. Login com senha errada
            System.out.print("Usuário para login: ");
            String usuarioLogin = entrada.nextLine();
            try {
                auth.login(usuarioLogin, "senha-errada-123");
            } catch (EstoqueException e) {
                System.out.println("3. senha errada        -> bloqueado: " + e.getMessage());
            }

            // 4. Login correto
            System.out.print("Senha: ");
            String senhaLogin = entrada.nextLine();
            auth.login(usuarioLogin, senhaLogin);
            System.out.println("4. login correto       -> " + auth.getAdministradorLogado());

            // 5. Logado, pode cadastrar
            tentarCadastrar(service, "5. logado             ");
            System.out.println("--- roupas ---");
            service.listarTodos().forEach(System.out::println);

            // Limpeza do teste
            Roupa teste = service.buscarPorNome("Camiseta Teste").get(0);
            service.excluir(teste.getId());

            // 6. Depois do logout, volta a bloquear
            auth.logout();
            tentarCadastrar(service, "6. após logout        ");

            // 7. Consultas continuam abertas
            System.out.println("7. consulta sem login  -> " + service.listarTodos().size() + " roupas");

        } catch (EstoqueException e) {
            System.out.println("Falha: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Causa: " + e.getCause().getMessage());
            }
        }
    }

    private static void tentarCadastrar(RoupaService service, String descricao) {
        try {
            service.cadastrar("Camiseta Teste", "Camisetas", "M", "Preta", "49,90", "10");
            System.out.println(descricao + "-> cadastrada");
        } catch (EstoqueException e) {
            System.out.println(descricao + "-> bloqueado: " + e.getMessage());
        }
    }
}