package br.edu.estoque.model;

/** Administrador do sistema. A senha é guardada somente como hash. */
public class Usuario {

    private final int id;
    private final String username;
    private final String senhaHash;

    public Usuario(int id, String username, String senhaHash) {
        this.id = id;
        this.username = username;
        this.senhaHash = senhaHash;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getSenhaHash() { return senhaHash; }
}
