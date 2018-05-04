package com.zstok.usuario.gui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.VerificaConexao;

public class RegistroActivity extends AppCompatActivity {

    private EditText edtRegCpfCnpj;
    private EditText edtRegEmail;
    private EditText edtRegSenha;

    private VerificaConexao verificaConexao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Instâncias das views
        edtRegCpfCnpj = findViewById(R.id.edtRegistroCpfCnpj);
        edtRegEmail = findViewById(R.id.edtRegistroEmail);
        edtRegSenha = findViewById(R.id.edtRegistroSenha);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);

        //Inicializando a instância da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                        cadastrarUsuario();
                    }
                }
            }
        });

    }
    //Autenticando usuário
    private void cadastrarUsuario(){
        final FirebaseAuth autenticacao = FirebaseController.getFirebaseAuthentication();
        autenticacao.createUserWithEmailAndPassword(
                edtRegEmail.getText().toString(),
                edtRegSenha.getText().toString()
        ).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (verificaAutenticacao(task)){
                    //Insere no banco (Negócio -> Persistência)
                }
            }
        });
    }
    //Verificando autenticação
    private boolean verificaAutenticacao(@NonNull Task<AuthResult> task) {
        boolean verificador = true;

        try{
            if (task.isSuccessful()) {
                verificador = true;
            }else {
                verificador = false;
                throw task.getException();
            }
        }catch (FirebaseAuthWeakPasswordException e){
            edtRegSenha.setError(getString(R.string.zs_excecao_senha_forte));
        }catch (FirebaseAuthInvalidCredentialsException e){
            edtRegEmail.setError(getString(R.string.zs_excecao_email));
        }catch (FirebaseAuthUserCollisionException e){
            edtRegEmail.setError(getString(R.string.zs_excecao_email));
        }catch (Exception e){
            Toast.makeText(RegistroActivity.this, getString(R.string.zs_excecao_database), Toast.LENGTH_LONG).show();
        }
        return verificador;
    }
    //Validando EditTexts
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtRegCpfCnpj.getText().toString().trim().isEmpty()){
            edtRegCpfCnpj.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegCpfCnpj.getText().toString().trim().length() > 11 && edtRegCpfCnpj.getText().toString().trim().length() < 14){
            edtRegCpfCnpj.setError(getString(R.string.zs_excecao_cpf_cnpj));
            verificador = false;
        }
        if (edtRegCpfCnpj.getText().toString().trim().length() < 11 && edtRegCpfCnpj.getText().toString().trim().length() > 14){
            edtRegCpfCnpj.setError(getString(R.string.zs_excecao_cpf_cnpj));
            verificador = false;
        }
        if (edtRegEmail.getText().toString().trim().isEmpty()){
            edtRegEmail.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegSenha.getText().toString().trim().isEmpty()){
            edtRegSenha.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegSenha.getText().toString().length() < 6){
            edtRegSenha.setError(getString(R.string.zs_excecao_senha_forte));
            verificador = false;
        }
        return verificador;
    }
}
