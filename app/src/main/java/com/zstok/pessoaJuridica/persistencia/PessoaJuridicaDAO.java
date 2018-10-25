package com.zstok.pessoaJuridica.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class PessoaJuridicaDAO {

    public static boolean inserirPessoaJuridica(PessoaJuridica pessoaJuridica){
        boolean verificador;

        try {
            String uidUsuario = FirebaseController.getUidUser();
            FirebaseController.getFirebase().child("pessoaJuridica").child(uidUsuario).setValue(pessoaJuridica);
            verificador = true;
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
