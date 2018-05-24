package com.zstok.perfil.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoa.dominio.Pessoa;

public class AlterarEnderecoPessoaActivity extends AppCompatActivity {

    private Spinner spnAlterarEstadoPessoa;
    private EditText edtAlterarRuaPessoa;
    private EditText edtAlterarComplemento;
    private EditText edtAlterarCidade;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_endereco_pessoa);

        Button btnAlterarEndereco = findViewById(R.id.btnAlterarEnderecoPessoa);
        spnAlterarEstadoPessoa = findViewById(R.id.spnAlteraEstadoPerfil);
        edtAlterarRuaPessoa = findViewById(R.id.edtAlterarRuaPessoa);
        edtAlterarComplemento = findViewById(R.id.edtAlterarComplementoPessoa);
        edtAlterarCidade = findViewById(R.id.edtAlterarCidadePessoa);

        verificaConexao = new VerificaConexao(this);

        btnAlterarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (validarCampos()) {
                        String rua = edtAlterarRuaPessoa.getText().toString();
                        String complemento = edtAlterarComplemento.getText().toString();
                        String cidade = edtAlterarCidade.getText().toString();
                        String estado = spnAlterarEstadoPessoa.getSelectedItem().toString();

                        alterarEndereco(criarPessoa(rua, complemento, cidade, estado));
                    }
                }
            }
        });
    }
    //Criando objeto pessoa e formando a string endereço
    private Pessoa criarPessoa(String rua, String complemento, String cidade, String estado){
        Pessoa pessoa = new Pessoa();

        String novoEndereco = rua + ", " + complemento + ". " + cidade + ", " + estado;

        pessoa.setEndereco(novoEndereco);
        return pessoa;
    }
    //Validando os campos obrigatórios da tela de endereço
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtAlterarRuaPessoa.getText().toString().isEmpty() || edtAlterarRuaPessoa.getText().toString().trim().length() == 0){
            edtAlterarRuaPessoa.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtAlterarComplemento.getText().toString().isEmpty() || edtAlterarComplemento.getText().toString().trim().length() == 0){
            edtAlterarComplemento.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtAlterarCidade.getText().toString().isEmpty() || edtAlterarCidade.getText().toString().trim().length() == 0){
            edtAlterarCidade.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (spnAlterarEstadoPessoa.getSelectedItem().equals("Selecione o seu estado")){
            Helper.criarToast(getApplicationContext(), "Escolha seu estado!");
            verificador = false;
        }
        return verificador;
    }
    //Método que chama a camada de negócio
    private void alterarEndereco(Pessoa pessoa){
        if (PerfilServices.alterarEndereco(pessoa)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_endereco_alterado_sucesso));
            abrirTelaPerfilPessoa();
        } else{
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Intent para a tela de perfil pessoa física ou jurídica
    private void abrirTelaPerfilPessoa(){
        FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    abrirTelaPerfilPessoaFisica();
                } else{
                    abrirTelaPerfilPessoaJuridica();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisica(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela de perfil pessoa jurídica
    private void abrirTelaPerfilPessoaJuridica(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
        finish();
    }
}
