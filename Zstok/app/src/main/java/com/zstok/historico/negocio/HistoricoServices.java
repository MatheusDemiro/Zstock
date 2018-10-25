package com.zstok.historico.negocio;

import com.zstok.historico.dominio.Historico;
import com.zstok.historico.persistencia.HistoricoDAO;

public class HistoricoServices {
    public static void inserirHistoricoCompra(Historico historico){
        HistoricoDAO.inserirHistoricoCompra(historico);
    }
    public static boolean inserirHistoricoNegociacao(Historico historico){
        return HistoricoDAO.inserirHistoricoNegociacao(historico);
    }
}
