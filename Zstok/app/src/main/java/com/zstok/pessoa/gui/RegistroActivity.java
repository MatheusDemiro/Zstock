package com.zstok.pessoa.gui;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.zstok.R;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.ValidarCpfCnpj;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoa.negocio.PessoaServices;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaFisica.negocio.PessoaFisicaServices;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;
import com.zstok.pessoaJuridica.negocio.PessoaJuridicaServices;

public class RegistroActivity extends AppCompatActivity {

    private EditText edtRegCpfCnpj;
    private EditText edtRegEmail;
    private EditText edtRegSenha;
    private EditText edtRegConfirmarSenha;
    private EditText edtRegNome;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Instâncias das views
        edtRegNome = findViewById(R.id.edtRegistroNome);
        edtRegCpfCnpj = findViewById(R.id.edtRegistroCpfCnpj);
        edtRegEmail = findViewById(R.id.edtRegistroEmail);
        edtRegSenha = findViewById(R.id.edtRegistroSenha);
        edtRegConfirmarSenha = findViewById(R.id.edtRegistroConfirmarSenha);
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
                if (verificarAutenticacao(task)){
                    if (inserirPessoa()) {
                        inserirCpfCnpj();
                    }
                }
            }
        });
    }
    private void inserirCpfCnpj() {
        if (ValidarCpfCnpj.isCpfCnpj(edtRegCpfCnpj.getText().toString())) {
            inserirPessoaFisica();
        } else {
            inserirPessoaJuridica();
        }
    }
    private boolean inserirPessoa(){
        return PessoaServices.inserirPessoa(criarPessoa());
    }
    private void inserirPessoaJuridica() {
        if (PessoaJuridicaServices.inserirPessoaJuridica(criarPessoaJuridica())){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_sucesso_usuario_cadastrado));
            abrirTelaLoginActivity();
        }
    }
    private void inserirPessoaFisica(){
        if (PessoaFisicaServices.inserirPessoaFisica(criarPessoaFisica())){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_sucesso_usuario_cadastrado));
            abrirTelaLoginActivity();
        }
    }
    private Pessoa criarPessoa(){
        Pessoa pessoa = new Pessoa();

        pessoa.setNome(edtRegNome.getText().toString());

        return pessoa;
    }
    private PessoaJuridica criarPessoaJuridica(){
        PessoaJuridica pessoaJuridica = new PessoaJuridica();

        pessoaJuridica.setCnpj(edtRegCpfCnpj.getText().toString());

        return pessoaJuridica;
    }
    private PessoaFisica criarPessoaFisica(){
        PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setCpf(edtRegCpfCnpj.getText().toString());

        return pessoaFisica;
    }
    //Verificando autenticação
    private boolean verificarAutenticacao(@NonNull Task<AuthResult> task) {
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
            edtRegConfirmarSenha.setError(getString(R.string.zs_excecao_senha_forte));
        }catch (FirebaseAuthInvalidCredentialsException e){
            edtRegEmail.setError(getString(R.string.zs_excecao_email));
        }catch (FirebaseAuthUserCollisionException e){
            edtRegEmail.setError(getString(R.string.zs_excecao_email_cadastrado));
        }catch (Exception e){
            Toast.makeText(RegistroActivity.this, getString(R.string.zs_excecao_database), Toast.LENGTH_LONG).show();
        }
        return verificador;
    }
    //Validando EditTexts
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtRegNome.getText().toString().trim().isEmpty()){
            edtRegNome.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegCpfCnpj.getText().toString().trim().isEmpty()){
            edtRegCpfCnpj.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegCpfCnpj.getText().toString().trim().length() != 11){
            if (edtRegCpfCnpj.getText().toString().trim().length() != 14){
                edtRegCpfCnpj.setError(getString(R.string.zs_excecao_cpf_cnpj));
                verificador = false;
            }
        }
        if (edtRegCpfCnpj.getText().toString().trim().length() == 11){
            if (!ValidarCpfCnpj.isValidarCPF(edtRegCpfCnpj.getText().toString())){
                edtRegCpfCnpj.setError(getString(R.string.zs_excecao_cpf_cnpj));
                verificador = false;
            }
        }
        if (edtRegCpfCnpj.getText().toString().trim().length() == 14){
            if (!ValidarCpfCnpj.isValidarCNPJ(edtRegCpfCnpj.getText().toString())){
                edtRegCpfCnpj.setError(getString(R.string.zs_excecao_cpf_cnpj));
                verificador = false;
            }
        }
        if (edtRegEmail.getText().toString().trim().isEmpty()){
            edtRegEmail.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegSenha.getText().toString().trim().isEmpty()){
            edtRegSenha.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtRegConfirmarSenha.getText().toString().trim().isEmpty()){
            edtRegConfirmarSenha.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (!edtRegSenha.getText().toString().equals(edtRegConfirmarSenha.getText().toString())){
            edtRegSenha.setError(getString(R.string.zs_excecao_senhas_iguais));
            edtRegConfirmarSenha.setError(getString(R.string.zs_excecao_senhas_iguais));
            verificador = false;
        }
        return verificador;
    }
    private void abrirTelaLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
