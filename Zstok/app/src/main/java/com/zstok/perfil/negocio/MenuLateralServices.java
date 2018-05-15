package com.zstok.perfil.negocio;

import android.support.design.widget.NavigationView;

import com.google.firebase.auth.FirebaseUser;
import com.zstok.perfil.persistencia.MenuLateralDAO;

public class MenuLateralServices {
    public static void setNomeEmailView(NavigationView navigationView, FirebaseUser user){
        MenuLateralDAO.setNomeEmailView(navigationView, user);
    }
}
