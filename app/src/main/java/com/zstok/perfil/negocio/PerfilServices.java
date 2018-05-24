package com.zstok.perfil.negocio;

import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.zstok.perfil.persistencia.PerfilDAO;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilServices {
    public static void setDadosNavHeader(FirebaseUser user, TextView tvNomeUsuarioNavHeader, TextView tvEmailUsuarioNavHeader){
        PerfilDAO.setDadosNavHeader(user, tvNomeUsuarioNavHeader, tvEmailUsuarioNavHeader);
    }
    public static void resgatarFoto(CircleImageView circleImageView){
        PerfilDAO.resgatarFoto(circleImageView);
    }
    public static void insereFoto(Uri uriFoto){
        PerfilDAO.insereFoto(uriFoto);
    }
    public static boolean alterarNome(String novoNome){
        return PerfilDAO.insereNome(novoNome);
    }
    public static boolean alterarEmail(String novoEmail){
        return PerfilDAO.insereEmail(novoEmail);
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
    public static boolean alterarCnpj(PessoaJuridica pessoaJuridica){
        return PerfilDAO.insereCnpj(pessoaJuridica);
    }
    public static boolean alterarDataNascimento(PessoaFisica pessoaFisica){
        return PerfilDAO.insereDataNascimento(pessoaFisica);
    }
    public static boolean alterarRazaoSocial(String razaoSocial){
        return PerfilDAO.insereRazaoSocial(razaoSocial);
    }
}
