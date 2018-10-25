package com.zstok.negociacao.gui;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.mensagem.dominio.Mensagem;
import com.zstok.mensagem.negocio.MensagemServices;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.produto.gui.VisualizarProdutoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatNegociacaoActivity extends AppCompatActivity {

    private TextView tvNegociacaoNome;
    private EditText edtNegociacaoBarraMensagem;
    private ListView lvMensagens;
    private String idNegociacao;
    private Button btnNegociacaoEnviarMensagem;

    private VerificaConexao verificaConexao;
    private List<HashMap<String, String>> listaMensagem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_negocicao);

        //Recuperando Views
        Button btnNegociacaoCarrinho = findViewById(R.id.btnNegociacaoCarrinho);
        btnNegociacaoEnviarMensagem = findViewById(R.id.btnNegociacaoEnviarMensagem);
        tvNegociacaoNome = findViewById(R.id.tvNegociacaoNome);
        edtNegociacaoBarraMensagem = findViewById(R.id.edtNegociacaoBarraMensagem);
        lvMensagens = findViewById(R.id.lvMensagens);
        verificaConexao = new VerificaConexao(this);

        //Carregando nome
        carregarNome();
        carregarMensagens();


        //Verificando status da negociação
        verificarNegociacao();

        //Recuperando ID da negociação
        idNegociacao = getIntent().getStringExtra("idNegociacao");

        btnNegociacaoCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCarrinhoNegociacaoActivity();
            }
        });

        btnNegociacaoEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaConexao.isConected()){
                    verificaMensagem();
                }else{
                    Helper.criarToast(getApplicationContext(),getString(R.string.zs_excecao_carrinho_vazio));
                }
            }
        });

        FirebaseController.getFirebase().child("chat").child(idNegociacao).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                carregarMensagens();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                carregarMensagens();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void abrirTelaCarrinhoNegociacaoActivity() {
        Intent intent = new Intent(getApplicationContext(), CarrinhoNegociacaoActivity.class);
        intent.putExtra("idNegociacao", idNegociacao);
        startActivity(intent);
    }
    //Bloquenado views caso a negociação ja tenha sido fechada
    private void verificarNegociacao(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("negociacao").child(idNegociacao).child("dataFim").exists()){
                    edtNegociacaoBarraMensagem.setEnabled(false);
                    btnNegociacaoEnviarMensagem.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Carregando nome
    private void carregarNome(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("negociacao").child(idNegociacao).child("idPessoaFisica").getValue(String.class).equals(FirebaseController.getUidUser())){
                    String idPessoa = dataSnapshot.child("negociacao").child(idNegociacao).child("idPessoaJuridica").getValue(String.class);
                    tvNegociacaoNome.setText(dataSnapshot.child("pessoa").child(idPessoa).child("nome").getValue(String.class));
                }else{
                    String idPessoa = (dataSnapshot.child("negociacao").child(idNegociacao).child("idPessoaFisica").getValue(String.class));
                    tvNegociacaoNome.setText(dataSnapshot.child("pessoa").child(idPessoa).child("nome").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verificaMensagem(){
        if (verificaMensagemVazia()){
            MensagemServices.enviarMensagem(criarMensagem(), idNegociacao);
            edtNegociacaoBarraMensagem.setText("");
        }
        else{
            Helper.criarToast(getApplicationContext(),"Mensagem vazia");
        }
    }

    private boolean verificaMensagemVazia(){
        boolean verificador = true;

        if (edtNegociacaoBarraMensagem.getText().toString().isEmpty()){
            verificador = false;
        }
        if (edtNegociacaoBarraMensagem.getText().toString().trim().length() == 0){
            verificador = false;
        }
        return verificador;
    }

    private Mensagem criarMensagem(){
        Mensagem mensagem = new Mensagem();
        mensagem.setAutor(FirebaseController.getUidUser());
        mensagem.setTexto(edtNegociacaoBarraMensagem.getText().toString());
        return mensagem;
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
