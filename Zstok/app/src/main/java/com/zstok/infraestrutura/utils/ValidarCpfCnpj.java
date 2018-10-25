package com.zstok.infraestrutura.utils;

public class ValidarCpfCnpj {
    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;

        return soma > 9 ? 0 : soma;
    }
    //Método criado pela equipe
    public static boolean isCpfCnpj(String s){
        return s.length() == 11;
    }
    public static boolean isValidarCPF(String cpf) {
        if (isCpfCnpj(cpf)) {
            Integer digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
            Integer digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);

            return cpf.equals(cpf.substring(0, 9) + digito1.toString() + digito2.toString());
        } else {
            return false;
        }
    }
    public static boolean isValidarCNPJ(String cnpj) {
        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);

        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }
    //By: Carlos Caldas, 30/04/2007
    //Link: https://www.vivaolinux.com.br/script/Codigo-para-validar-CPF-e-CNPJ-otimizado
}
