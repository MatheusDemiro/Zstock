package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoa.dominio.Pessoa;

public class AlterarTelefonePessoaActivity extends AppCompatActivity {

    private EditText edtAlterarTelefonePessoa;
    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_telefone_pessoa);

        verificaConexao = new VerificaConexao(this);

        edtAlterarTelefonePessoa = findViewById(R.id.edtAlterarTelefonePessoa);
        Button btnAlterarTelefonePessoa = findViewById(R.id.btnAlterarTelefonePessoa);

        //Máscara
        Helper.mascaraTelefone(edtAlterarTelefonePessoa);

        btnAlterarTelefonePessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (validarCampo()) {
                        alterarTelefone(criarPessoa());
                    }
                }
            }
        });
    }
    //Validando o EditText do telefone
    private boolean validarCampo(){
        boolean verificador = true;

        if (edtAlterarTelefonePessoa.getText().toString().isEmpty() ||
                edtAlterarTelefonePessoa.getText().toString().trim().length() == 0){
            edtAlterarTelefonePessoa.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Criando objeto pessoa
    private Pessoa criarPessoa(){
        Pessoa pessoa = new Pessoa();

        pessoa.setTelefone(verificandoTipoTelefone(Helper.removerMascara(edtAlterarTelefonePessoa.getText().toString())));

        return pessoa;
    }
    //Verificando o tipo de telefone
    private String verificandoTipoTelefone(String telefone) {
        int primeiroDigito = Integer.valueOf(telefone.substring(2,3));
        if (!(primeiroDigito >= 2 && primeiroDigito <= 5)){
            return formatarTelefone(telefone);
        }else {
            return telefone;
        }
    }
    //Acrescentando o nove
    private String formatarTelefone(String telefone){
        return telefone.substring(0,2) + "9" + telefone.substring(2);
    }
    //Chamando camada de negócio para alterar o telefone
    private void alterarTelefone(Pessoa pessoa){
        if (PerfilServices.alterarTelefone(pessoa)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_telefone_alterado_sucesso));
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
