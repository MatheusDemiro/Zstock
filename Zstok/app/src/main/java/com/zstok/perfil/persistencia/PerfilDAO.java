package com.zstok.perfil.persistencia;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseException;
import com.zstok.infraestrutura.utils.FirebaseController;
import com.zstok.pessoa.dominio.Pessoa;
import com.zstok.pessoaFisica.dominio.PessoaFisica;
import com.zstok.pessoaJuridica.dominio.PessoaJuridica;

public class PerfilDAO {

    private static FirebaseUser user = FirebaseController.getFirebaseAuthentication().getCurrentUser();

    //Inserindo nome no banco
    public static boolean insereNome(String novoNome){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("nome").setValue(novoNome);
            if (user != null) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(novoNome)
                        .build();
                user.updateProfile(profileChangeRequest);
            }
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo email no banco
    public static boolean insereEmail(final String novoEmail) {
        boolean verificador = true;

        try {
            if (user != null){
                user.updateEmail(novoEmail);
            }
        } catch (DatabaseException e) {
            verificador = false;
        }
        return verificador;
    }
    //Inserindo telefone no banco
    public static boolean insereTelefone(Pessoa pessoa){
        boolean verificador;

        try {
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("telefone").setValue(pessoa.getTelefone());
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo endereço no banco
    public static boolean insereEndereco(Pessoa pessoa){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoa").child(FirebaseController.getUidUser()).child("endereco").setValue(pessoa.getEndereco());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo cpf no banco
    public static boolean insereCpf(PessoaFisica pessoaFisica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).child("cpf").setValue(pessoaFisica.getCpf());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo cnpj no banco
    public static boolean insereCnpj(PessoaJuridica pessoaJuridica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaJuridica").child(FirebaseController.getUidUser()).child("cnpj").setValue(pessoaJuridica.getCnpj());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo data de nascimento no banco
    public static boolean insereDataNascimento(PessoaFisica pessoaFisica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaFisica").child(FirebaseController.getUidUser()).child("dataNascimento").setValue(pessoaFisica.getDataNascimento());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo razão social no banco
    public static boolean insereRazaoSocial(PessoaJuridica pessoaJuridica){
        boolean verificador = true;

        try{
            FirebaseController.getFirebase().child("pessoaJuridica").child(FirebaseController.getUidUser()).child("razaoSocial").setValue(pessoaJuridica.getRazaoSocial());
        } catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
}