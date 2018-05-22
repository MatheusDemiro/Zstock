package com.zstok.produto.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {
    public static boolean insereProduto(Produto produto){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).push().setValue(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
