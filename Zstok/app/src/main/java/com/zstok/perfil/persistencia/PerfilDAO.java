package com.zstok.perfil.persistencia;

import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;

public class PerfilDAO {
    public static void setNomeEmailView(final NavigationView navigationView, final FirebaseUser user){

        //Consultando banco de dados para resgatar o nome e email
        final DatabaseReference referencia = FirebaseController.getFirebase();

        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pessoa pessoa = dataSnapshot.child("pessoa").child(user.getUid()).getValue(Pessoa.class);
                setUserName(navigationView , pessoa.getNome());
                setUserEmail(navigationView, user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Método que seta o email para o TextView id: txt_nav_UserEmail
    private static void setUserEmail(NavigationView navView, String email){
        View headerView = navView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.tvNavHeaderEmail);
        userEmail.setText(email);
    }
    //Método que seta o nome para o TextView id: txt_nav_UserName
    private static void setUserName(NavigationView navView, String nome){
        View headerView = navView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.tvNavHeaderNome);
        userName.setText(nome);
    }

    public static boolean insereNome(String novoNome){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("nome").setValue(novoNome);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    public static boolean insereEmail(final String novoEmail, String senha) {
        boolean verificador = true;

        try {
            FirebaseController.getFirebaseAuthentication().getCurrentUser().updateEmail(novoEmail);
            /*
            <<Reautenticação>>
            FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), senha);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
            */

        } catch (DatabaseException e) {
            verificador = false;
        }
        return verificador;
    }
    public static boolean insereTelefone(String novoTelefone){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("telefone").setValue(novoTelefone);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    public static boolean insereEndereco(Pessoa pessoa){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("endereco").setValue(pessoa.getEndereco());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    public static boolean insereCpf(PessoaFisica pessoaFisica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).child("cpf").setValue(pessoaFisica.getCpf());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    public static boolean insereDataNascimento(PessoaFisica pessoaFisica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).child("dataNascimento").setValue(pessoaFisica.getDataNascimento());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}
