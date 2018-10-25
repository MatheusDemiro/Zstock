package com.zstok.negociacao.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.mensagem.dominio.Mensagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VizualizarChatActivity extends AppCompatActivity {

    private ListView lvMensagens;
    private VerificaConexao verificaConexao;
    private List<HashMap<String, String>> listaMensagem = new ArrayList<>();
    private String idNegociacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizar_chat);

        lvMensagens = findViewById(R.id.lvMensagensVizualizacao);
        verificaConexao = new VerificaConexao(this);
        idNegociacao = getIntent().getStringExtra("idNegociacao");

        carregarMensagens();
    }

    private void carregarMensagens(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                montandoArrayListMensagem(dataSnapshot);
                setListViewMensagens();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void montandoArrayListMensagem(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> referencia = dataSnapshot.child("chat").child(idNegociacao).getChildren();
        listaMensagem.clear();

        for (DataSnapshot dataSnapshotChild: referencia){
            HashMap<String, String> dicionarioMensagem = new HashMap<>();

            Mensagem mensagem = dataSnapshotChild.getValue(Mensagem.class);
            if (mensagem != null) {
                String nome = dataSnapshot.child("pessoa").child(mensagem.getAutor()).child("nome").getValue(String.class);
                String texto = mensagem.getTexto();

                dicionarioMensagem.put("nome", nome);
                dicionarioMensagem.put("mensagem", texto);
                listaMensagem.add(dicionarioMensagem);
            }

        }
    }

    private void setListViewMensagens(){
        SimpleAdapter adapter = new SimpleAdapter(this, listaMensagem,R.layout.modelo_list_view_chat,
                new String[]{"nome","mensagem"},
                new int[]{R.id.txtNomeUsuario,
                        R.id.txtMensagem});
        lvMensagens.setAdapter(adapter);
        lvMensagens.setSelection(lvMensagens.getAdapter().getCount()-1);
    }
}
