package com.zstok.mensagem.persistencia;

import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.mensagem.dominio.Mensagem;

public class MensagemDAO {

    public static void enviarMensagem(Mensagem mensagem, String idNegociacao){
        FirebaseController.getFirebase().child("chat").child(idNegociacao).push().setValue(mensagem);
    }
}
