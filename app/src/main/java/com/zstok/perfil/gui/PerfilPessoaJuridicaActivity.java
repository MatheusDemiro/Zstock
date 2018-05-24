package com.zstok.perfil.gui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;
import com.zstok.pessoaJuridica.gui.MainPessoaJuridicaActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALERY_REQUEST_CODE = 71;
    private Uri uriFoto;

    private AlertDialog alertaSair;

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;

    private CircleImageView cvPerfilPessoaJuridica;
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

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //InstanciandoAsViews
        cvPerfilPessoaJuridica = findViewById(R.id.imgPerfilPessoaJuridica);
        tvNomeFantasiaPerfilJuridico = findViewById(R.id.tvNomeFantasiaPerfilJuridico);
        tvRazaoSocialPerfilJuridico = findViewById(R.id.tvRazaoSocialPerfilJuridico);
        tvEmailPerfilJuridico = findViewById(R.id.tvEmailPerfilJuridico);
        tvCnpjPerfilJuridico = findViewById(R.id.tvCnpjPerfilJuridico);
        tvTelefonePerfilJuridico = findViewById(R.id.tvTelefonePerfilJuridico);
        tvEnderecoPerfilJuridico = findViewById(R.id.tvEnderecoPerfilJuridico);

        //Instanciando as views
        instanciandoView();

        //Carregandos informações do menu lateral
        setDadosMenuLateral();

        //Recuperando dados do perfil
        recuperarDados();

        //Solicitando permissão ao usuário
        permissaoGravarLerArquivos();

        //Carregando foto do perfil
        carregarFoto();

        //Máscaras cnpj e telfone
        Helper.mascaraCpf(tvCnpjPerfilJuridico);
        Helper.mascaraTelefone(tvTelefonePerfilJuridico);

        FloatingActionButton fabAbrirGaleriaPerfilPessoaJuridica = findViewById(R.id.fabAbrirGaleriaCameraPerfilPessoaJuridica);
        fabAbrirGaleriaPerfilPessoaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });
        FloatingActionButton fabAbrirCameraPerfilPessoaJuridica = findViewById(R.id.fabAbrirCameraPerfilPessoaJuridica);
        fabAbrirCameraPerfilPessoaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissaoAcessarCamera();
            }
        });

        tvNomeFantasiaPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarNomeActivity();

            }
        });

        tvRazaoSocialPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarRazaoSocialActivity();
            }
        });

        tvEmailPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarEmailActivity();
            }
        });

        tvCnpjPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarCnpjActivity();
            }
        });

        tvTelefonePerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarTelefoneActivity();
            }
        });

        tvEnderecoPerfilJuridico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaAlterarEnderecoActivity();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_juridico:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_negociacao_juridico:
                        //Função abrir tela negociacao
                        abrirTelaMainPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_produtos:
                        //Função abrir tela produtos
                        return true;
                    case R.id.nav_sair:
                        sair();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    //Carregando foto de perfil
    private void carregarFoto(){
        FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(cvPerfilPessoaJuridica);
                Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
            }else {
                Helper.criarToast(getApplicationContext(), "ERROR");
            }
        }
    }
    //Método que inicializa as instâncias dos itens do menu lateral
    private void instanciandoView(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Recuperando os dados do perfil
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
    //Carregando as informções do perfil
    private void setInformacoesPerfil(Pessoa pessoa, PessoaJuridica pessoaJuridica){
        tvNomeFantasiaPerfilJuridico.setText(pessoa.getNome());
        tvTelefonePerfilJuridico.setText(pessoa.getTelefone());
        tvCnpjPerfilJuridico.setText(pessoaJuridica.getCnpj());
        tvEmailPerfilJuridico.setText(FirebaseController.getFirebaseAuthentication().getCurrentUser().getEmail());
        tvEnderecoPerfilJuridico.setText(pessoa.getEndereco());
        tvRazaoSocialPerfilJuridico.setText(pessoaJuridica.getRazaoSocial());
    }

    private void setDadosMenuLateral(){
        PerfilServices.setDadosNavHeader(FirebaseController.getFirebaseAuthentication().getCurrentUser(),tvNomeUsuarioNavHeader,tvEmailUsuarioNavHeader);
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
    //Permissão para acessar a câmera
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
    //Método que abre a galeria
    private void escolherFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem"), GALERY_REQUEST_CODE);
    }
    //Método que abre a câmera
    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALERY_REQUEST_CODE:{
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriFoto);
                        cvPerfilPessoaJuridica.setImageBitmap(bitmap);
                        cvNavHeaderPessoa.setImageBitmap(bitmap);
                        inserirFoto(uriFoto);
                    }catch(IOException e ){
                        Log.d("IOException upload", e.getMessage());
                    }
                }
            }
            case CAMERA_REQUEST_CODE: {
                if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap bitmap = (Bitmap) extras.get("data");
                            uriFoto = getImageUri(getApplicationContext(), bitmap);
                            cvPerfilPessoaJuridica.setImageBitmap(bitmap);
                            cvNavHeaderPessoa.setImageBitmap(bitmap);
                            inserirFoto(uriFoto);
                        }
                    }
                }
            }
        }
    }
    //Inserindo imagem no banco
    private void inserirFoto(Uri uriFoto){
        PerfilServices.insereFoto(uriFoto);
    }
    //Esse método trata as permissões do usuário
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    tirarFoto();
                    break;
                }
            }
            case GALERY_REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    escolherFoto();
                    break;
                }
            }
        }
    }
    //Método que resgata o caminha da foto
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void sair () {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle(getString(R.string.zs_dialogo_titulo));
        //define a mensagem
        builder.setMessage(getString(R.string.zs_dialogo_mensagem_sair_conta));
        //define um botão como positivo
        builder.setPositiveButton(getString(R.string.zs_dialogo_sim), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                FirebaseAuth.getInstance().signOut();
                abrirTelaLoginActivity();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton(getString(R.string.zs_dialogo_nao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                alertaSair.dismiss();
            }
        });
        //cria o AlertDialog
        alertaSair = builder.create();
        //Exibe
        alertaSair.show();
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
    private void abrirTelaAlterarNomeActivity(){
        Intent intent = new Intent(getApplicationContext(), AlterarNomePessoaActivity.class);
        startActivity(intent);
    }
    private void abrirTelaAlterarEmailActivity(){
        Intent intent = new Intent(getApplicationContext(), AlterarEmailPessoaActivity.class);
        startActivity(intent);
    }
    private void abrirTelaAlterarTelefoneActivity() {
        Intent intent = new Intent(getApplicationContext(), AlterarTelefonePessoaActivity.class);
        startActivity(intent);
    }

    private void abrirTelaAlterarEnderecoActivity() {
        Intent intent = new Intent(getApplicationContext(), AlterarEnderecoPessoaActivity.class);
        startActivity(intent);
    }
    private void abrirTelaAlterarCnpjActivity() {
        Intent intent = new Intent(getApplicationContext(),AlterarCnpjPessoaJuridicaActivity.class);
        startActivity(intent);
    }

    private void abrirTelaAlterarRazaoSocialActivity() {
        Intent intent = new Intent(getApplicationContext(),AlterarRazaoSocialPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para tela de login
    private void abrirTelaLoginActivity () {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela main
    private void abrirTelaMainPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainPessoaJuridicaActivity.class);
        startActivity(intent);
    }
}