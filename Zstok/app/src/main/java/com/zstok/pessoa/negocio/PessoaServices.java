package com.zstok.pessoa.negocio;

import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoa.persistencia.PessoaDAO;

public class PessoaServices {
    public static boolean inserirPessoa(Pessoa pessoa){
        return PessoaDAO.inserirPessoa(pessoa);
    }
}
