package com.zstok.pessoaFisica.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class PessoaFisicaDAO {
    public static boolean inserirPessoaFisica(PessoaFisica pessoaFisica){
        boolean verificador;

        try {
            String uidUsuario = FirebaseController.getUidUser();
            FirebaseController.getFirebase().child("pessoaFisica").child(uidUsuario).setValue(pessoaFisica);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
