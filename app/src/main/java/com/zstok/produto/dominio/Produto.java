package com.zstok.produto.dominio;

public class Produto {
    private String idProduto;
    private String idEmpresa;
    private String nome;
    private String nomePesquisa;
    private String descricao;
    private String urlImagem;
    private int quantidadeEstoque;
    private double precoSugerido;

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
    public double getPrecoSugerido() {
        return precoSugerido;
    }
    public void setPrecoSugerido(double precoSugerido) {
        this.precoSugerido = precoSugerido;
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
    public String getNomePesquisa() {
        return nomePesquisa;
    }
    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    public String getUrlImagem() {
        return urlImagem;
    }
    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
    public String getIdEmpresa() {
        return idEmpresa;
    }
    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}