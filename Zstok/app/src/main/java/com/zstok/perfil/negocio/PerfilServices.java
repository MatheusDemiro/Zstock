package com.zstok.perfil.negocio;

import android.support.design.widget.NavigationView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.zstok.perfil.persistencia.PerfilDAO;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class PerfilServices {
    public static void setNomeEmailView(NavigationView navigationView, FirebaseUser user){
        PerfilDAO.setNomeEmailView(navigationView, user);
    }
    public static boolean alterarNome(String novoNome){
        return PerfilDAO.insereNome(novoNome);
    }
    public static boolean alterarEmail(String novoEmail, String senha){
        return PerfilDAO.insereEmail(novoEmail, senha);
    }
    public static boolean alterarTelefone(String novoTelefone){
        return PerfilDAO.insereTelefone(novoTelefone);
    }
    public static boolean alterarEndereco(Pessoa pessoa){
        return PerfilDAO.insereEndereco(pessoa);
    }
    public static boolean alterarCpf(PessoaFisica pessoaFisica){
        return PerfilDAO.insereCpf(pessoaFisica);
    }
    public static boolean alterarDataNascimento(PessoaFisica pessoaFisica){
        return PerfilDAO.insereDataNascimento(pessoaFisica);
    }
}
