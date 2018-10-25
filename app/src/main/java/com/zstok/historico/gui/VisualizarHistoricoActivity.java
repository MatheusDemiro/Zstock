package com.zstok.historico.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.adapter.ItemCompraListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.itemcompra.dominio.ItemCompra;
import com.zstok.negociacao.gui.VizualizarChatActivity;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.produto.dominio.Produto;

import java.text.NumberFormat;

public class VisualizarHistoricoActivity extends AppCompatActivity {

    private TextView tvNomeEmpresaVizualizarHistorico;
    private TextView tvDataCompraVizualizarHistorico;
    private TextView tvCpfVisualizarHistorico;
    private TextView tvCnpjVisualizarHistorico;
    private TextView tvTotalVisualizarHistorico;
    private Button btnVizualizarChat;

    private RecyclerView recyclerViewItens;
    private ProgressDialog progressDialog;

    private String idHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_historico_compra);

        //Resgatando id da intent
        idHistorico = getIntent().getStringExtra("idHistorico");

        //Habilitando visualização do chat caso trate-se de uma negociação

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando views
        tvNomeEmpresaVizualizarHistorico = findViewById(R.id.tvNomeEmpresaVizualizarHistorico);
        tvDataCompraVizualizarHistorico = findViewById(R.id.tvDataCompraVizualizarHistorico);
        tvCpfVisualizarHistorico =  findViewById(R.id.tvCpfVisualizarHistorico);
        tvCnpjVisualizarHistorico = findViewById(R.id.tvCnpjVisualizarHistorico);
        tvTotalVisualizarHistorico = findViewById(R.id.tvTotalVisualizarHistorico);
        btnVizualizarChat = findViewById(R.id.btnVizualizarChat);

        //Habilitar botão chat
        habilitarChat();

        //Aplicar máscara
        Helper.mascaraCnpj(tvCnpjVisualizarHistorico);
        Helper.mascaraCpf(tvCpfVisualizarHistorico);

        //Instanciando recyler view
        recyclerViewItens = findViewById(R.id.recyclerItensCompraHistorico);
        recyclerViewItens.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VisualizarHistoricoActivity.this);
        recyclerViewItens.setLayoutManager(layoutManager);

        //Setando informações do histórico
        setarViews();

        //Método que cria o adapter de itens compra
        verificarQuery();
    }
    //Evento de clique do botão visualizar chat
    private void clickVisualizarChat(final DataSnapshot dataSnapshot) {
        btnVizualizarChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idNegociacao = dataSnapshot.child("historico").child(idHistorico).child("idNegociacao").getValue(String.class);
                abrirTelaVizualizarNegociacao(idNegociacao);
            }
        });
    }
    //Método para habilitar chat caso seja uma negociação
    private void habilitarChat(){
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("historico").child(idHistorico).child("idNegociacao").exists()){
                    clickVisualizarChat(dataSnapshot);
                }else{
                    btnVizualizarChat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Intent para a tela de visuzalização da Negociação
    private void abrirTelaVizualizarNegociacao(String idNegociacao){
        Intent intent = new Intent(getApplicationContext(), VizualizarChatActivity.class);
        intent.putExtra("idNegociacao",idNegociacao);
        startActivity(intent);
    }
    //Verificando se a referência do banco existe
    private void verificarQuery() {
        final DatabaseReference refenrciaCarrinhoHistoricoCompra = FirebaseController.getFirebase().child("historico").child(idHistorico).child("carrinho");

        refenrciaCarrinhoHistoricoCompra.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    criarAdapterItensCompra(refenrciaCarrinhoHistoricoCompra);
                }else {
                    Helper.criarToast(getApplicationContext(), "HISTÓRICO VAZIO");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Montando adapter e jogando no list holder
    private void criarAdapterItensCompra(final DatabaseReference databaseReference) {
        FirebaseRecyclerAdapter adapterItensCompra = new FirebaseRecyclerAdapter<ItemCompra, ItemCompraListHolder>(
                ItemCompra.class,
                R.layout.card_item_compra,
                ItemCompraListHolder.class,
                databaseReference) {

            @Override
            protected void populateViewHolder(final ItemCompraListHolder viewHolder, final ItemCompra model, int position) {
                viewHolder.mainLayout.setVisibility(View.VISIBLE);
                viewHolder.linearLayout.setVisibility(View.VISIBLE);

                viewHolder.tvCardViewPrecoItemCompra.setText(NumberFormat.getCurrencyInstance().format(model.getValor()));
                viewHolder.tvCardViewQuantidadeItemCompra.setText(String.valueOf(model.getQuantidade()));

                resgatarInformacoes(viewHolder, model);

            }
        };
        recyclerViewItens.setAdapter(adapterItensCompra);
    }

    //Método que resgata as informações do banco (informações do produto e nome da empresa responsável por este)
    private void resgatarInformacoes(final ItemCompraListHolder viewHolder, final ItemCompra model) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produto produto = dataSnapshot.child("produto").child(model.getIdProduto()).getValue(Produto.class);
                if (produto != null) {
                    viewHolder.tvCardViewNomeItemCompra.setText(produto.getNome());
                    if (produto.getUrlImagem() != null) {
                        Glide.with(getApplicationContext()).load(produto.getUrlImagem()).into(viewHolder.imgCardViewItemCompra);
                    } else {
                        viewHolder.imgCardViewItemCompra.setImageResource(R.drawable.ic_produtos);
                    }
                    Pessoa pessoa = dataSnapshot.child("pessoa").child(produto.getIdEmpresa()).getValue(Pessoa.class);
                    if (pessoa != null) {
                        viewHolder.tvCardViewNomeEmpresaItemCompra.setText(pessoa.getNome());
                    }
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
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_visualizar_historico));
        progressDialog.show();
    }
    //Criando objeto histórico
    private Historico criarHistorico(){
        Historico historico = new Historico();

        historico.setIdPessoaJuridica(getIntent().getStringExtra("idEmpresa"));
        historico.setIdPessoaFisica(getIntent().getStringExtra("idPessoaFisica"));

        return historico;
    }
    private void setarViews(){
        iniciarProgressDialog();
        final Historico historico = criarHistorico();

        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvNomeEmpresaVizualizarHistorico.setText(dataSnapshot.child("pessoa").child(historico.getIdPessoaJuridica()).child("nome").getValue(String.class));
                tvCpfVisualizarHistorico.setText(dataSnapshot.child("pessoaFisica").child(historico.getIdPessoaFisica()).child("cpf").getValue(String.class));
                tvCnpjVisualizarHistorico.setText(dataSnapshot.child("pessoaJuridica").child(historico.getIdPessoaJuridica()).child("cnpj").getValue(String.class));
                tvTotalVisualizarHistorico.setText(NumberFormat.getCurrencyInstance().format(dataSnapshot.child("historico").child(idHistorico).child("total").getValue(Double.class)));
                tvDataCompraVizualizarHistorico.setText(dataSnapshot.child("historico").child(idHistorico).child("dataFim").getValue(String.class));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
