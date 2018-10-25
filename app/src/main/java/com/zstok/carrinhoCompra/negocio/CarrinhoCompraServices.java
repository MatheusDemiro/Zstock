package com.zstok.carrinhoCompra.negocio;

import com.zstok.carrinhoCompra.persistencia.CarrinhoCompraDAO;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

public class CarrinhoCompraServices {

    public static void reduzirQuantidade(Produto produto){
        CarrinhoCompraDAO.reduzirQuantidade(produto);
    }

    public static boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto){
        return CarrinhoCompraDAO.alterarValorItemCompra(itemCompra, produto);
    }

    public static void inserirTotal(Produto produto, ItemCompra itemCompra, double total){
        CarrinhoCompraDAO.inserirTotal(total, produto, itemCompra);
    }

    public static boolean removerItemCompraCarrinho(ItemCompra itemCompra){
        return CarrinhoCompraDAO.removerItemCompraCarrinho(itemCompra);
    }

    public static void limparCarrinho(){
        CarrinhoCompraDAO.limparCarrinho();
    }

    public static void removerItemInativoCarrinho(String idItemCompra){
        CarrinhoCompraDAO.removerItemInativoCarrinho(idItemCompra);
    }

}
