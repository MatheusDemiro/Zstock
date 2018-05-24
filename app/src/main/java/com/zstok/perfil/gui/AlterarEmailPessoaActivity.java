package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.zstok.R;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoa.gui.RegistroActivity;

public class AlterarEmailPessoaActivity extends AppCompatActivity {

    private EditText edtAlterarEmailPessoa;
    private EditText edtSenhaPessoa;
    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_email_pessoa);

        verificaConexao = new VerificaConexao(this);

        edtAlterarEmailPessoa = findViewById(R.id.edtAlterarEmailPessoa);
        edtSenhaPessoa = findViewById(R.id.edtSenhaPessoa);
        Button btnAlterarEmailPessoa = findViewById(R.id.btnAlterarEmailPessoa);

        btnAlterarEmailPessoa.setOnClickListener(new View.OnClickListener() {
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

        if (edtAlterarEmailPessoa.getText().toString().isEmpty() ||
                edtAlterarEmailPessoa.getText().toString().trim().length() == 0){
            edtAlterarEmailPessoa.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtSenhaPessoa.getText().toString().isEmpty() ||
                edtSenhaPessoa.getText().toString().trim().length() == 0){
            edtSenhaPessoa.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (!Helper.verificaExpressaoRegularEmail(edtAlterarEmailPessoa.getText().toString())){
            edtAlterarEmailPessoa.setError(getString(R.string.zs_excecao_email));
            verificador = false;
        }
        return verificador;
    }
    //Validando email e senha
    private void validarEmailSenha(){
        FirebaseController.getFirebaseAuthentication().signInWithEmailAndPassword(FirebaseController.getFirebaseAuthentication()
                .getCurrentUser().getEmail(), edtSenhaPessoa.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    alterarEmail(edtAlterarEmailPessoa.getText().toString());
                } else {
                    edtSenhaPessoa.setError(getString(R.string.zs_excecao_senha));
                }
            }
        });
    }
    private void alterarEmail(String novoEmail){
            if (PerfilServices.alterarEmail(novoEmail)) {
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