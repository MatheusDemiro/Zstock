package com.zstok.carrinhoCompra.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoCompra {
    private String idCarrinho;
    private Double totalCompra;
    private List<ItemCompra> arrayItens = new ArrayList<>();

    public String getIdCarrinho() {
        return idCarrinho;
    }
    public void setIdCarrinho(String idCarrinho) {
        this.idCarrinho = idCarrinho;
    }
    public Double getTotalCompra() {
        return totalCompra;
    }
    public void setTotalCompra(Double totalCompra) {
        this.totalCompra = totalCompra;
    }
    public List<ItemCompra> getArrayItens() {
        return arrayItens;
    }
    public void setArrayItens(List<ItemCompra> arrayItens) {
        this.arrayItens = arrayItens;
    }
}
