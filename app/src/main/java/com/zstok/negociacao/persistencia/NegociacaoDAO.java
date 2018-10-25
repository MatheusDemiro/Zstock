package com.zstok.negociacao.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.produto.dominio.Produto;

public class NegociacaoDAO {

    public static void inserirNegociacao(Negociacao negociacao){
        negociacao.setIdNegociacao(FirebaseController.getFirebase().push().getKey());
        FirebaseController.getFirebase().child("negociacao").child(negociacao.getIdNegociacao()).setValue(negociacao);
    }

    public static void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("quantidadeEstoque").setValue(produto.getQuantidadeEstoque() - itemCompra.getQuantidade());
    }

    public static boolean inserirTotal(String idNegociacao, double novoTotal){
        boolean verificador;

        try{
            FirebaseController.getFirebase().child("negociacao").child(idNegociacao).child("total").setValue(novoTotal);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static boolean alterarItemCarrinho(Produto produto, String idNegociacao, String chave){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("negociacao").child(idNegociacao).child("carrinhoAtual").child(chave).child("valor").setValue(produto.getPrecoSugerido());
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

    public static void removerItemInativoCarrinho(String idNegociacao, String chave){
        FirebaseController.getFirebase().child("negociacao").child(idNegociacao).child("carrinhoAtual").child(chave).setValue(null);
    }

    public static boolean limparNegociacao(String idNegociacao){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("negociacao").child(idNegociacao).setValue(null);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
