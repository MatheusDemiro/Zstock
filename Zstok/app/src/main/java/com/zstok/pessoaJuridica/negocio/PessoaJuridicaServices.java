package com.zstok.pessoaJuridica.negocio;

import com.zstok.pessoaJuridica.dominio.PessoaJuridica;
import com.zstok.pessoaJuridica.persistencia.PessoaJuridicaDAO;

public class PessoaJuridicaServices {
    public static boolean inserirPessoaJuridica(PessoaJuridica pessoaJuridica){
        return PessoaJuridicaDAO.inserirPessoaJuridica(pessoaJuridica);
    }
}
