package com.zstok.pessoa.persistencia;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.pessoa.dominio.Pessoa;

public class PessoaDAO {

    public static boolean inserirPessoa(Pessoa pessoa){
        boolean verificador = true;

        FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
        try {
            if (user != null) {
                FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).setValue(pessoa);
            }
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
