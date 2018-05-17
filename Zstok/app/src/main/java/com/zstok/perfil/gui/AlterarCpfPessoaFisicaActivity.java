package com.zstok.perfil.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.ValidarCpfCnpj;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class AlterarCpfPessoaFisicaActivity extends AppCompatActivity {

    private EditText edtCpfAtualAlterarPessoaFisica;
    private EditText edtCpfNovoAlterarPessoaFisica;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_cpf_pessoa_fisica);

        edtCpfAtualAlterarPessoaFisica = findViewById(R.id.edtCpfAtualAlterarPessoaFisica);
        edtCpfNovoAlterarPessoaFisica = findViewById(R.id.edtCpfNovoAlterarPessoaFisica);
        Button btnAlterarCpfPessoaFisica = findViewById(R.id.btnAlterarCpfPessoaFisica);

        verificaConexao = new VerificaConexao(this);

        //Máscara
        Helper.mascaraCpf(edtCpfAtualAlterarPessoaFisica);
        Helper.mascaraCpf(edtCpfNovoAlterarPessoaFisica);

        btnAlterarCpfPessoaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    if (validarCampos()) {
                        alterarCpf();
                    }
                }
            }
        });
    }
    //Método que verifica o cpf e chama o método para inserir cpf no banco
    private void alterarCpf() {
        FirebaseController.getFirebase().child("pessoaFisica").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cpf = dataSnapshot.child(FirebaseController.getUidUser()).child("cpf").getValue(String.class);
                if (!cpf.equals(Helper.removerMascara(edtCpfAtualAlterarPessoaFisica.getText().toString()))){
                    edtCpfAtualAlterarPessoaFisica.setError(getString(R.string.zs_excecao_cpf_nao_cadastrado));
                } else {
                    verificaCpf(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Verificando se o CPF está cadastrado no banco
    private void verificaCpf(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> cpfsCadastrados = dataSnapshot.getChildren();
        boolean temp = true;

        for (DataSnapshot dataSnapshotChild: cpfsCadastrados){
            String cpfCadastrado = dataSnapshotChild.child("cpf").getValue(String.class);
            if (cpfCadastrado.equals(Helper.removerMascara(edtCpfNovoAlterarPessoaFisica.getText().toString()))){
                edtCpfNovoAlterarPessoaFisica.setError(getString(R.string.zs_excecao_cpf_cadastrado_sistema));
                temp = false;
            }
        }if (temp){
            //Inserindo cpf no banco
            insereCpf();
        }
    }
    //Método que chama a camada de negócio
    private void insereCpf(){
        if (PerfilServices.alterarCpf(criarPessoaFisica())){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_cpf_alterado_sucesso));
            abrirTelaPerfilPessoaFisicaActivity();
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Método que cria um objeto da classe pessoa
    private PessoaFisica criarPessoaFisica(){
        PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setCpf(Helper.removerMascara(edtCpfNovoAlterarPessoaFisica.getText().toString()));

        return pessoaFisica;
    }
    private boolean validarCampos() {
        boolean verificador = true;

        String cpfAtual = Helper.removerMascara(edtCpfAtualAlterarPessoaFisica.getText().toString());
        String cpfNovo = Helper.removerMascara(edtCpfNovoAlterarPessoaFisica.getText().toString());

        if (cpfAtual.isEmpty() || cpfAtual.trim().length() == 0) {
            edtCpfAtualAlterarPessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (cpfNovo.isEmpty() || cpfNovo.trim().length() == 0) {
            edtCpfNovoAlterarPessoaFisica.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (!ValidarCpfCnpj.isValidarCPF(cpfAtual)) {
            edtCpfAtualAlterarPessoaFisica.setError(getString(R.string.zs_excecao_cpf));
            verificador = false;
        }
        if (!ValidarCpfCnpj.isValidarCPF(cpfNovo)) {
            edtCpfNovoAlterarPessoaFisica.setError(getString(R.string.zs_excecao_cpf));
            verificador = false;
        }
        return verificador;
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
}
