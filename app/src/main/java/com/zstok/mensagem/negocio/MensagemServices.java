package com.zstok.mensagem.negocio;

import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.mensagem.MensagemException;
import com.zstok.mensagem.dominio.Mensagem;
import com.zstok.mensagem.persistencia.MensagemDAO;

public class MensagemServices {

    public static void enviarMensagem(Mensagem mensagem, String idNegociacao){
        MensagemDAO.enviarMensagem(mensagem, idNegociacao);
    }
}
