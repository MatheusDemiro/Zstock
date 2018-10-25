package com.zstok.pessoa.dominio;

import java.util.Date;

public class PessoaFisica {
    private String cpf;
    private Date dataNascimento;

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public Date getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}