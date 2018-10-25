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
import com.zstok.infraestrutura.utils.ValidarCpfCnpj;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class AlterarCnpjPessoaJuridicaActivity extends AppCompatActivity {

    private VerificaConexao verificaConexao;
    private EditText edtCnpjAtualAlterarPessoaJuridica;
    private EditText edtCnpjNovoAlterarPessoaJuridica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_cnpj_pessoa_juridica);

        edtCnpjAtualAlterarPessoaJuridica = findViewById(R.id.edtCnpjAtualAlterarPessoaJuridica);
        edtCnpjNovoAlterarPessoaJuridica = findViewById(R.id.edtCnpjNovoAlterarPessoaJuridica);
        Button btnAlterarCnpjPessoaJuridica = findViewById(R.id.btnAlterarCnpjPessoaJuridica);

        btnAlterarCnpjPessoaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaConexao.isConected()){
                    if(validarCampos()){
                        alterarCnpj();
                    }
                }
            }
        });


        verificaConexao = new VerificaConexao(this);


    }

    private void alterarCnpj() {
        FirebaseController.getFirebase().child("pessoaJuridica").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cnpj = dataSnapshot.child(FirebaseController.getUidUser()).child("cnpj").getValue(String.class);
                if (!cnpj.equals(edtCnpjAtualAlterarPessoaJuridica.getText().toString())){
                    edtCnpjAtualAlterarPessoaJuridica.setError("Cnpj não cadastrado !");
                } else {
                    verificaCnpj(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void verificaCnpj(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> cnpjsCadastrados = dataSnapshot.getChildren();
        boolean temp = true;

        for (DataSnapshot dataSnapshotChild: cnpjsCadastrados){
            String cnpjCadastrado = dataSnapshotChild.child("cnpj").getValue(String.class);
            if (cnpjCadastrado.equals(edtCnpjNovoAlterarPessoaJuridica.getText().toString())){
                edtCnpjNovoAlterarPessoaJuridica.setError("Cnpj cadastrado no sitema !");
                temp = false;
            }
        }if (temp){
            //Inserindo cnpj no banco
            insereCnpj();
        }
    }

    private void insereCnpj(){
        if (PerfilServices.alterarCnpj(criarPessoaJuridica())){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_cpf_alterado_sucesso));
            abrirTelaPerfilPessoaJuridicaActivity();
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    private PessoaJuridica criarPessoaJuridica(){
        PessoaJuridica pessoaJuridica = new PessoaJuridica();

        pessoaJuridica.setCnpj(edtCnpjNovoAlterarPessoaJuridica.getText().toString());

        return pessoaJuridica;
    }

    private void abrirTelaPerfilPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validarCampos() {
        boolean verificador = true;

        String cnpjAtual = Helper.removerMascara(edtCnpjAtualAlterarPessoaJuridica.getText().toString());
        String cnpjNovo = Helper.removerMascara(edtCnpjNovoAlterarPessoaJuridica.getText().toString());

        if (cnpjAtual.isEmpty() || cnpjAtual.trim().length() == 0) {
            edtCnpjAtualAlterarPessoaJuridica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (cnpjNovo.isEmpty() || cnpjNovo.trim().length() == 0) {
            edtCnpjNovoAlterarPessoaJuridica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (!ValidarCpfCnpj.isValidarCNPJ(cnpjAtual)) {
            edtCnpjAtualAlterarPessoaJuridica.setError(getString(R.string.zs_excecao_cnpj));
            verificador = false;
        }
        if (!ValidarCpfCnpj.isValidarCNPJ(cnpjNovo)) {
            edtCnpjNovoAlterarPessoaJuridica.setError(getString(R.string.zs_excecao_cnpj));
            verificador = false;
        }
        return verificador;
    }
}
