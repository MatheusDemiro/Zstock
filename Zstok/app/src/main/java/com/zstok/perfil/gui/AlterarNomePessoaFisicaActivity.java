package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zstok.R;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;

public class AlterarNomePessoaFisicaActivity extends AppCompatActivity {

    private EditText edtAlterarNomePessoaFisica;
    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_nome_pessoa_fisica);

        verificaConexao = new VerificaConexao(this);

        edtAlterarNomePessoaFisica = findViewById(R.id.edtAlterarNomePessoaFisica);
        Button btnAlterarNomePessoaFisica =  findViewById(R.id.btnAlterarNomePessoaFisica);

        btnAlterarNomePessoaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampo()){
                        alterarNome(edtAlterarNomePessoaFisica.getText().toString());
                    }
                }
            }
        });
    }
    private boolean validarCampo(){
        boolean verificador = true;

        if (edtAlterarNomePessoaFisica.getText().toString().isEmpty() ||
                edtAlterarNomePessoaFisica.getText().toString().trim().length() == 0){
            edtAlterarNomePessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    private void alterarNome(String novoNome){
        if (PerfilServices.alterarNome(novoNome)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_nome_alterado_sucesso));
            abrirTelaPerfilPessoaFisicaActivity();
        } else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
}
