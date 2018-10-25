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
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class AlterarRazaoSocialPessoaJuridicaActivity extends AppCompatActivity {

    private VerificaConexao verificaConexao;
    private EditText edtAlterarRazaoSocial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_razao_social_pessoa_juridica);

        edtAlterarRazaoSocial = findViewById(R.id.edtAlterarRazaoSocial);
        Button btnAlterarRazaoSocial = findViewById(R.id.btnAlterarRazaoSocial);
        verificaConexao = new VerificaConexao(this);

        btnAlterarRazaoSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaConexao.isConected()){
                    if(validarCampo()){
                        alterarRazaoSocial(criarPessoaJuridica());
                    }
                }

            }
        });


    }
    //Criando objeto pessoa
    private PessoaJuridica criarPessoaJuridica(){
        PessoaJuridica pessoaJuridica = new PessoaJuridica();

        pessoaJuridica.setRazaoSocial(edtAlterarRazaoSocial.getText().toString());

        return pessoaJuridica;
    }
    //Alterando razão social da empresa
    private void alterarRazaoSocial(PessoaJuridica pessoaJuridica) {
        if (PerfilServices.alterarRazaoSocial(pessoaJuridica)){
            Helper.criarToast(getApplicationContext(), "Razão Social alterada com Sucesso !");
            abrirTelaPerfilPessoaJuridica();
        } else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }

    private void abrirTelaPerfilPessoaJuridica() {
        Intent intent = new Intent(getApplicationContext(),PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
    }

    private boolean validarCampo(){
        boolean verificador = true;

        if (edtAlterarRazaoSocial.getText().toString().isEmpty() ||
                edtAlterarRazaoSocial.getText().toString().trim().length() == 0){
            edtAlterarRazaoSocial.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
}
