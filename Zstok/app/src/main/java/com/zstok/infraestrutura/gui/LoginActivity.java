package com.zstok.infraestrutura.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;
import com.zstok.R;
import com.zstok.ResgatarSenhaActivity;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
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
        TextView tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);

        //Inicializando a instância da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        tvEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRecuperacaoSenha();
            }
        });

        tvRegistreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaConexao.isConected()) {
                    if (validarCampos()) {
                        verificarAutenticacao(edtEmail.getText().toString(), edtSenha.getText().toString());
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.zs_excecao_conexao_falha), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaRecuperacaoSenha() {
        Intent intent = new Intent(getApplicationContext(), ResgatarSenhaActivity.class);
        startActivity(intent);
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

    //Verificando se o usuário está autenticado
    private void verificarAutenticacao(String email, String senha){
        FirebaseController.getFirebaseAuthentication().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
                    if (user != null) {
                        verificarTipoConta(user);
                    } else {
                        Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_usuario_nao_encontrado));
                    }
                } else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_usuario_senha));
                }

            }
        });
    }
    private void verificarTipoConta(FirebaseUser user){
        FirebaseController.getFirebase().child("pessoaFisica").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    abrirTelaPessoaFisica();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //Transição para a tela de registro
    private void abrirTelaRegistro(){
        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
        startActivity(intent);
    }
    private void abrirTelaPessoaFisica(){
        Intent intent = new Intent(getApplicationContext(), MainPessoaFisicaActivity.class);
        startActivity(intent);
    }
}