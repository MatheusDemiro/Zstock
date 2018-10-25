package com.zstok.mensagem.dominio;

public class Mensagem {

    private String texto;
    private String autor;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String string) {
        this.texto = string;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String string) {
        this.autor = string;
    }
}
