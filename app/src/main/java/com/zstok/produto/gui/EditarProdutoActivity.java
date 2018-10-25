package com.zstok.produto.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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

import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarProdutoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALERY_REQUEST_CODE = 71;

    private String idProduto;

    private CircleImageView cvImagemProduto;
    private EditText edtNomeProduto;
    private EditText edtPrecoProduto;
    private EditText edtQuantidadeEstoqueProduto;
    private EditText edtDescricaoProduto;

    private VerificaConexao verificaConexao;

    private Uri uriFoto;

    private ProgressDialog progressDialogCarregandoInformacoes;
    private ProgressDialog progressDialogAlterandoProduto;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        //Resgatando id do produto enviado pela intent
        idProduto = getIntent().getStringExtra("idProduto");

        //Instanciando progress dialog
        progressDialogCarregandoInformacoes = new ProgressDialog(this);
        progressDialogAlterandoProduto = new ProgressDialog(this);

        //Instanciando storage
        storageReference = FirebaseStorage.getInstance().getReference();

        cvImagemProduto = findViewById(R.id.cvEditarProduto);
        edtNomeProduto = findViewById(R.id.edtNomeProduto);
        edtPrecoProduto = findViewById(R.id.edtPrecoProduto);
        edtQuantidadeEstoqueProduto = findViewById(R.id.edtQuantidadeEstoqueProduto);
        edtDescricaoProduto = findViewById(R.id.edtDescricaoProduto);

        verificaConexao = new VerificaConexao(this);

        //Mascara Monetária
        Locale mLocale = new Locale("pt", "BR");
        edtPrecoProduto.addTextChangedListener(new MoneyTextWatcher(edtPrecoProduto,mLocale));

        //Recuperando informações do produto
        recuperarProduto();

        Button btnAlterarProduto = findViewById(R.id.btnAlterarProduto);

        btnAlterarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    if (verificaConexao.isConected()){
                        iniciarProgressDialogAlterarProduto();
                        alterarProduto(criarProduto());
                    }
                }
            }
        });

        FloatingActionButton fabAbrirGaleriaEditarProduto = findViewById(R.id.fabAbrirGaleriaEditarProduto);
        fabAbrirGaleriaEditarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherFoto();
            }
        });

        FloatingActionButton fabAbrirCameraEditarProduto =  findViewById(R.id.fabAbrirCameraEditarProduto);
        fabAbrirCameraEditarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
            }
        });
    }

    private void recuperarProduto() {
        iniciarProgressDialogCarregarInformaçoes();
        FirebaseController.getFirebase().child("produto").child(idProduto)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.getValue(Produto.class);
                if (produto != null) {
                    if (produto.getUrlImagem() != null) {
                        recuperarFoto(produto.getUrlImagem());
                    }else {
                        cvImagemProduto.setImageResource(R.drawable.ic_produtos);
                        progressDialogCarregandoInformacoes.dismiss();
                    }
                    edtNomeProduto.setText(produto.getNome());
                    edtPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                    edtQuantidadeEstoqueProduto.setText(String.valueOf(produto.getQuantidadeEstoque()));
                    edtDescricaoProduto.setText(produto.getDescricao());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Resgatando foto do Storage
    private void recuperarFoto(String urlImagemProduto){
        if (urlImagemProduto != null) {
            Glide.with(EditarProdutoActivity.this).load(Uri.parse(urlImagemProduto)).into(cvImagemProduto);
        }
        progressDialogCarregandoInformacoes.dismiss();
    }
    //Validar campos
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
    //Método que preenche objeto produto
    private Produto criarProduto(){
        Produto produto = new Produto();

        produto.setNome(edtNomeProduto.getText().toString());
        produto.setNomePesquisa(Helper.removerAcentos(edtNomeProduto.getText().toString().toLowerCase()));
        produto.setPrecoSugerido(MoneyTextWatcher.convertToBigDecimal(edtPrecoProduto.getText().toString()).doubleValue());
        produto.setQuantidadeEstoque(Integer.valueOf(edtQuantidadeEstoqueProduto.getText().toString()));
        produto.setDescricao(edtDescricaoProduto.getText().toString());
        produto.setIdProduto(idProduto);

        return produto;
    }
    //Método que inicia o progress dialog para carregar informações
    private void iniciarProgressDialogCarregarInformaçoes() {
        progressDialogCarregandoInformacoes.setCanceledOnTouchOutside(false);
        progressDialogCarregandoInformacoes.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_informacoes_produto));
        progressDialogCarregandoInformacoes.show();
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialogAlterarProduto() {
        progressDialogCarregandoInformacoes.setCanceledOnTouchOutside(false);
        progressDialogCarregandoInformacoes.setTitle(getString(R.string.zs_titulo_progress_dialog_alterar_produto));
        progressDialogCarregandoInformacoes.show();
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
                    finalizarAlteracao();
                }else {
                    progressDialogAlterandoProduto.dismiss();
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                }
            }
        });
    }
    //Finalizando alteração do produto
    private void finalizarAlteracao() {
        progressDialogCarregandoInformacoes.dismiss();
        Helper.criarToast(getApplicationContext(), getString(R.string.zs_produto_alterado_sucesso));
        abrirTelaMeusProdutosActivity();
    }
    //Inserindo imagem no banco
    private void alterarProduto(Produto produto){
        if (ProdutoServices.alterarProduto(produto)){
            if (uriFoto != null) {
                inserirFoto(produto);
            }else {
                finalizarAlteracao();
            }
        } else {
            progressDialogAlterandoProduto.dismiss();
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
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
                Bitmap bitmapCadstrarProduto;
            {
                if (requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK) {
                    uriFoto = data.getData();
                    Glide.with(EditarProdutoActivity.this).load(uriFoto).into(cvImagemProduto);
                }
            }
            case CAMERA_REQUEST_CODE: {
                if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmapCadstrarProduto = (Bitmap) extras.get("data");
                            uriFoto = Helper.getImageUri(getApplicationContext(), bitmapCadstrarProduto);
                            cvImagemProduto.setImageBitmap(bitmapCadstrarProduto);
                        }
                    }
                }
            }
        }
    }
    //Intent para abrir meus produtos
    private void abrirTelaMeusProdutosActivity(){
        Intent intent = new Intent(getApplicationContext(), MeusProdutosActivity.class);
        startActivity(intent);
        finish();
    }
}