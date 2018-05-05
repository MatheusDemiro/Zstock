package com.zstok.pessoa.persistencia;

import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.pessoa.dominio.Pessoa;

public class PessoaDAO {
    public static boolean inserirPessoa(Pessoa pessoa){

        String uidUsuario = FirebaseController.getUidUsuario();

        FirebaseController.getFirebase().child("pessoa").child(uidUsuario).setValue(pessoa);

        return true;
    }
}
