package com.zstok.perfil.gui;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.R;
import com.zstok.historico.gui.MainHistoricoVendaPessoaJuridicaActivity;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;
import com.zstok.negociacao.gui.MainNegociacaoPessoaJuridicaActivity;
import com.zstok.produto.gui.MeusProdutosActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALERY_REQUEST_CODE = 71;
    private static final int PERMISSION_REQUEST = 0;

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

    private FirebaseUser user;

    private ProgressDialog progressDialog;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Solicitando permissão ao usuário
        permissaoGravarLerArquivos();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando storage
        storageReference = FirebaseStorage.getInstance().getReference();

        //Resgatando usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando AsViews
        cvPerfilPessoaJuridica = findViewById(R.id.imgPerfilPessoaJuridica);
        tvNomeFantasiaPerfilJuridico = findViewById(R.id.tvNomeFantasiaPerfilJuridico);
        tvRazaoSocialPerfilJuridico = findViewById(R.id.tvRazaoSocialPerfilJuridico);
        tvEmailPerfilJuridico = findViewById(R.id.tvEmailPerfilJuridico);
        tvCnpjPerfilJuridico = findViewById(R.id.tvCnpjPerfilJuridico);
        tvTelefonePerfilJuridico = findViewById(R.id.tvTelefonePerfilJuridico);
        tvEnderecoPerfilJuridico = findViewById(R.id.tvEnderecoPerfilJuridico);

        //Habilitando o scrollbars do TextView (quando necessário o scroll irá aparecer)
        habilitarScrollBars();

        //Instanciando as views
        instanciandoView();

        //Carregandos informações do menu lateral
        setDadosMenuLateral();

        //Recuperando dados do perfil
        recuperarDados();

        //Máscaras cnpj
        Helper.mascaraCnpj(tvCnpjPerfilJuridico);

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
                    case R.id.nav_meu_perfil_pessoa_juridica:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_negociacao_pessoa_juridica:
                        //Função abrir tela negociacao
                        abrirTelaMainPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_juridica:
                        //Função abrir tela produtos
                        abrirTelaMeusProdutosActivity();
                        return true;
                    case R.id.nav_meu_historico_vendas_pessoa_juridica:
                        //Função abrir tela histórico
                        abrirTelaMainHistoricoVendasPessoaJuridicaActivity();
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
    //Método que habilita o scrollbars do TextView endereço
    private void habilitarScrollBars() {
        tvEnderecoPerfilJuridico.setMaxLines(Integer.MAX_VALUE);
        tvEnderecoPerfilJuridico.setMovementMethod(new ScrollingMovementMethod());
    }
    //Carregando foto de perfil
    private void carregarFoto(){
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(cvPerfilPessoaJuridica);
                progressDialog.dismiss();
            }else {
                cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
            }
        }
        progressDialog.dismiss();
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
        iniciarProgressDialog();

        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pessoa pessoa = dataSnapshot.child("pessoa").child(FirebaseController.getUidUser()).getValue(Pessoa.class);
                PessoaJuridica pessoaJuridica = dataSnapshot.child("pessoaJuridica").child(FirebaseController.getUidUser()).getValue(PessoaJuridica.class);

                if (pessoa != null && pessoaJuridica != null){
                    setInformacoesPerfil(pessoa, pessoaJuridica);
                    carregarFoto();
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
        tvCnpjPerfilJuridico.setText(pessoaJuridica.getCnpj());
        tvEmailPerfilJuridico.setText(user.getEmail());
        tvEnderecoPerfilJuridico.setText(pessoa.getEndereco());
        tvRazaoSocialPerfilJuridico.setText(pessoaJuridica.getRazaoSocial());
        verificandoTipoTelefone(pessoa);
        progressDialog.dismiss();
    }
    //Verificando o tipo de telefone
    private void verificandoTipoTelefone(Pessoa pessoa) {
        if (pessoa.getTelefone() != null) {
            int primeiroDigito = Integer.valueOf(pessoa.getTelefone().substring(2, 3));
            if (primeiroDigito >= 2 && primeiroDigito <= 5) {
                Helper.mascaraTelefone(tvTelefonePerfilJuridico);
            } else {
                Helper.mascaraCelular(tvTelefonePerfilJuridico);
            }
        }
        tvTelefonePerfilJuridico.setText(pessoa.getTelefone());
    }
    //Carregando informações do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
    }
    //Permissão para ler e gravar arquivos do celular
    private void permissaoGravarLerArquivos(){
        //Trecho adiciona permissão de ler arquivos
        int permissionCheckRead = ContextCompat.checkSelfPermission(PerfilPessoaJuridicaActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionCheckRead != PackageManager.PERMISSION_GRANTED){
            //Não tem permissão: solicitar
            if(ActivityCompat.shouldShowRequestPermissionRationale(PerfilPessoaJuridicaActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }else{
                ActivityCompat.requestPermissions(PerfilPessoaJuridicaActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
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
            case GALERY_REQUEST_CODE:
                Uri uriFoto;
            {
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    try{
                        Bitmap bitmapFotoPerfil = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFoto);
                        setFotoCircleView(bitmapFotoPerfil);
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
                            Bitmap bitmapFotoPerfil = (Bitmap) extras.get("data");
                            setFotoCircleView(bitmapFotoPerfil);
                            uriFoto = Helper.getImageUri(getApplicationContext(), bitmapFotoPerfil);
                            inserirFoto(uriFoto);
                        }
                    }
                }
            }
        }
    }
    //Método para setar foto de perfil e menu lateral
    private void setFotoCircleView(Bitmap bitmapFotoPerfil) {
        cvPerfilPessoaJuridica.setImageBitmap(bitmapFotoPerfil);
        cvNavHeaderPessoa.setImageBitmap(bitmapFotoPerfil);
    }
    //Inserindo imagem no banco
    private void inserirFoto(Uri uriFoto){
        iniciarProgressDialog();

        StorageReference ref = storageReference.child("images/perfil/" + FirebaseController.getUidUser() + ".bmp");
        ref.putFile(uriFoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String urlImagem = taskSnapshot.getDownloadUrl().toString();
                FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
                if (user != null && urlImagem != null) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(urlImagem))
                            .build();
                    user.updateProfile(profileChangeRequest);
                }
                progressDialog.dismiss();
            }
        });
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
            case PERMISSION_REQUEST:{
                if (!(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    abrirTelaMainPessoaJuridicaActivity();
                    break;
                }
            }
        }
    }
    //Iniciando progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_perfil));
        progressDialog.show();
    }
    //Método que exibe a caixa de diálogo para o usuário confirmar ou não a sua saída do sistema
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //Intent para a tela main
    private void abrirTelaMainPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent meus produtos
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de histórico pessoa jurídica, onde estão os produtos
    private void abrirTelaMainHistoricoVendasPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoVendaPessoaJuridicaActivity.class);
        startActivity(intent);
    }
}