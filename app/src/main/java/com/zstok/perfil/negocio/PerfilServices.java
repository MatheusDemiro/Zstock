package com.zstok.perfil.negocio;

import com.zstok.perfil.persistencia.PerfilDAO;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class PerfilServices {
    public static boolean alterarNome(String novoNome){
        return PerfilDAO.insereNome(novoNome);
    }
    public static boolean alterarEmail(String novoEmail){
        return PerfilDAO.insereEmail(novoEmail);
    }
    public static boolean alterarTelefone(Pessoa pessoa){
        return PerfilDAO.insereTelefone(pessoa);
    }
    public static boolean alterarEndereco(Pessoa pessoa){
        return PerfilDAO.insereEndereco(pessoa);
    }
    public static boolean alterarCpf(PessoaFisica pessoaFisica){
        return PerfilDAO.insereCpf(pessoaFisica);
    }
    public static boolean alterarCnpj(PessoaJuridica pessoaJuridica){
        return PerfilDAO.insereCnpj(pessoaJuridica);
    }
    public static boolean alterarDataNascimento(PessoaFisica pessoaFisica){
        return PerfilDAO.insereDataNascimento(pessoaFisica);
    }
    public static boolean alterarRazaoSocial(PessoaJuridica pessoaJuridica){
        return PerfilDAO.insereRazaoSocial(pessoaJuridica);
    }
}
