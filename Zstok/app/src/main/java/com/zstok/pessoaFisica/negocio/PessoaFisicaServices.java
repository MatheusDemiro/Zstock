package com.zstok.pessoaFisica.negocio;

import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaFisica.persistencia.PessoaFisicaDAO;

public class PessoaFisicaServices {
    public static boolean inserirPessoaFisica(PessoaFisica pessoaFisica){
        return PessoaFisicaDAO.inserirPessoaFisica(pessoaFisica);
    }
}
