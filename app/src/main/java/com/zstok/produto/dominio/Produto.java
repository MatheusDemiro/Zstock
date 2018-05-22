package com.zstok.produto.dominio;

import android.graphics.Bitmap;

public class Produto {
    private String idProduto;
    private int quantidadeEstoque;
    private String nome;
    private String descricao;
    private Bitmap imagemProduto;
    private double preco;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public double getPreco() {
        return preco;
    }
    public void setPreco(double preco) {
        this.preco = preco;
    }
    public String getIdProduto(){
        return idProduto;
    }
    public void setIdProduto(String idProduto){
        this.idProduto = idProduto;
    }
    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    public Bitmap getImagemProduto() {
        return imagemProduto;
    }
    public void setImagemProduto(Bitmap imagemProduto) {
        this.imagemProduto = imagemProduto;
    }
}