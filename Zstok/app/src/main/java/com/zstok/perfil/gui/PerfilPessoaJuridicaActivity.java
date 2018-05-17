package com.zstok.perfil.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

import java.io.File;
import java.io.IOException;

public class PerfilPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference storageReference;
    private Uri uriphoto;

    private ImageView imgPerfilPessoaJuridica;
    private TextView tvNomeFantasiaPerfilJuridico;
    private TextView tvRazaoSocialPerfilJuridico;
    private TextView tvEmailPerfilJuridico;
    private TextView tvCnpjPerfilJuridico;
    private TextView tvTelefonePerfilJuridico;
    private TextView tvEnderecoPerfilJuridico;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //InstanciandoAsViews
        Button btnAlterarImagemPerfil = findViewById(R.id.btnAlterarImagemPerfilPessoaJuridica);
        imgPerfilPessoaJuridica = findViewById(R.id.imgPerfilPessoaJuridica);
        tvNomeFantasiaPerfilJuridico = findViewById(R.id.tvNomeFantasiaPerfilJuridico);
        tvRazaoSocialPerfilJuridico = findViewById(R.id.tvRazaoSocialPerfilJuridico);
        tvEmailPerfilJuridico = findViewById(R.id.tvEmailPerfilJuridico);
        tvCnpjPerfilJuridico = findViewById(R.id.tvCnpjPerfilJuridico);
        tvTelefonePerfilJuridico = findViewById(R.id.tvTelefonePerfilJuridico);
        tvEnderecoPerfilJuridico = findViewById(R.id.tvEnderecoPerfilJuridico);

        //Referencia storage
        storageReference = FirebaseStorage.getInstance().getReference();

        setDadosMenuLateral();
        recuperarDados();
        permissaoGravarLerArquivos();
        carregandoFoto();

        btnAlterarImagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissaoAcessarCamera();
            }
        });

        tvNomeFantasiaPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA DE ALTERAR NOME
            }
        });

        tvRazaoSocialPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA DE ALTERAR RAZAO SOCIAL
            }
        });

        tvEmailPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA DE ALTERAR EMAIL *** MESMA QUE PERFIL FISICO
            }
        });

        tvCnpjPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA PARA ALTERAR CPNJ *** LEMBRAR DE VALIDAR O MESMO
            }
        });

        tvTelefonePerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA PARA ALTERAR TELEFONE *** MESMA QUE PERFIL FISICO
            }
        });

        tvEnderecoPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TELA PARA ALTERAR ENDEREÇO *** MESMA QUE PERFIL FISICO
            }
        });
    }

    private void recuperarDados(){
        FirebaseController.getFirebase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pessoa pessoa = dataSnapshot.child("pessoa").child(FirebaseController.getUidUser()).getValue(Pessoa.class);
                PessoaJuridica pessoaJuridica = dataSnapshot.child("pessoaJuridica").child(FirebaseController.getUidUser()).getValue(PessoaJuridica.class);

                if (pessoa != null && pessoaJuridica != null){
                    setInformacoesPerfil(pessoa, pessoaJuridica);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setInformacoesPerfil(Pessoa pessoa, PessoaJuridica pessoaJuridica){
        tvNomeFantasiaPerfilJuridico.setText(pessoaJuridica.getNomeFantasia());
        tvTelefonePerfilJuridico.setText(pessoa.getTelefone());
        tvCnpjPerfilJuridico.setText(pessoaJuridica.getCnpj());
        tvEmailPerfilJuridico.setText(FirebaseController.getFirebaseAuthentication().getCurrentUser().getEmail());
    }
    private void setDadosMenuLateral(){
        PerfilServices.setNomeEmailView(navigationView, FirebaseController.getFirebaseAuthentication().getCurrentUser());
    }

    private void permissaoGravarLerArquivos(){
        //Trecho adiciona permissão de ler arquivos
        int PERMISSION_REQUEST = 0;

        if(ContextCompat.checkSelfPermission(PerfilPessoaJuridicaActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //Não tem permissão: solicitar
            if(ActivityCompat.shouldShowRequestPermissionRationale(PerfilPessoaJuridicaActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(PerfilPessoaJuridicaActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        }
        //Trecho adiciona permissão de gravar arquivos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        }
    }

    private void carregandoFoto(){
        StorageReference ref = storageReference.child("images/perfil/" + FirebaseController.getUidUser() + ".bmp");

        try {
            final File localFile = File.createTempFile("images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap minhaFoto = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imgPerfilPessoaJuridica.setImageBitmap(minhaFoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            Log.d("IOException downlaod", e.getMessage());
        }
    }

    private void permissaoAcessarCamera() {
        //Verifica permissão de camera
        int permissionCheck = ContextCompat.checkSelfPermission(PerfilPessoaJuridicaActivity.this, Manifest.permission.CAMERA);
        //Se tiver permissão, então a camêra será aberta
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            tirarFoto();
        }
        //Caso contrário solicitará ao usuário
        else{
            ActivityCompat.requestPermissions(PerfilPessoaJuridicaActivity.this,new String[]{
                    Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
        }
    }
    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil_pessoa_juridica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
