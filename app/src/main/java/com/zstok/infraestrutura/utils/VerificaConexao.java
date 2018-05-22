package com.zstok.infraestrutura.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class VerificaConexao {

    private Context context;

    public VerificaConexao(Context contextActivity) {
        this.context = contextActivity;
    }

    public boolean isConected(){
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivity != null){
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null){
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }
}
