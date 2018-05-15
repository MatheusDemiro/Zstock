package com.zstok.pessoa.dominio;

public class Pessoa {
    private String nome;
    private String telefone;
    private String endreco;

    public String getEndreco() {
        return endreco;
    }
    public void setEndreco(String endreco) {
        this.endreco = endreco;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
