package br.com.loja.estoque.model;

public class Administrador {

    private int id;
    private String usuario;
    private String senhaHash;

    public Administrador() {
    }

    public Administrador(String usuario, String senhaHash) {
        this.usuario = usuario;
        this.senhaHash = senhaHash;
    }

    public Administrador(int id, String usuario, String senhaHash) {
        this(usuario, senhaHash);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    // Propositalmente NÃO mostra o hash
    @Override
    public String toString() {
        return "Administrador{id=" + id + ", usuario='" + usuario + "'}";
    }
}