package com.zstok.produto.negocio;

import com.zstok.produto.dominio.Produto;
import com.zstok.produto.persistencia.ProdutoDAO;

public class ProdutoServices {
    public static boolean insereProduto(Produto produto){
        return ProdutoDAO.insereProduto(produto);
    }
}
