package com.zstok.produto.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.historico.gui.MainHistoricoVendaPessoaJuridicaActivity;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.perfil.gui.PerfilPessoaJuridicaActivity;
import com.zstok.negociacao.gui.MainNegociacaoPessoaJuridicaActivity;
import com.zstok.produto.adapter.ProdutoListHolder;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.negocio.ProdutoServices;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeusProdutosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog alertaSair;

    private EditText edtPesquisaProdutoPessoaJuridica;
    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;
    private NavigationView navigationView;

    private RecyclerView recylerViewMeusprodutos;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressDialog progressDialog;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgantado usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando views
        edtPesquisaProdutoPessoaJuridica = findViewById(R.id.edtPesquisaProdutoPessoaJuridica);
        Button btnPesquisaProdutoPessoaJuridica = findViewById(R.id.btnPesquisaProdutoPessoaJuridica);

        btnPesquisaProdutoPessoaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criandoAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaJuridica.getText().toString().toLowerCase()));
            }
        });

        //Evento de pesquisa
        edtPesquisaProdutoPessoaJuridica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPesquisaProdutoPessoaJuridica.getText().toString().isEmpty() ||
                        edtPesquisaProdutoPessoaJuridica.getText().toString().trim().length() == 0){
                    verificarQuery();
                }else {
                    criandoAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaJuridica.getText().toString().toLowerCase()));
                }
            }
        });

        FloatingActionButton fabCadastrarProduto = findViewById(R.id.fabCadastrarProduto);
        fabCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastrarProdutoActivity();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recylerViewMeusprodutos = findViewById(R.id.recyclerProdutosPessoaJuridica);
        recylerViewMeusprodutos.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MeusProdutosActivity.this);
        recylerViewMeusprodutos.setLayoutManager(layoutManager);

        //Criando o adapter
        verificarQuery();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do navigation header (menu lateral)
        instanciandoView();

        //Resgatando informações do menu lateral
        setDadosMenuLateral();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_juridica:
                        abrirTelaPerfilPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_juridica:
                        abrirTelaMainPessoaJuridicaActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_juridica:
                        drawer.closeDrawers();
                        //Função abrir tela produtos
                        return true;
                    case R.id.nav_meu_historico_vendas_pessoa_juridica:
                        //Função abrir tela histórico de vendas
                        abrirTelaMainHistoricoVendaPessoaJuridicaActivity();
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
    //Montando adapter e jogando no list holder
    private void criandoAdapterPesquisa(String pesquisa) {
        final DatabaseReference databaseReference = FirebaseController.getFirebase().child("produto");
        Query query = databaseReference.orderByChild("nomePesquisa").startAt(pesquisa).endAt(pesquisa+"\uf8ff");

        if (query != null) {
            FirebaseRecyclerAdapter adapterPesquisa = new FirebaseRecyclerAdapter<Produto, ProdutoListHolder>(
                    Produto.class,
                    R.layout.card_produto,
                    ProdutoListHolder.class,
                    query) {

                @Override
                protected void populateViewHolder(final ProdutoListHolder viewHolder, final Produto model, int position) {
                    viewHolder.mainLayout.setVisibility(View.VISIBLE);
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    viewHolder.tvCardViewNomeProduto.setText(model.getNome());
                    viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(model.getPrecoSugerido()));
                    viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                    viewHolder.tvCardViewNomeEmpresa.setText(user.getDisplayName());
                    if (model.getUrlImagem() != null) {
                        Glide.with(getApplicationContext()).load(model.getUrlImagem()).into(viewHolder.imgCardViewProduto);
                    } else {
                        viewHolder.imgCardViewProduto.setImageResource(R.drawable.ic_produtos);

                    }
                }

                @NonNull
                @Override
                public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                    final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Produto produto = (Produto) adapter.getItem(position);
                            dialogoProduto(produto);
                        }
                    });
                    return viewHolder;
                }
            };
            recylerViewMeusprodutos.setAdapter(adapterPesquisa);
        }
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_produto));
        progressDialog.show();
    }
    //Método que cria o adapter de histórico
    private void verificarQuery(){
        iniciarProgressDialog();
        DatabaseReference databaseReference = FirebaseController.getFirebase().child("produto");
        final Query queryProdutos = databaseReference.orderByChild("idEmpresa").equalTo(FirebaseController.getUidUser());
        //Verificando query
        queryProdutos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    criandoAdapter(queryProdutos);
                }else {
                    Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_produto_vazio));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Montando adapter e jogando no list holder
    private void criandoAdapter(Query query) {
        adapter = new FirebaseRecyclerAdapter<Produto, ProdutoListHolder>(
                Produto.class,
                R.layout.card_produto,
                ProdutoListHolder.class,
                query) {

            @Override
            protected void populateViewHolder(final ProdutoListHolder viewHolder, final Produto model, int position) {
                viewHolder.mainLayout.setVisibility(View.VISIBLE);
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.tvCardViewNomeProduto.setText(model.getNome());
                viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(model.getPrecoSugerido()));
                viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                viewHolder.tvCardViewNomeEmpresa.setText(user.getDisplayName());
                if (model.getUrlImagem() != null) {
                    Glide.with(MeusProdutosActivity.this).load(Uri.parse(model.getUrlImagem())).into(viewHolder.imgCardViewProduto);
                    progressDialog.dismiss();
                } else {
                    viewHolder.imgCardViewProduto.setImageResource(R.drawable.ic_produtos);
                    progressDialog.dismiss();
                }
            }

            @NonNull
            @Override
            public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produto = (Produto) adapter.getItem(position);
                        dialogoProduto(produto);
                    }
                });
                return viewHolder;
            }
        };
        recylerViewMeusprodutos.setAdapter(adapter);
    }
    //Método que exibe a caixa de diálogo para o aluno confirmar ou não a sua saída da turma
    private void dialogoProduto (final Produto produto) {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Define o titulo
        builder.setTitle(getString(R.string.zs_dialogo_titulo_produto));
        //Define a mensagem
        builder.setMessage(getString(R.string.zs_dialogo_mensagem_produto));
        //Define a opção de editar produto
        builder.setPositiveButton(getString(R.string.zs_dialogo_editar_produto), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                abrirTelaEditarProdutoActivity(produto);
            }
        });
        //Define a opção de excluir produto
        builder.setNegativeButton(getString(R.string.zs_dialogo_excluir), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                excluirProduto(produto);
            }
        });
        //Cria o AlertDialog
        AlertDialog alertaProduto = builder.create();
        //Exibe
        alertaProduto.show();
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
    //Chamando camada de negócio para excluir produto
    private void excluirProduto(Produto produto){
        if (ProdutoServices.excluirProduto(produto)){
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_produto_excluido_sucesso));
        }else {
            Helper.criarToast(getApplicationContext(), getString(R.string.zs_excecao_database));
        }
    }
    //Método que instancia as views
    private void instanciandoView(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega nome e email do usuário e seta nas views do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
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
    //Intent para a tela de cadastro do produto
    private void abrirTelaCadastrarProdutoActivity(){
        Intent intent = new Intent(getApplicationContext(), CadastrarProdutoActivity.class);
        startActivity(intent);
    }
    //Intent para a tela onde estará as negociações
    private void abrirTelaMainPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de perfil pessoa jurídica
    private void abrirTelaPerfilPessoaJuridicaActivity() {
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de editar produto
    private void abrirTelaEditarProdutoActivity(Produto produto){
        Intent intent = new Intent(getApplicationContext(), EditarProdutoActivity.class);
        intent.putExtra("idProduto", produto.getIdProduto());
        startActivity(intent);
    }
    //Intent para a tela de histórico pessoa jurídica
    private void abrirTelaMainHistoricoVendaPessoaJuridicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoVendaPessoaJuridicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}