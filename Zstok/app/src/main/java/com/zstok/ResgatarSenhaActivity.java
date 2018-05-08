package com.zstok;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;

public class ResgatarSenhaActivity extends AppCompatActivity {

    private EditText edtEmailRecuperacaoSenha;

    private VerificaConexao verificaConexao;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacao_senha);

        edtEmailRecuperacaoSenha = (EditText) findViewById(R.id.edtEmailResgatarSenha);
        Button btnEnviarEmail = findViewById(R.id.btnResgatarSenha);

        //Instanciando a classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        btnEnviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (verificarCampo()) {
                        resgatarSenhaViaEmail(edtEmailRecuperacaoSenha.getText().toString());
                    }
                } else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_conexao_falha));
                }
            }
        });
    }
    //Método OnClick do botão btn_resgatar_senha
    private boolean verificarCampo(){
        boolean verificador = true;

        if (edtEmailRecuperacaoSenha.getText().length() == 0){
            edtEmailRecuperacaoSenha.setError(getString(R.string.zs_excecao_conexao_falha));
            verificador = false;
        }
        return verificador;
    }
    //Método para enviar senha ao email que solicitou nova senha.
    private void resgatarSenhaViaEmail(String email){
        //talvez tenha que instanciar
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ResgatarSenhaActivity.this, R.string.zs_email_enviado_sucesso, Toast.LENGTH_SHORT).show();
                    abrirTelaLoginActivity();
                }else {
                    edtEmailRecuperacaoSenha.setError(getString(R.string.zs_excecao_email));
                }
            }
        });
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intentAbrirTelaLoginActivity = new Intent(ResgatarSenhaActivity.this, LoginActivity.class);
        startActivity(intentAbrirTelaLoginActivity);
        finish();
    }
}