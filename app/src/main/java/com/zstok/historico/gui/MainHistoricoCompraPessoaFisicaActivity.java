package com.zstok.historico.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.zstok.historico.adapter.HistoricoListHolder;
import com.zstok.historico.dominio.Historico;
import com.zstok.infraestrutura.gui.LoginActivity;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.negociacao.gui.MainNegociacaoPessoaFisicaActivity;
import com.zstok.perfil.gui.PerfilPessoaFisicaActivity;
import com.zstok.pessoaFisica.gui.MainPessoaFisicaActivity;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainHistoricoCompraPessoaFisicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog alertaSair;

    private TextView tvNomeUsuarioNavHeader;
    private TextView tvEmailUsuarioNavHeader;
    private CircleImageView cvNavHeaderPessoa;

    private FirebaseRecyclerAdapter adapterHistorico;
    private RecyclerView recyclerViewHistorico;

    private NavigationView navigationView;

    private ProgressDialog progressDialog;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historico_compra_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Resgatando usuario atual
        user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

        //Instanciando progress dialog
        progressDialog = new ProgressDialog(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Instanciando recyler view
        recyclerViewHistorico = findViewById(R.id.recyclerHistoricoCompraPessoaFisica);
        recyclerViewHistorico.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainHistoricoCompraPessoaFisicaActivity.this);
        recyclerViewHistorico.setLayoutManager(layoutManager);

        //Criando adapter
        verificarQuery();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Instanciando views do menu lateral
        instanciandoViews();

        //Carregar dados do menu lateral
        setDadosMenuLateral();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_meu_perfil_pessoa_fisica:
                        abrirTelaMeuPerfilPessoaFisicaActivity();
                        return true;
                    case R.id.nav_negociacao_pessoa_fisica:
                        //Intent para tela de negocicao
                        abrirTelaMainNegociacaoActivity();
                        return true;
                    case R.id.nav_produtos_pessoa_fisica:
                        abrirTelaMainPessoaFisicaActivity();
                        return true;
                    case R.id.nav_meu_historico_compra_pessoa_fisica:
                        drawer.closeDrawers();
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
    //Método que instancia as views
    private void instanciandoViews(){
        View headerView = navigationView.getHeaderView(0);
        tvNomeUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderNome);
        tvEmailUsuarioNavHeader = headerView.findViewById(R.id.tvNavHeaderEmail);
        cvNavHeaderPessoa = headerView.findViewById(R.id.cvNavHeaderPessoa);
    }
    //Método que carrega nome e email do usuário e seta nas views do menu lateral
    private void setDadosMenuLateral(){
        if (user.getPhotoUrl() != null){
            Glide.with(MainHistoricoCompraPessoaFisicaActivity.this).load(user.getPhotoUrl()).into(cvNavHeaderPessoa);
        }else {
            cvNavHeaderPessoa.setImageResource(R.drawable.ic_sem_foto);
        }
        tvNomeUsuarioNavHeader.setText(user.getDisplayName());
        tvEmailUsuarioNavHeader.setText(user.getEmail());
    }
    //Método que inicia o progress dialog
    private void iniciarProgressDialog() {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.zs_titulo_progress_dialog_carregar_historico_compra));
        progressDialog.show();
    }
    //Verificando query
    private void verificarQuery(){
        iniciarProgressDialog();
        DatabaseReference referenciaHistorico = FirebaseController.getFirebase().child("historico");
        final Query queryHistoricoCompra = referenciaHistorico.orderByChild("idPessoaFisica").equalTo(FirebaseController.getUidUser());

        queryHistoricoCompra.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    criarAdapterHistorico(queryHistoricoCompra);
                }else {
                    Helper.criarToast(getApplicationContext(), "HISTÓRICO VAZIO!");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que cria o adapter de histórico
    private void criarAdapterHistorico(final Query queryHistoricoCompra) {
        adapterHistorico = new FirebaseRecyclerAdapter<Historico, HistoricoListHolder>(
                Historico.class,
                R.layout.card_historico,
                HistoricoListHolder.class,
                queryHistoricoCompra) {

            @Override
            protected void populateViewHolder(final HistoricoListHolder viewHolder, final Historico model, int position) {
                viewHolder.mainLayout.setVisibility(View.VISIBLE);
                viewHolder.linearLayout.setVisibility(View.VISIBLE);
                viewHolder.tvCardViewTotalCompra.setText(NumberFormat.getCurrencyInstance().format(model.getTotal()));
                viewHolder.tvCardViewDataCompra.setText(String.valueOf(model.getDataFim()));

                //Método que resgata o cnpj da pessoa jurídica
                resgatarCpnjPessoaJuridica(viewHolder, model);
            }

            @NonNull
            @Override
            public HistoricoListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                final HistoricoListHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnItemClickListener(new HistoricoListHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Historico historico =  (Historico) adapterHistorico.getItem(position);
                        abrirTelaVisualizarHistoricoActivity(historico);
                    }
                });
                return viewHolder;
            }
        };
        recyclerViewHistorico.setAdapter(adapterHistorico);
    }

    //Resgatando o cnpj envolvido na compra
    private void resgatarCpnjPessoaJuridica(final HistoricoListHolder viewHolder, final Historico model) {
        FirebaseController.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nomeEmpresa = dataSnapshot.child("pessoa").child(model.getIdPessoaJuridica()).child("nome").getValue(String.class);
                viewHolder.tvCardViewNome.setText(nomeEmpresa);
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
    //Intent para tela main
    private void abrirTelaMainPessoaFisicaActivity() {
        Intent intent = new Intent(getApplicationContext(), MainPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de perfil pessoa física
    private void abrirTelaMeuPerfilPessoaFisicaActivity(){
        Intent intent = new Intent(getApplicationContext(), PerfilPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela visualizar histórico
    private void abrirTelaVisualizarHistoricoActivity(Historico historico){
        Intent intent = new Intent(getApplicationContext(), VisualizarHistoricoActivity.class);
        intent.putExtra("idHistorico", historico.getIdHistorico());
        intent.putExtra("idEmpresa", historico.getIdPessoaJuridica());
        intent.putExtra("idPessoaFisica", historico.getIdPessoaFisica());
        startActivity(intent);
    }
    //Intent para a tela de negociação
    private void abrirTelaMainNegociacaoActivity(){
        Intent intent = new Intent(getApplicationContext(), MainNegociacaoPessoaFisicaActivity.class);
        startActivity(intent);
    }
    //Intent para a tela de login
    private void abrirTelaLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}