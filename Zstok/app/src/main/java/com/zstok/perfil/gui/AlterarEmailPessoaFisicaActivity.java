package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.zstok.R;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;

public class AlterarEmailPessoaFisicaActivity extends AppCompatActivity {

    private EditText edtAlterarEmailPessoaFisica;
    private EditText edtSenhaPessoaFisica;
    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_email_pessoa_fisica);

        verificaConexao = new VerificaConexao(this);

        edtAlterarEmailPessoaFisica = findViewById(R.id.edtAlterarEmailPessoaFisica);
        edtSenhaPessoaFisica = findViewById(R.id.edtSenhaPessoaFisica);
        Button btnAlterarEmailPessoaFisica = findViewById(R.id.btnAlterarEmailPessoaFisica);

        btnAlterarEmailPessoaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                       validarEmailSenha();
                    }
                }
            }
        });
    }
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtAlterarEmailPessoaFisica.getText().toString().isEmpty() ||
                edtAlterarEmailPessoaFisica.getText().toString().trim().length() == 0){
            edtAlterarEmailPessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtSenhaPessoaFisica.getText().toString().isEmpty() ||
                edtSenhaPessoaFisica.getText().toString().trim().length() == 0){
            edtSenhaPessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (!Helper.verificaExpressaoRegularEmail(edtAlterarEmailPessoaFisica.getText().toString())){
            edtAlterarEmailPessoaFisica.setError(getString(R.string.zs_excecao_email));
            verificador = false;
        }
        return verificador;
    }
    //Validando email e senha
    private void validarEmailSenha(){
        FirebaseController.getFirebaseAuthentication().signInWithEmailAndPassword(FirebaseController.getFirebaseAuthentication()
                .getCurrentUser().getEmail(), edtSenhaPessoaFisica.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    alterarEmail(edtAlterarEmailPessoaFisica.getText().toString(), edtSenhaPessoaFisica.getText().toString());
                } else {
                    edtSenhaPessoaFisica.setError(getString(R.string.zs_excecao_senha));
                }
            }
        });
    }
    private void alterarEmail(String novoEmail, String senha){
            if (PerfilServices.alterarEmail(novoEmail, senha)) {
                Helper.criarToast(getApplicationContext(), getString(R.string.zs_email_alterado_sucesso));
                encerrarSessaoUsuario();
            } else {
                Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
            }
    }
    //SignOut
    private void encerrarSessaoUsuario() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
        finish();
    }
}
