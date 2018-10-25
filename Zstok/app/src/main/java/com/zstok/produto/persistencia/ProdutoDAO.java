package com.zstok.produto.persistencia;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.produto.dominio.Produto;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ProdutoDAO {
    //Inserindo produto no banco de dados
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            //Setando o idProduto
            produto.setIdProduto(FirebaseController.getFirebase().child("produto").push().getKey());
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).setValue(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Excluindo produto do banco de dados
    public static boolean excluirProduto(Produto produto){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("produtoExcluido").child(produto.getIdProduto()).setValue(produto);
            FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).setValue(null);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Retorno alterar produto para GUI
    public static boolean alterarProdutoVerificador(Produto produto){
        boolean verificador;

        try {
            alterarProduto(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Alterando produto da árvore de visão do cliente
    private static void alterarProduto(Produto produto) {
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("nome").setValue(produto.getNome());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("nomePesquisa").setValue(produto.getNomePesquisa());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("precoSugerido").setValue(produto.getPrecoSugerido());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque());
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("descricao").setValue(produto.getDescricao());
    }
    //Método provisório
    public static boolean adicionarProdutoCarrinho(ItemCompra itemCompra, DataSnapshot dataSnapshot){
        boolean verificador;

        try {
            //Verificando a existencia do TOTAL
            if(dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()){
                Double totalCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").setValue(totalCarrinho+(itemCompra.getValor()*itemCompra.getQuantidade()));
                //Pesquisa procurando se já existe o item adicionado ao carrinho
                if (!adicionarItemExistente(itemCompra, dataSnapshot)){
                    adicionarNovoItem(itemCompra);
                }
                verificador = true;
            }else{
                FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").setValue(itemCompra.getQuantidade() * itemCompra.getValor());
                //Caso não tenha adicionado
                verificador = adicionarNovoItem(itemCompra);
            }
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Adicionando novo item ao carrinho
    private static boolean adicionarNovoItem(ItemCompra itemCompra) {
        itemCompra.setIdItemCompra(FirebaseController.getFirebase().child("carrinhoCompra").push().getKey());
        FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompra.getIdItemCompra()).setValue(itemCompra);

        return true;
    }
    //Adicionando item já existente no carrinho
    private static boolean adicionarItemExistente(ItemCompra itemCompra, DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> produtosCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
        for(DataSnapshot itemSnapshot: produtosCarrinho) {
            ItemCompra itemCompraPesquisa = itemSnapshot.getValue(ItemCompra.class);
            if (itemCompraPesquisa.getIdProduto().equals(itemCompra.getIdProduto())){
                FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").child(itemCompraPesquisa.getIdItemCompra()).child("quantidade").setValue(itemCompraPesquisa.getQuantidade()+itemCompra.getQuantidade());
                return true;
            }
        }
        return false;
    }
}