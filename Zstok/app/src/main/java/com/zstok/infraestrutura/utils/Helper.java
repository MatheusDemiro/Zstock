package com.zstok.infraestrutura.utils;

import android.content.Context;
import android.widget.Toast;

public class Helper {
    public static void criarToast(Context context, String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
}
