package com.zstok.perfil.negocio;

import android.support.design.widget.NavigationView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.zstok.perfil.persistencia.PerfilDAO;

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
}
