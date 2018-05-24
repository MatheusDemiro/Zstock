package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;

public class AlterarNomePessoaActivity extends AppCompatActivity {

    private EditText edtAlterarNomePessoa;
    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_nome_pessoa);
        verificaConexao = new VerificaConexao(this);

        //Esconder ActionBar
        edtAlterarNomePessoa = findViewById(R.id.edtAlterarNomePessoa);
        Button btnAlterarNomePessoa =  findViewById(R.id.btnAlterarNomePessoa);

        btnAlterarNomePessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampo()){
                        alterarNome(edtAlterarNomePessoa.getText().toString());
                    }
                }
            }
        });
    }
    private boolean validarCampo(){
        boolean verificador = true;

        if (edtAlterarNomePessoa.getText().toString().isEmpty() ||
                edtAlterarNomePessoa.getText().toString().trim().length() == 0){
            edtAlterarNomePessoa.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    private void alterarNome(String novoNome){
        if (PerfilServices.alterarNome(novoNome)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_nome_alterado_sucesso));
            abrirTelaPerfilPessoa();
        } else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Intent para a tela de perfil pessoa física ou jurídica
    private void abrirTelaPerfilPessoa(){
        FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    abrirTelaPerfilPessoaFisica();
                } else{
                    abrirTelaPerfilPessoaJuridica();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisica(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela de perfil pessoa jurídica
    private void abrirTelaPerfilPessoaJuridica(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
        finish();
    }
}
