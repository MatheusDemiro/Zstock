package com.zstok.perfil.persistencia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.R;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.infraestrutura.utils.Helper;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilDAO {

    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private static String uriImagemPerfil;

    //Inserindo imagem no banco
    public static void insereFoto(Uri uriFoto) {
        if (uriFoto != null) {
            StorageReference ref = storageReference.child("images/perfil/" + FirebaseController.getUidUser() + ".bmp");
            ref.putFile(uriFoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uriImagemPerfil = taskSnapshot.getDownloadUrl().toString();
                    FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();
                    if (user != null && uriImagemPerfil != null) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(uriImagemPerfil))
                                .build();
                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }
            });
        }
    }
    //Resgatando foto do Storage
    public static void resgatarFoto(final CircleImageView circleImageView){
        StorageReference ref = storageReference.child("images/perfil/" +
                FirebaseController.getUidUser() + ".bmp");

        try {
            final File localFile = File.createTempFile("images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap minhaFoto = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    circleImageView.setImageBitmap(minhaFoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            Log.d("ERRO", e.getMessage());
        }
    }
    public static void setDadosNavHeader(final FirebaseUser user, final TextView tvNomeUsuarioNavHeader, final TextView tvEmailUsuarioNavHeader){

        //Consultando banco de dados para resgatar o nome e email
        final DatabaseReference referencia = FirebaseController.getFirebase();

        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nomeUsuario = dataSnapshot.child("pessoa").child(user.getUid()).child("nome").getValue(String.class);
                tvNomeUsuarioNavHeader.setText(nomeUsuario);
                tvEmailUsuarioNavHeader.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    public static boolean insereEmail(final String novoEmail) {
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
    public static boolean insereCnpj(PessoaJuridica pessoaJuridica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaJuridica").child(FirebaseController.getUidUser()).child("cnpj").setValue(pessoaJuridica.getCnpj());
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

    public static boolean insereRazaoSocial(String razaoSocial){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaJuridica").child(FirebaseController.getUidUser()).child("razaoSocial").setValue(razaoSocial);
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }

}
/*//Método que seta o email para o TextView id: txt_nav_UserEmail
    private static void setEmailUsuario(NavigationView navView, String email){
        View headerView = navView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.tvNavHeaderEmail);
        userEmail.setText(email);
    }
    //Método que seta o nome para o TextView id: txt_nav_UserName
    private static void setNomeUsuario(NavigationView navView, String nome){
        View headerView = navView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.tvNavHeaderNome);
        userName.setText(nome);
    }
    //Método que seta a foto do menu lateral
    private static void setImagemNavHeader(NavigationView navView, Bitmap image) {
        if(image!=null){
            View headerView = navView.getHeaderView(0);
            ImageView imagemPerfilLateral = headerView.findViewById(R.id.cvNavHeaderPessoa);
            imagemPerfilLateral.setImageBitmap(image);
        }
    }*/
