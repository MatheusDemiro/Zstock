package com.zstok.produto.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.pessoaJuridica.gui.VisualizarEmpresaActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.negocio.ProdutoServices;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class VisualizarProdutoActivity extends AppCompatActivity {

    private String idEmpresa;
    private String idProduto;
    private TextView tvNomeProduto;
    private TextView tvQuantidadeDisponivelProduto;
    private TextView tvPrecoProduto;
    private TextView tvDescricaoProduto;
    private TextView tvEmpresaProduto;
    private ImageView imgProduto;

    //Views da caixa de diálogo
    private EditText edtQuantidadeDialogoCompra;
    private TextView tvNomeProdutoDialogoCompra;
    private TextView tvTotalDialogoCompra;
    private Button btnComprarDialogoCompra;
    private Button btnVoltarDialogoCompra;

    private AlertDialog alertaCompra;

    private VerificaConexao verificaConexao;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_produto);

        //Resgatando elementos passados pela intent
        idEmpresa = getIntent().getStringExtra("idEmpresa");
        idProduto = getIntent().getStringExtra("idProduto");

        //Instanciando views
        tvNomeProduto = findViewById(R.id.tvNomeProduto);
        tvQuantidadeDisponivelProduto = findViewById(R.id.tvQuantidadeDisponivelProduto);
        tvPrecoProduto = findViewById(R.id.tvPrecoProduto);
        tvDescricaoProduto = findViewById(R.id.tvDescricaoProduto);
        tvEmpresaProduto = findViewById(R.id.tvEmpresaProduto);
        imgProduto = findViewById(R.id.imgProduto);
        Button btnAdicionarAoCarrinho = findViewById(R.id.btnAdicionarAoCarrinho);

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando objeto para verificar conexão
        verificaConexao = new VerificaConexao(this);

        btnAdicionarAoCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCompra();
            }
        });

        //Recuperando dados do firebase e setando campos da activity
        recuperarDados();

        tvEmpresaProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaVisualizarEmpresaActivity();

            }
        });

        FirebaseController.getFirebase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvQuantidadeDisponivelProduto.setText(String.valueOf(dataSnapshot.child("produto").child(idProduto).child("quantidadeEstoque").getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Recuperando dados do firebase
    private void recuperarDados(){
        iniciarProgressDialog();
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.child("produto").child(idProduto).getValue(Produto.class);
                Pessoa pessoa = dataSnapshot.child("pessoa").child(idEmpresa).getValue(Pessoa.class);

                if (produto != null && pessoa != null) {
                    setarCampos(pessoa, produto);
                    setarFoto(produto);
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
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_informacoes_produto));
        progressDialog.show();
    }
    //Setando campo foto do produto
    private void setarFoto(Produto produto){
        if (produto.getUrlImagem() != null) {
            Glide.with(VisualizarProdutoActivity.this).load(Uri.parse(produto.getUrlImagem())).into(imgProduto);
        }else {
            imgProduto.setImageResource(R.drawable.ic_produtos);
        }
        progressDialog.dismiss();
    }
    //Setando campos da activity
    private void setarCampos(Pessoa pessoa, Produto produto) {
        tvNomeProduto.setText(produto.getNome());
        tvPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
        tvDescricaoProduto.setText(produto.getDescricao());
        tvEmpresaProduto.setText(pessoa.getNome());
    }
    //Método que abre a caixa de diálogo
    private void iniciarCompra () {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualizarProdutoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.modelo_caixa_dialogo_compra, null);

        //Instanciando views
        instanciandoViews(mView);

        //Setando informações das views
        setarInformacoesViews();

        builder.setView(mView);
        alertaCompra = builder.create();
        alertaCompra.show();

        //Chamando método para tratar o clique no botão voltar
        clickVoltar();

        //Chamando método para tratar o clique no botão comprar
        clickAdicionarAoCarrinho();
    }
    //Método que implementa o evento de click do botão voltar
    private void clickVoltar(){
        btnVoltarDialogoCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaCompra.dismiss();
            }
        });
    }
    //Método que implementa o evento de click do botão comprar
    //Value listener of quantidade
    private void clickAdicionarAoCarrinho(){
        btnComprarDialogoCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampos()){
                        if(validarClickCompra()){
                            adicionarProdutoCarrinho();
                        }
                    }
                }
            }
        });
    }
    //Validando edit text da quantidade digitavda pelo usuário na caixa de diálogo
    private boolean validarCampos(){
        boolean verificador = true;

        if (edtQuantidadeDialogoCompra.getText().toString().isEmpty() || edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0){
            edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Método que instancia as views da caixa de diálogo
    private void instanciandoViews(View mView){
        edtQuantidadeDialogoCompra = mView.findViewById(R.id.edtQuantidadeDesejadaDialogoCompra);
        tvNomeProdutoDialogoCompra = mView.findViewById(R.id.tvNomeProdutoDialogoCompra);
        tvTotalDialogoCompra = mView.findViewById(R.id.tvTotalDialogoCompra);
        btnVoltarDialogoCompra = mView.findViewById(R.id.btnVoltarDialogoCompra);
        btnComprarDialogoCompra = mView.findViewById(R.id.btnAdicionarItemDialogoCarrinho);
    }
    //Método que seta as informações para as views da caixa de diálogo
    private void setarInformacoesViews(){
        tvNomeProdutoDialogoCompra.setText(tvNomeProduto.getText());
        edtQuantidadeDialogoCompra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtQuantidadeDialogoCompra.getText().toString().isEmpty() ||
                        !(edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0)){
                    int quantidadeItens = Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString());
                    BigDecimal precoItem = MoneyTextWatcher.convertToBigDecimal(tvPrecoProduto.getText().toString());
                    tvTotalDialogoCompra.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(precoItem.multiply(new BigDecimal(quantidadeItens)))));
                }

        }
            @Override
            public void afterTextChanged(Editable s) {
                if (!edtQuantidadeDialogoCompra.getText().toString().isEmpty() ||
                        !(edtQuantidadeDialogoCompra.getText().toString().trim().length() == 0)){
                    int quantidadeItens = Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString());
                    BigDecimal precoItem = MoneyTextWatcher.convertToBigDecimal(tvPrecoProduto.getText().toString());
                    tvTotalDialogoCompra.setText(MoneyTextWatcher.convertStringToMoney(String.valueOf(precoItem.multiply(new BigDecimal(quantidadeItens)))));
                }else {
                    tvTotalDialogoCompra.setText("");
                }
            }
        });
    }
    //Método que cria um objeto ItemCompra
    private ItemCompra criarItemCompra(){
        ItemCompra itemCompra = new ItemCompra();

        itemCompra.setIdProduto(idProduto);
        itemCompra.setValor(MoneyTextWatcher.convertToBigDecimal(tvPrecoProduto.getText().toString()).doubleValue());
        itemCompra.setQuantidade(Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString()));

        return itemCompra;
    }
    //Chamando camada de negócio para fazer a redução da quantidade
    private void adicionarProdutoCarrinho(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Verifica se carrinho já existe carrinho.
                if(dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()){
                    Double totalCarrinho = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                    Double precoProduto = dataSnapshot.child("produto").child(idProduto).child("precoSugerido").getValue(Double.class);
                    double novoTotal = totalCarrinho+(precoProduto* Double.valueOf(edtQuantidadeDialogoCompra.getText().toString()));
                    if(novoTotal <= 50000.0){
                        if (ProdutoServices.adicionarProdutoCarrinho(criarItemCompra(),dataSnapshot)){
                            alertaCompra.dismiss();
                            Helper.criarToast(getApplicationContext(), getString(R.string.zs_adicionar_carrinho_compra_sucesso));
                            abrirTelaMainPessoaFisicaActivity();
                        }else{
                            Helper.criarToast(getApplicationContext(),getString(R.string.zs_excecao_database));
                        }
                    }else{
                        edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_quantidade_excedida));
                    }

                }else{
                    if (ProdutoServices.adicionarProdutoCarrinho(criarItemCompra(),dataSnapshot)){
                        alertaCompra.dismiss();
                        abrirTelaMainPessoaFisicaActivity();
                        Helper.criarToast(getApplicationContext(), getString(R.string.zs_adicionar_carrinho_compra_sucesso));
                    }else{
                        Helper.criarToast(getApplicationContext(),getString(R.string.zs_excecao_database));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Validando compra
    private boolean validarClickCompra(){
        boolean verificador = true;
        BigDecimal bigDecimal = MoneyTextWatcher.convertToBigDecimal(tvTotalDialogoCompra.getText().toString());
        BigDecimal bigDecimal1 = new BigDecimal(50000);
        if ((bigDecimal.compareTo(bigDecimal1)) == 1){
            edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_quantidade_excedida));
            verificador = false;
        }
        if(Integer.valueOf(tvQuantidadeDisponivelProduto.getText().toString()) < Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString())){
            edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_quantidade_maxima));
            verificador = false;
        }
        if (Integer.valueOf(edtQuantidadeDialogoCompra.getText().toString()) == 0){
            edtQuantidadeDialogoCompra.setError(getString(R.string.zs_excecao_quantidade_invalida));
            verificador = false;
        }
        return verificador;
    }
    //Intent para a tela de visualização da empresa
    private void abrirTelaVisualizarEmpresaActivity(){
        Intent intent = new Intent(getApplicationContext(), VisualizarEmpresaActivity.class);
        intent.putExtra("idEmpresa", idEmpresa);
        startActivity(intent);
    }
    //Intent para a tela de visualização da empresa
    private void abrirTelaMainPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainPessoaFisicaActivity.class);
        startActivity(intent);
    }
}