package com.zstok.infraestrutura.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zstok.R;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.pessoa.gui.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instanciando views
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        TextView tvRegistreSe = findViewById(R.id.tvRegistreSe);
        Button btnEntrar = findViewById(R.id.btnEntrar);

        //Inicializando a instância da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        tvRegistreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (validarCampos()) {
                        //Sign in Usuário
                        //Fazer consulta na árvore e verificar a validez do email
                    }
                }
            }
        });
    }
    //Validando views
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtEmail.getText().toString().trim().isEmpty()){
            edtEmail.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtSenha.getText().toString().trim().isEmpty()){
            edtSenha.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Transição para a tela de registro
    private void abrirTelaRegistro(){
        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
        startActivity(intent);
    }
}