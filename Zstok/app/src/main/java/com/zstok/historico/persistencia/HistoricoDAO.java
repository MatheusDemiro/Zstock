package com.zstok.historico.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;

public class HistoricoDAO {
    public static void inserirHistoricoCompra(Historico historico){
        historico.setIdHistorico(FirebaseController.getFirebase().push().getKey());
        FirebaseController.getFirebase().child("historico").child(historico.getIdHistorico()).setValue(historico);
    }

    public static boolean inserirHistoricoNegociacao(Historico historico){
        boolean verificador;

        try{
            historico.setIdHistorico(FirebaseController.getFirebase().push().getKey());
            FirebaseController.getFirebase().child("historico").child(historico.getIdHistorico()).setValue(historico);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
