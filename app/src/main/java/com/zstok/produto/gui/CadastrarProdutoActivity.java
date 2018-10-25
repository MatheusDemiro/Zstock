package com.zstok.produto.gui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.R;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.negocio.ProdutoServices;

import java.math.BigDecimal;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastrarProdutoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALERY_REQUEST_CODE = 71;

    private EditText edtNomeProduto;
    private EditText edtPrecoProduto;
    private EditText edtQuantidadeEstoqueProduto;
    private EditText edtDescricaoProduto;
    private CircleImageView cvCadastrarProduto;

    private Uri uriFoto;
    private VerificaConexao verificaConexao;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Criar novo produto");
        setContentView(R.layout.activity_cadastrar_produto);

        //Solicitando permissão ao usuário para ler e gravar arquivos
        permissaoGravarLerArquivos();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando storage
        storageReference = FirebaseStorage.getInstance().getReference();

        //Inicializando o objeto da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        //Instanciando views
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtPrecoProduto = findViewById(R.id.edtPrecoProduto);
        edtQuantidadeEstoqueProduto = findViewById(R.id.edtQuantidadeEstoqueProduto);
        edtDescricaoProduto = findViewById(R.id.edtDescricaoProduto);
        cvCadastrarProduto = findViewById(R.id.cvCadastrarProduto);

        //Mascara Monetária
        Locale mLocale = new Locale("pt", "BR");
        edtPrecoProduto.addTextChangedListener(new MoneyTextWatcher(edtPrecoProduto,mLocale));

        Button btnCadastrarProduto = findViewById(R.id.btnCadastrarProduto);

        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                        iniciarProgressDialog();
                        inserirProduto(criarProduto());
                        edtDescricaoProduto.setSelection(0);
                    }
                }
            }
        });

        FloatingActionButton fabAbrirGaleriaPerfilPessoaFisica = findViewById(R.id.fabAbrirGaleriaCadastrarProduto);
        fabAbrirGaleriaPerfilPessoaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        FloatingActionButton fabAbrirCameraCadastrarProduto =  findViewById(R.id.fabAbrirCameraCadastrarProduto);
        fabAbrirCameraCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissaoAcessarCamera();
            }
        });
    }
    //Validando campos
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtNomeProduto.getText().toString().isEmpty() || edtNomeProduto.getText().toString().trim().length() == 0){
            edtNomeProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtPrecoProduto.getText().toString().isEmpty() || edtPrecoProduto.getText().toString().trim().length() == 0){
            edtPrecoProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }else {
            BigDecimal precoProduto = MoneyTextWatcher.convertToBigDecimal(edtPrecoProduto.getText().toString());
            if ((precoProduto.compareTo(new BigDecimal(50000))) == 1){
                edtPrecoProduto.setError("Preco do produto excede o máximo: R$ 50.000,00");
                verificador = false;
            }
        }
        if (edtQuantidadeEstoqueProduto.getText().toString().isEmpty() || edtQuantidadeEstoqueProduto.getText().toString().trim().length() == 0){
            edtQuantidadeEstoqueProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }

        if (edtQuantidadeEstoqueProduto.getText().toString().isEmpty() || edtQuantidadeEstoqueProduto.getText().toString().trim().length() == 0){
            edtQuantidadeEstoqueProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtDescricaoProduto.getText().toString().isEmpty() || edtDescricaoProduto.getText().toString().trim().length() == 0){
            edtDescricaoProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }

        return verificador;
    }
    //Permissão para ler e gravar arquivos do celular
    private void permissaoGravarLerArquivos(){
        //Trecho adiciona permissão de ler arquivos
        int PERMISSION_REQUEST = 0;

        if(ContextCompat.checkSelfPermission(CadastrarProdutoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //Não tem permissão: solicitar
            if(ActivityCompat.shouldShowRequestPermissionRationale(CadastrarProdutoActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(CadastrarProdutoActivity.this,
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
    //Permissão para tirar foto
    private void permissaoAcessarCamera() {
        //Verifica permissão de camera
        int permissionCheck = ContextCompat.checkSelfPermission(CadastrarProdutoActivity.this, Manifest.permission.CAMERA);
        //Se tiver permissão, então a camêra será aberta
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            tirarFoto();
        }
        //Caso contrário solicitará ao usuário
        else{
            ActivityCompat.requestPermissions(CadastrarProdutoActivity.this,new String[]{
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
    //Esse método trata as permissões do usuário
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALERY_REQUEST_CODE:
                Bitmap bitmapCadstrarProduto;
            {
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    Glide.with(CadastrarProdutoActivity.this).load(uriFoto).into(cvCadastrarProduto);
                }
            }
            case CAMERA_REQUEST_CODE: {
                if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmapCadstrarProduto = (Bitmap) extras.get("data");
                            uriFoto = Helper.getImageUri(getApplicationContext(), bitmapCadstrarProduto);
                            cvCadastrarProduto.setImageBitmap(bitmapCadstrarProduto);
                        }
                    }
                }
            }
        }
    }
    //Método que preenche objeto produto
    private Produto criarProduto(){
        Produto produto = new Produto();

        produto.setIdEmpresa(FirebaseController.getUidUser());
        produto.setNome(edtNomeProduto.getText().toString());
        produto.setNomePesquisa(Helper.removerAcentos(edtNomeProduto.getText().toString().toLowerCase()));
        produto.setPrecoSugerido(MoneyTextWatcher.convertToBigDecimal(edtPrecoProduto.getText().toString()).doubleValue());
        produto.setQuantidadeEstoque(Integer.valueOf(edtQuantidadeEstoqueProduto.getText().toString()));
        produto.setDescricao(edtDescricaoProduto.getText().toString());

        return produto;
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_cadastrando_produto));
        progressDialog.show();
    }
    //Inserindo foto no banco
    private void inserirFoto(final Produto produto){
        StorageReference reference = storageReference.child("images/produtos/" + FirebaseController.getUidUser() + "/" + produto.getIdProduto() + ".bmp");
        reference.putFile(uriFoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri donwloadUri = taskSnapshot.getDownloadUrl();
                if (donwloadUri != null) {
                    FirebaseController.getFirebase().child("produto").child(produto.getIdProduto()).child("urlImagem").setValue(donwloadUri.toString());
                    finalizarCadastroProduto();
                }else {
                    progressDialog.dismiss();
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                }
            }
        });
    }
    //Finalizando o cadastro
    private void finalizarCadastroProduto(){
        progressDialog.dismiss();
        Helper.criarToast(getApplicationContext(), getString(R.string.zs_produto_cadastrado_sucesso));
        abrirTelaMeusProdutosActivity();
    }
    //Inserindo dados do produto no banco
    private void inserirProduto(Produto produto){
        if (ProdutoServices.insereProduto(produto)){
            if (uriFoto != null) {
                inserirFoto(produto);
            }else {
                finalizarCadastroProduto();
                abrirTelaMeusProdutosActivity();
            }
        } else {
            progressDialog.dismiss();
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Intent para a tela meus produtos
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
    }
}