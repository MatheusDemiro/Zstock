package com.zstok.carrinhoCompra.persistencia;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

import java.io.FileInputStream;

import static com.zstok.infraestrutura.utils.FirebaseController.getFirebase;

public class CarrinhoCompraDAO {

    public static void reduzirQuantidade(Produto produto){
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
    }

    public static boolean alterarValorItemCompra(ItemCompra itemCompra, Produto produto){
        boolean verificador;

        try{
            FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompra.getIdItemCompra()).child("valor").setValue(produto.getPrecoSugerido());
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static void inserirTotal(double novoTotal, Produto produto, ItemCompra itemCompra){
        if (produto != null){
            FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompra.getIdItemCompra()).child("valor").setValue(produto.getPrecoSugerido());
        }
        FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").setValue(novoTotal);
    }

    public static boolean removerItemCompraCarrinho(ItemCompra itemCompra){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompra.getIdItemCompra()).setValue(null);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static void limparCarrinho(){
        FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).setValue(null);
    }

    public static void removerItemInativoCarrinho(String idItemCompra){
        FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(idItemCompra).setValue(null);
    }
}
