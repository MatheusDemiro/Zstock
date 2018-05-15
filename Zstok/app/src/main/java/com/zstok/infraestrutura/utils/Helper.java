package com.zstok.infraestrutura.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static void criarToast(Context context, String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
    public static boolean verificaExpressaoRegularEmail(String email) {

        if (!email.isEmpty()) {
            String excecoes = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
            Pattern pattern = Pattern.compile(excecoes);
            Matcher matcher = pattern.matcher(email);

            return matcher.matches();//se igual a true tem alguma expressão irregular.
        }
        return false;
    }
}
