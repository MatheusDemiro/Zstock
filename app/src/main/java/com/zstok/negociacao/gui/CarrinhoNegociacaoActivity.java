package com.zstok.negociacao.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.ItemCompraListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.historico.gui.MainHistoricoCompraPessoaFisicaActivity;
import com.zstok.historico.negocio.HistoricoServices;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.infraestrutura.utils.MoneyTextWatcher;
import com.zstok.infraestrutura.utils.VerificaConexao;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.dominio.Negociacao;
import com.zstok.negociacao.negocio.NegociacaoServices;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CarrinhoNegociacaoActivity extends AppCompatActivity {

    private TextView tvTotalCaixaDialogo;
    private TextView tvTotalDescontoCaixaDialogo;
    private EditText edtDescontoCaixaDialogo;
    private Button btnGerarDescontoCaixaDialogo;
    private TextView tvTotalCarrinhoNegociacao;
    private TextView tvCancelarNegociacao;
    private Button btnCarrinhoNegociacao;

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerViewItens;

    private VerificaConexao verificaConexao;

    private AlertDialog alertaDesconto;

    private String idNegociacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_negociacao);

        //Resgatando ID passada pela intent
        idNegociacao = getIntent().getStringExtra("idNegociacao");

        //Criando instância da class "VerificarConexao"
        verificaConexao = new VerificaConexao(this);

        //Instanciando views
        tvTotalCarrinhoNegociacao = findViewById(R.id.tvTotalCarrinhoNegociacao);
        btnCarrinhoNegociacao = findViewById(R.id.btnCarrinhoNegociacao);
        tvCancelarNegociacao = findViewById(R.id.tvCancelarNegociacao);

        tvCancelarNegociacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()) {
                    cancelarNegocicao();
                }
            }
        });

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCarrinhoNecogiacao);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CarrinhoNegociacaoActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        //Identificando o tipo de pessoa e executando
        verificarConta();

        //Verifica itens excluidos enquanto usuário estava off
        verificarItensRemovidos();
        atualizarTotalCarrinho();

        //Resgatando total do banco
        resgatandoTotal();

        FirebaseController.getFirebase().child("produto").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                atualizarCarrinhoNegociacao(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        criarAdapter();
    }
    //Cancelando negociação
    private void cancelarNegocicao(){
        if (NegociacaoServices.limparNegociacao(idNegociacao)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_negociacao_cancelada_sucesso));
            abrirTelaMainNegociacaoPessoaFisicaActivity();
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Verificando tipo e gerenciando o onClick do botão
    private void verificarConta(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setClickBotaoCarrinhoNegociacao(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Tratando os eventos de click do botão
    private void setClickBotaoCarrinhoNegociacao(DataSnapshot dataSnapshot) {
        if (verificaConexao.isConected()) {
            if (dataSnapshot.child("pessoaJuridica").child(FirebaseController.getUidUser()).exists()) {
                clickGerarDesconto();
            } else {
                clickFinalizarNegociacao();
            }
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Evento de click para gerar desconto
    private void clickGerarDesconto() {
        btnCarrinhoNegociacao.setText(getString(R.string.zs_btn_gerar_desconto_carrinho_negociacao));
        tvCancelarNegociacao.setVisibility(View.GONE);
        btnCarrinhoNegociacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir caixa de diálogo
                gerarDesconto();
            }
        });
    }
    //Evento de click para finalizar negocição
    private void clickFinalizarNegociacao() {
        btnCarrinhoNegociacao.setText(getString(R.string.zs_btn_finalizar_negociacao));
        btnCarrinhoNegociacao.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                fecharNegociacao();
            }
        });
    }
    //Resgatando total do banco
    private void resgatandoTotal() {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Negociacao negociacao = dataSnapshot.child("negociacao").child(idNegociacao).getValue(Negociacao.class);
                if (negociacao != null) {
                    tvTotalCarrinhoNegociacao.setText(NumberFormat.getCurrencyInstance().format(negociacao.getTotal()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que atualiza carrinho compra
    private void atualizarCarrinhoNegociacao(final String idProdutoAlterado){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("negociacao").child(idNegociacao).exists()) {
                    Negociacao negociacao = dataSnapshot.child("negociacao").child(idNegociacao).getValue(Negociacao.class);
                    Produto produto = dataSnapshot.child("produto").child(idProdutoAlterado).getValue(Produto.class);
                    if (produto != null && negociacao != null) {
                        resgatarItensComprasCarrinho(produto, negociacao, idProdutoAlterado);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que resgata todos os itens do carrinho de compra
    private void resgatarItensComprasCarrinho(Produto produto, Negociacao negociacao, String idProdutoAlterado) {
        int chave = 0;
        for (ItemCompra itemCompra: negociacao.getCarrinhoAtual()){
            if (itemCompra.getIdProduto().equals(idProdutoAlterado)){
                if (alterarValorItemCarrinho(produto, String.valueOf(chave))){
                    inserirTotal(produto, itemCompra, negociacao.getTotal());
                    resgatandoTotal();
                    criarAdapter();
                    break;
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                    break;
                }
            }
            chave++;
        }

    }
    //Recalculando total e inserindo no banco
    private void inserirTotal(Produto produto, ItemCompra itemCompra, double total){
        if (produto.getPrecoSugerido() != itemCompra.getValor()){
            double novoTotal = total - (itemCompra.getValor()*itemCompra.getQuantidade()) + (produto.getPrecoSugerido()*itemCompra.getQuantidade());
            NegociacaoServices.inserirTotal(idNegociacao, novoTotal);
        }
    }
    private boolean alterarValorItemCarrinho(Produto produto, String chave){
        return NegociacaoServices.alterarItemCarrinho(produto, idNegociacao, chave);
    }
    //Atualizando total do carrinho
    private void atualizarTotalCarrinho(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).exists()){
                    Double totalAntigo = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").getValue(Double.class);
                    Iterable<DataSnapshot> itensCompra = dataSnapshot.child("carrinhoCompra").child(FirebaseController.getUidUser()).child("itensCompra").getChildren();
                    for (DataSnapshot itensCompraCarrinhoUser: itensCompra){
                        ItemCompra itemCompra = itensCompraCarrinhoUser.getValue(ItemCompra.class);
                        if (dataSnapshot.child("produtoExcluido").child(itemCompra.getIdProduto()).exists()) {
                            Produto produto = dataSnapshot.child("produtoExcluido").child(itemCompra.getIdProduto()).getValue(Produto.class);
                            if (itemCompra.getIdProduto().equals(produto.getIdProduto())) {
                                double novoTotal = totalAntigo - (itemCompra.getValor() * itemCompra.getQuantidade());
                                FirebaseController.getFirebase().child("carrinhoCompra").child(FirebaseController.getUidUser()).child("total").setValue(novoTotal);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Verificando se algum item que está no carrinho foi removido do sistema
    private void verificarItensRemovidos(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int chave = 0;
                Negociacao negociacao = dataSnapshot.child("negociacao").child(idNegociacao).getValue(Negociacao.class);
                for (ItemCompra itemCompra: negociacao.getCarrinhoAtual()){
                    if (dataSnapshot.child("produtoExcluido").child(itemCompra.getIdProduto()).exists()){
                        //Remover item do carrinho
                        remorItemInativoCarrinho(String.valueOf(chave));
                    }
                    chave++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Gerando desconto
    private void gerarDesconto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CarrinhoNegociacaoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.modelo_caixa_dialogo_desconto, null);
        builder.setView(mView);

        //Instanciando views da caixa de diálogo
        instanciandoViews(mView);

        //Setando informações referentes a caixa de diálogo
        setarInformacoesViews();

        //Gerando
        alertaDesconto = builder.create();
        alertaDesconto.show();

        clickAplicarDesconto();
    }
    //Validando EditText da caixa de diálogo
    private boolean validarCampo(){
        boolean verificador = true;

        if (edtDescontoCaixaDialogo.getText().toString().isEmpty() || edtDescontoCaixaDialogo.getText().toString().trim().length() == 0){
            edtDescontoCaixaDialogo.setError(getString(R.string.zs_excecao_campo_vazio));
            verificador = false;
        }
        return verificador;
    }
    //Método que executa o evento de click do botão gerar desconto
    private void clickAplicarDesconto(){
        btnGerarDescontoCaixaDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao.isConected()){
                    if (validarCampo()) {
                        double totalDesconto = MoneyTextWatcher.convertToBigDecimal(tvTotalDescontoCaixaDialogo.getText().toString()).doubleValue();
                        if (NegociacaoServices.inserirTotal(idNegociacao, totalDesconto)){
                            alertaDesconto.dismiss();
                        }else {
                            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
                        }
                    }
                }
                resgatandoTotal();
            }
        });
    }
    //Instanciando views da caixa de diálogo
    private void instanciandoViews(View mView){
        tvTotalCaixaDialogo = mView.findViewById(R.id.tvTotalCaixaDialogo);
        tvTotalDescontoCaixaDialogo = mView.findViewById(R.id.tvTotalDescontoCaixaDialogo);
        edtDescontoCaixaDialogo = mView.findViewById(R.id.edtDescontoCaixaDialogo);
        btnGerarDescontoCaixaDialogo = mView.findViewById(R.id.btnGerarDescontoCaixaDialogo);
    }
    //Setando informações iniciais
    private void setInformacoesIniciais(final double desconto) {
        double total = MoneyTextWatcher.convertToBigDecimal(tvTotalCarrinhoNegociacao.getText().toString()).doubleValue();
        double totalDesconto = total - (total*(desconto/100));
        tvTotalCaixaDialogo.setText(NumberFormat.getCurrencyInstance().format(total));
        tvTotalDescontoCaixaDialogo.setText(NumberFormat.getCurrencyInstance().format(totalDesconto));
    }
    //Setando informações
    private void setarInformacoesViews(){
        setInformacoesIniciais(0);
        edtDescontoCaixaDialogo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(edtDescontoCaixaDialogo.getText().toString().isEmpty() || edtDescontoCaixaDialogo.getText().toString().trim().length() == 0)){
                    setInformacoesIniciais(Double.valueOf(edtDescontoCaixaDialogo.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(edtDescontoCaixaDialogo.getText().toString().isEmpty() || edtDescontoCaixaDialogo.getText().toString().trim().length() == 0)){
                    if(!(Double.valueOf(edtDescontoCaixaDialogo.getText().toString())> 100)){
                        setInformacoesIniciais(Double.valueOf(edtDescontoCaixaDialogo.getText().toString()));
                    }else{
                        setInformacoesIniciais(100.00);
                    }
                }else {
                    tvTotalDescontoCaixaDialogo.setText(tvTotalCaixaDialogo.getText().toString());
                }
            }
        });
    }
    //Fechando negociação
    private void fecharNegociacao(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Negociacao negociacao = dataSnapshot.child("negociacao").child(idNegociacao).getValue(Negociacao.class);
                if (negociacao != null) {
                    negociacao.setDataFim(Helper.getData());
                    if (verificaQuantidade(dataSnapshot, negociacao)) {
                        gerarHistorico(negociacao);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Gerando histórico
    private void gerarHistorico(Negociacao negociacao){
        Historico historico = criarHistorico(negociacao);
        if (HistoricoServices.inserirHistoricoNegociacao(historico)){
            finalizarNegociacao();
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Finalizando negociação
    private void finalizarNegociacao() {
        if (NegociacaoServices.limparNegociacao(idNegociacao)) {
            abrirTelaMainHistoricoPessoaFisicaActivity();
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_negociacao_finalizada_sucesso));
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Criando o objeto histórico
    private Historico criarHistorico(Negociacao negociacao){
        Historico historico = new Historico();

        historico.setIdNegociacao(negociacao.getIdNegociacao());
        historico.setIdPessoaJuridica(negociacao.getIdPessoaJuridica());
        historico.setIdPessoaFisica(negociacao.getIdPessoaFisica());
        historico.setDataInicio(negociacao.getDataInicio());
        historico.setDataFim(Helper.getData());
        historico.setCarrinho((ArrayList<ItemCompra>) negociacao.getCarrinhoAtual());
        historico.setTotal(negociacao.getTotal());

        return historico;
    }
    //Verificando se a quantidade solicitada pelo usuário está disponível em estoque
    private boolean verificaQuantidade(DataSnapshot dataSnapshot, Negociacao negociacao) {
        //Validando quantidade
        for(ItemCompra itemCompra: negociacao.getCarrinhoAtual()){
            Produto produtoCompra = dataSnapshot.child("produto").child(itemCompra.getIdProduto()).getValue(Produto.class);
            if (itemCompra.getQuantidade() > produtoCompra.getQuantidadeEstoque() ) {
                Helper.criarToast(getApplicationContext(), "Quantidade de " + produtoCompra.getNome() + " indisponível!");
                return false;
            }else {
                //Diminuindo quando caso esteja disponível
                diminuirQuantidade(produtoCompra, itemCompra);
            }
        }
        return true;
    }
    //Chamando camada de negócio
    private void remorItemInativoCarrinho(String chave){
        NegociacaoServices.removerItemInativoCarrinho(idNegociacao, chave);
    }
    //Diminuindo quantidade produto
    private void diminuirQuantidade(Produto produto, ItemCompra itemCompra){
        NegociacaoServices.diminuirQuantidade(produto, itemCompra);
    }
    //Montando adapter e jogando no list holder
    private void criarAdapter() {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("negociacao").child(idNegociacao).child("carrinhoAtual");

        if (databaseReference != null) {

            adapter = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                    ItemCompra.class,
                    R.layout.card_item_compra,
                    ItemCompraListHolder.class,
                    databaseReference) {

                @Override
                protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                    FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                            if (produto != null) {
                                Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                                if (pessoa != null) {
                                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                                    viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(produto.getPrecoSugerido()));
                                    viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));
                                    viewHolder.tvCardViewNomeEmpresaItemCompra.setText(pessoa.getNome());
                                    if (produto.getUrlImagem() != null) {
                                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
                                    }else {
                                        viewHolder.imgCardViewItemCompra.setImageResource(R.drawable.ic_produtos);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                @NonNull
                @Override
                public ItemCompraListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final ItemCompraListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new ItemCompraListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            ItemCompra itemCompra = (ItemCompra) adapter.getItem(position);
                        }
                    });
                    return viewHolder;
                }
            };
            recyclerViewItens.setAdapter(adapter);
        }
    }
    //Intent para a tela main histórico da pessoa física
    private void abrirTelaMainHistoricoPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoCompraPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
    //Intent para a tela main negociação da pessoa física
    private void abrirTelaMainNegociacaoPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoPessoaFisicaActivity.class);
        startActivity(intent);
        finish();
    }
}