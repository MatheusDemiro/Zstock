package com.zstok.pessoaFisica.gui;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.zstok.carrinhoCompra.gui.CarrinhoCompraActivity;
import com.zstok.R;
import com.zstok.historico.gui.MainHistoricoCompraPessoaFisicaActivity;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.negociacao.gui.MainNegociacaoPessoaFisicaActivity;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.produto.adapter.ProdutoListHolder;
import com.zstok.produto.dominio.Produto;
import com.zstok.produto.gui.VisualizarProdutoActivity;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainPessoaFisicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;
    private Spinner spnFiltroPesquisaPessoaFisica;

    private NavigationView navigationView;
    private AlertDialog alertaSair;

    private EditText edtPesquisaProdutoPessoaFisica;

    private RecyclerView recylerViewMeusprodutos;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter adapter;

    private ProgressDialog progressDialog;

    private FirebaseUser user;

    private DatabaseReference databaseReference = FirebaseController.getFirebase().child("produto");

    private Query queryFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgantado usuário atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        //Instanciando views
        edtPesquisaProdutoPessoaFisica = findViewById(R.id.edtPesquisaProdutoPessoaFisica);
        spnFiltroPesquisaPessoaFisica = findViewById(R.id.spnFiltroPesquisaPessoaFisica);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCarrinhoCompra);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCarrinhoCompraActivity();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recylerViewMeusprodutos = findViewById(R.id.recyclerProdutosPessoaFisica);
        recylerViewMeusprodutos.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recylerViewMeusprodutos.setLayoutManager(layoutManager);

        //Criando o adapter
        criarAdapterProduto(databaseReference.orderByKey());

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do menu lateral
        instanciandoViews();

        //Carregando informações do menu lateral
        setDadosMenuLateral();

        spnFiltroPesquisaPessoaFisica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                criarFiltro();
                edtPesquisaProdutoPessoaFisica.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Evento de pesquisa
        edtPesquisaProdutoPessoaFisica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPesquisaProdutoPessoaFisica.getText().toString().isEmpty() ||
                        edtPesquisaProdutoPessoaFisica.getText().toString().trim().length() == 0){
                    criarFiltro();
                }else {
                    criarAdapterPesquisa(Helper.removerAcentos(edtPesquisaProdutoPessoaFisica.getText().toString().toLowerCase()));
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_fisica:
                        abrirTelaPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_fisica:
                        //Função abrir tela de negociações em
                        abrirTelaMainNegociacaoActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_fisica:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_meu_historico_compra_pessoa_fisica:
                        //Função abrir tela histórico de vendas
                        abrirTelaMainHistoricoVendaPessoaFisicaActivity();
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
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_produto));
        progressDialog.show();
    }
    //Montando adapter e jogando no list holder
    private void criarFiltro() {
        switch (spnFiltroPesquisaPessoaFisica.getSelectedItem().toString()) {
            case "Selecione o tipo de filtro":
                queryFiltro = databaseReference.orderByKey();
                normalizarLayoutRecyclerView();
                break;
            case "Preço: Menor-Maior":
                queryFiltro = databaseReference.orderByChild("precoSugerido");
                normalizarLayoutRecyclerView();
                break;
            case "Preço: Maior-Menor":
                queryFiltro = databaseReference.orderByChild("precoSugerido");
                reverteLayoutRecyclerView();
                break;
        }
        verificarQuery(queryFiltro);
    }
    //Revertendo o layout do recycler view
    private void reverteLayoutRecyclerView() {
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
    }
    //Normalizando layout do recycler view
    private void normalizarLayoutRecyclerView(){
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
    }
    //Método que cria o adapter de histórico
    private void verificarQuery(final Query queryFiltro){
        iniciarProgressDialog();
        //Verificando query
        queryFiltro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    criarAdapterProduto(queryFiltro);
                }else {
                    Helper.criarToast(getApplicationContext(), "Sem produtos cadastrado no sistema!");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que preenche o recycler adapter com os produtos
    private void criarAdapterProduto(Query query) {
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
                if (model.getQuantidadeEstoque() != 0) {
                    viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                } else {
                    viewHolder.tvCardViewQuantidadeEstoque.setText(getString(R.string.zs_excecao_produto_esgotado));
                }
                if (model.getUrlImagem() != null) {
                    Glide.with(MainPessoaFisicaActivity.this).load(Uri.parse(model.getUrlImagem())).into(viewHolder.imgCardViewProduto);
                } else {
                    viewHolder.imgCardViewProduto.setImageResource(R.drawable.ic_produtos);
                }
                //Resgatando nome da pessoa jurídica
                resgatarNomeEmpresa(viewHolder, model);
            }

            @NonNull
            @Override
            public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produto = (Produto) adapter.getItem(position);
                        abrirTelaProdutoActivity(produto.getIdEmpresa(), produto.getIdProduto());
                    }
                });
                return viewHolder;
            }
        };
        recylerViewMeusprodutos.setAdapter(adapter);
    }
    //Montando adapter e jogando no list holder
    private void criarAdapterPesquisa(String pesquisa) {
        Query queryPesquisa = databaseReference.orderByChild("nomePesquisa").startAt(pesquisa).endAt(pesquisa+"\uf8ff");
        FirebaseRecyclerAdapter adapter1 = new FirebaseRecyclerAdapter<Produto, ProdutoListHolder>(
                Produto.class,
                R.layout.card_produto,
                ProdutoListHolder.class,
                queryPesquisa) {

            @Override
            protected void populateViewHolder(final ProdutoListHolder viewHolder, final Produto model, int position) {
                getItemCount();
                viewHolder.mainLayout.setVisibility(View.VISIBLE);
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.tvCardViewNomeProduto.setText(model.getNome());
                viewHolder.tvCardViewPrecoProduto.setText(NumberFormat.getCurrencyInstance().format(model.getPrecoSugerido()));
                viewHolder.tvCardViewQuantidadeEstoque.setText(String.valueOf(model.getQuantidadeEstoque()));
                if (model.getUrlImagem() != null) {
                    Glide.with(getApplicationContext()).load(Uri.parse(model.getUrlImagem())).into(viewHolder.imgCardViewProduto);
                }
                resgatarNomeEmpresa(viewHolder, model);
            }

            @NonNull
            @Override
            public ProdutoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                final ProdutoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnItemClickListener(new ProdutoListHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produto = (Produto) adapter.getItem(position);
                        abrirTelaProdutoActivity(produto.getIdEmpresa(), produto.getIdProduto());
                    }
                });
                return viewHolder;
            }
        };
        recylerViewMeusprodutos.setAdapter(adapter1);
    }
    //Método que resgata nome da empresa do banco
    private void resgatarNomeEmpresa(final ProdutoListHolder viewHolder, Produto model) {
        FirebaseController.getFirebase().child("pessoa").child(model.getIdEmpresa()).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.tvCardViewNomeEmpresa.setText(dataSnapshot.getValue(String.class));
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    //Método que instancia as views
    private void instanciandoViews(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega as informações do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null) {
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
    //Intent para a tela de perfil pessoa física
    private void abrirTelaPerfilPessoaFisicaActivity() {
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaProdutoActivity(String idEmpresa, String idProduto) {
        Intent intent = new Intent(getApplicationContext(), VisualizarProdutoActivity.class);
        intent.putExtra("idEmpresa", idEmpresa);
        intent.putExtra("idProduto", idProduto);
        startActivity(intent);
    }
    //Intent para a tela de carrinho de compra
    private void abrirTelaCarrinhoCompraActivity() {
        Intent intent = new Intent(getApplicationContext(), CarrinhoCompraActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de histórico pessoa física
    private void abrirTelaMainHistoricoVendaPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), MainHistoricoCompraPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de negociação
    private void abrirTelaMainNegociacaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoPessoaFisicaActivity.class);
        startActivity(intent);
    }
}