package com.zstok.pessoaFisica.persistencia;

import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class PessoaFisicaDAO {
    public static boolean inserirPessoaFisica(PessoaFisica pessoaFisica){
        String uidUsuario = FirebaseController.getUidUser();

        FirebaseController.getFirebase().child("pessoaFisica").child(uidUsuario).setValue(pessoaFisica);

        return true;
    }
}
