package com.zstok.produto.negocio;

import android.net.Uri;

import com.zstok.produto.dominio.Produto;
import com.zstok.produto.persistencia.ProdutoDAO;

public class ProdutoServices {
    public static boolean insereProduto(Uri uriFoto,Produto produto){
        return ProdutoDAO.insereProduto(uriFoto,produto);
    }
}
