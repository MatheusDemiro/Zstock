package com.zstok.produto.gui;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zstok.R;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.perfil.negocio.PerfilServices;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.negocio.ProdutoServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastrarProdutoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int GALERY_REQUEST_CODE = 1;

    private EditText edtNomeProduto;
    private EditText edtPrecoProduto;
    private EditText edtQuantidadeEstoqueProduto;
    private EditText edtDescricaoProduto;
    private CircleImageView cvCadastrarProduto;

    private Uri uriFoto;
    private Bitmap bitmapCadstrarProduto;

    private VerificaConexao verificaConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Criar novo produto");
        setContentView(R.layout.activity_cadastrar_produto);

        //Solicitando permissão ao usuário para ler e gravar arquivos
        permissaoGravarLerArquivos();

        //Inicializando o objeto da classe VerificaConexao
        verificaConexao = new VerificaConexao(this);

        //Instanciando views
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtPrecoProduto = findViewById(R.id.edtPrecoProduto);
        edtQuantidadeEstoqueProduto = findViewById(R.id.edtQuantidadeEstoqueProduto);
        edtDescricaoProduto = findViewById(R.id.edtDescricaoProduto);
        cvCadastrarProduto = findViewById(R.id.cvCadstrarProduto);
        Button btnCadastrarProduto = findViewById(R.id.btnCadastrarProduto);

        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                        inserirProduto(uriFoto, criarProduto());
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
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtNomeProduto.getText().toString().isEmpty() || edtNomeProduto.getText().toString().trim().length() == 0){
            edtNomeProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        if (edtPrecoProduto.getText().toString().isEmpty() || edtPrecoProduto.getText().toString().trim().length() == 0){
            edtPrecoProduto.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
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
                }
            }
            case GALERY_REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    escolherFoto();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GALERY_REQUEST_CODE:
            {
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    try{
                        bitmapCadstrarProduto = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFoto);
                        cvCadastrarProduto.setImageBitmap(bitmapCadstrarProduto);
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
                            bitmapCadstrarProduto = (Bitmap) extras.get("data");
                            uriFoto = getImageUri(getApplicationContext(), bitmapCadstrarProduto);
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

        produto.setNomeProduto(edtNomeProduto.getText().toString());
        produto.setPreco(Double.valueOf(edtPrecoProduto.getText().toString()));
        produto.setQuantidadeEstoque(Integer.valueOf(edtQuantidadeEstoqueProduto.getText().toString()));
        produto.setDescricao(edtDescricaoProduto.getText().toString());

        return produto;
    }
    //Inserindo imagem no banco
    private void inserirProduto(Uri uriFoto, Produto produto){
        if (ProdutoServices.insereProduto(uriFoto, produto)){
            abrirTelaMeusProdutosActivity();
        } else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Obtendo URI da imagem
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //Intent para a tela meus produtos
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
    }
}
