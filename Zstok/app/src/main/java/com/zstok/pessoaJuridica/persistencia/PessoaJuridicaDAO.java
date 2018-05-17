package com.zstok.pessoaJuridica.persistencia;

import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class PessoaJuridicaDAO {
    public static boolean inserirPessoaJuridica(PessoaJuridica pessoaJuridica){
        String uidUsuario = FirebaseController.getUidUser();

        FirebaseController.getFirebase().child("pessoaJuridica").child(uidUsuario).setValue(pessoaJuridica);

        return true;
    }
}
