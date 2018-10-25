package com.zstok.produto.negocio;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.persistencia.ProdutoDAO;

public class ProdutoServices {
    public static boolean insereProduto(Produto produto){
        return ProdutoDAO.insereProduto(produto);
    }
    public static boolean excluirProduto(Produto produto){
        return ProdutoDAO.excluirProduto(produto);
    }
    public static boolean alterarProduto(Produto produto){
        return ProdutoDAO.alterarProdutoVerificador(produto);
    }
    //Método provisório
    public static boolean adicionarProdutoCarrinho(ItemCompra itemCompra, DataSnapshot dataSnapshot){
        return ProdutoDAO.adicionarProdutoCarrinho(itemCompra, dataSnapshot);
    }

}