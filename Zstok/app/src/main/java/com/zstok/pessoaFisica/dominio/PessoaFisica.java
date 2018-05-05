package com.zstok.pessoaFisica.dominio;

import java.util.Date;

public class PessoaFisica {
    private String cpf;
    private String dataNascimento;

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}