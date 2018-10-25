package com.zstok.pessoaJuridica.gui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarEmpresaActivity extends AppCompatActivity {

    private String idEmpresa;

    private TextView tvNomeFantasiaEmpresa;
    private TextView tvRazaoSocialEmpresa;
    private TextView tvCnpjEmpresa;
    private TextView tvTelefoneEmpresa;
    private TextView tvEnderecoEmpresa;

    private StorageReference referenciaStorage;

    private CircleImageView cvImagemPerfilEmpresa;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_empresa);

        //Resgatando id da empresa enviado pela intent
        idEmpresa = getIntent().getStringExtra("idEmpresa");

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando views
        tvNomeFantasiaEmpresa = findViewById(R.id.tvNomeFantasiaEmpresa);
        tvRazaoSocialEmpresa = findViewById(R.id.tvRazaoSocialEmpresa);
        tvCnpjEmpresa = findViewById(R.id.tvCnpjEmpresa);
        tvTelefoneEmpresa = findViewById(R.id.tvTelefoneEmpresa);
        tvEnderecoEmpresa = findViewById(R.id.tvEnderecoEmpresa);
        cvImagemPerfilEmpresa = findViewById(R.id.cvEmpresa);

        //Habilitando o scrollbars do TextView (quando necessário o scroll irá aparecer)
        habilitarScrollBars();

        //Instanciando referência do storage
        referenciaStorage = FirebaseStorage.getInstance().getReference();

        //Resgatando foto do perfil da empresa
        recuperarFoto();

        //Aplicando máscaras aos campos de cnpj e telefone
        aplicarMascaras();

        //Recuperando dados do firebase
        recuperarDados();
    }
    //Método que habilita o scrollbars do TextView endereço
    private void habilitarScrollBars() {
        tvEnderecoEmpresa.setMaxLines(Integer.MAX_VALUE);
        tvEnderecoEmpresa.setMovementMethod(new ScrollingMovementMethod());
    }
    //Resgatando foto do Storage
    private void recuperarFoto(){
        iniciarProgressDialog();
        StorageReference ref = referenciaStorage.child("images/perfil/" + idEmpresa + ".bmp");

        try {
            final File localFile = File.createTempFile("images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap minhaFoto = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    cvImagemPerfilEmpresa.setImageBitmap(minhaFoto);
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Log.d("IOException downlaod", e.getMessage());
        }
    }
    private void recuperarDados(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PessoaJuridica pessoaJuridica = dataSnapshot.child("pessoaJuridica").child(idEmpresa).getValue(PessoaJuridica.class);
                Pessoa pessoa = dataSnapshot.child("pessoa").child(idEmpresa).getValue(Pessoa.class);
                if (pessoaJuridica != null && pessoa != null) {
                    setarCampos(pessoaJuridica, pessoa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_perfil));
        progressDialog.show();
    }
    //Aplicando máscaras nos campos cnpjs e telefone
    private void aplicarMascaras(){
        Helper.mascaraCnpj(tvCnpjEmpresa);
        Helper.mascaraTelefone(tvTelefoneEmpresa);
    }
    //Setando campos da activity
    private void setarCampos(PessoaJuridica pessoaJuridica, Pessoa pessoa) {
        tvNomeFantasiaEmpresa.setText(pessoa.getNome());
        tvTelefoneEmpresa.setText(pessoa.getTelefone());
        tvEnderecoEmpresa.setText(pessoa.getEndereco());
        tvRazaoSocialEmpresa.setText(pessoaJuridica.getRazaoSocial());
        tvCnpjEmpresa.setText(pessoaJuridica.getCnpj());
    }
}
