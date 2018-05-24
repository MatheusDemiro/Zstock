package com.zstok.produto.dominio;

public class Produto {
    private String idProduto;
    private int quantidadeEstoque;
    private String nomeProduto;
    private String descricao;
    private String urlImagemProduto;
    private double preco;

    public String getNomeProduto() {
        return nomeProduto;
    }
    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
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
    public String getUrlImagemProduto() {
        return urlImagemProduto;
    }
    public void setUrlImagemProduto(String urlImagemProduto) {
        this.urlImagemProduto = urlImagemProduto;
    }
}