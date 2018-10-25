package com.zstok.negociacao.dominio;

import com.zstok.itemcompra.dominio.ItemCompra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Negociacao {

    private String idNegociacao;
    private String idPessoaFisica;
    private String idPessoaJuridica;
    private String dataInicio;
    private String dataFim;
    private double total;
    private List<ItemCompra> carrinhoOferta = new ArrayList<>();
    private List<ItemCompra> carrinhoAtual = new ArrayList<>();

    public String getIdNegociacao() {
        return idNegociacao;
    }

    public String getIdPessoaFisica() {
        return idPessoaFisica;
    }

    public void setIdPessoaFisica(String idPessoaFisica) {
        this.idPessoaFisica = idPessoaFisica;
    }

    public String getIdPessoaJuridica() {
        return idPessoaJuridica;
    }

    public void setIdPessoaJuridica(String idPessoaJuridica) {
        this.idPessoaJuridica = idPessoaJuridica;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public void setIdNegociacao(String idNegociacao) {
        this.idNegociacao = idNegociacao;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ItemCompra> getCarrinhoOferta() {
        return carrinhoOferta;
    }

    public void setCarrinhoOferta(List<ItemCompra> carrinhoOferta) {
        this.carrinhoOferta = carrinhoOferta;
    }

    public List<ItemCompra> getCarrinhoAtual() {
        return carrinhoAtual;
    }

    public void setCarrinhoAtual(List<ItemCompra> carrinhoAtual) {
        this.carrinhoAtual = carrinhoAtual;
    }
}
