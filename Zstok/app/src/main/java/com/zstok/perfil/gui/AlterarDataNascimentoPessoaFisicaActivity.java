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
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class AlterarDataNascimentoPessoaFisicaActivity extends AppCompatActivity {

    private EditText edtAlterarDataNascimentoPessoaFisica;
    private VerificaConexao verificaConexao;
    private String dataNascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_data_nascimento_pessoa_fisica);

        edtAlterarDataNascimentoPessoaFisica = findViewById(R.id.edtAlterarDataNascimentoPessoFisica);
        Button btnAlterarDataNascimentoPessoaFisica = findViewById(R.id.btnAlterarDataNascimentoPessoaFisica);

        verificaConexao = new VerificaConexao(this);

        //Máscara
        Helper.mascaraDataNascimento(edtAlterarDataNascimentoPessoaFisica);

        btnAlterarDataNascimentoPessoaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampo()){
                        alterarDataNascimento();
                    }
                }
            }
        });
    }
    //Método que valida o campo data de nascimento
    private boolean validarCampo(){
        boolean verificador = true;
        dataNascimento = Helper.removerMascara(edtAlterarDataNascimentoPessoaFisica.getText().toString());

        if (dataNascimento.isEmpty() || dataNascimento.trim().length() == 0){
            edtAlterarDataNascimentoPessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (Integer.valueOf(dataNascimento.substring(0,2)) > 31){
            edtAlterarDataNascimentoPessoaFisica.setError(getString(R.string.zs_excecao_dia_invalido));
            verificador = false;
        }
        if (Integer.valueOf(dataNascimento.substring(2,4)) > 12){
            edtAlterarDataNascimentoPessoaFisica.setError(getString(R.string.zs_excecao_mes_invalido));
            verificador = false;
        }
        return verificador;
    }
    //Método que chama a camada de negócio
    private void alterarDataNascimento(){
        if (PerfilServices.alterarDataNascimento(criarPessoaFisica())){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_data_nascimento_alterado_sucesso));
            abrirTelaPerfilPessoaFisicaActivity();
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Método que cria um objeto da classe pessoa
    private PessoaFisica criarPessoaFisica(){
        PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setDataNascimento(dataNascimento);
        return pessoaFisica;
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
}
