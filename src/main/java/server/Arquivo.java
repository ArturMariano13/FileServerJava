package server;

public class Arquivo {
    private String nome;
    private String conteudo;

    public Arquivo(String nome, String conteudo) {
        this.nome = nome;
        this.conteudo = conteudo;
    }

    public String getNome() {
        return nome;
    }

    public String getConteudo() {
        return conteudo;
    }
}

