package com.zstok.negociacao.negocio;

import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.persistencia.NegociacaoDAO;
import com.zstok.produto.dominio.Produto;

public class NegociacaoServices {

    public static void inserirNegociacao(Negociacao negociacao){
        NegociacaoDAO.inserirNegociacao(negociacao);
    }

    public static void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        NegociacaoDAO.diminuirQuantidade(produto, itemCompra);
    }

    public static boolean inserirTotal(String idNegociacao, double novoTotal){
        return NegociacaoDAO.inserirTotal(idNegociacao, novoTotal);
    }

    public static boolean alterarItemCarrinho(Produto produto, String idNegociacao, String chave){
        return NegociacaoDAO.alterarItemCarrinho(produto, idNegociacao, chave);
    }

    public static void removerItemInativoCarrinho(String idNegociacao, String chave){
        NegociacaoDAO.removerItemInativoCarrinho(idNegociacao, chave);
    }

    public static boolean limparNegociacao(String idNegociacao){
        return NegociacaoDAO.limparNegociacao(idNegociacao);
    }
}
