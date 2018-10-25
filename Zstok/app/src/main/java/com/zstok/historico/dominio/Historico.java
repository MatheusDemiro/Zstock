package com.zstok.historico.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;

public class Historico {

    private String idHistorico;
    private String idNegociacao;
    private String idPessoaJuridica;
    private String idPessoaFisica;
    private String dataInicio;
    private String dataFim;
    private double total;
    private ArrayList<ItemCompra> carrinho;

    public String getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(String idHistorico) {
        this.idHistorico = idHistorico;
    }

    public String getIdPessoaJuridica() {
        return idPessoaJuridica;
    }

    public void setIdPessoaJuridica(String idPessoaJuridica) {
        this.idPessoaJuridica = idPessoaJuridica;
    }

    public String getIdPessoaFisica() {
        return idPessoaFisica;
    }

    public void setIdPessoaFisica(String idPessoaFisica) {
        this.idPessoaFisica = idPessoaFisica;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public ArrayList<ItemCompra> getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(ArrayList<ItemCompra> carrinho) {
        this.carrinho = carrinho;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getIdNegociacao() {
        return idNegociacao;
    }

    public void setIdNegociacao(String idNegociacao) {
        this.idNegociacao = idNegociacao;
    }
}