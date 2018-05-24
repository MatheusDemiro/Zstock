package com.zstok.produto.persistencia;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zstok.infraestrutura.persistencia.FirebaseController;
import com.zstok.produto.dominio.Produto;

public class ProdutoDAO {

    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //Inserindo produto no banco
    public static boolean insereProduto(Uri uriFoto, Produto produto){
        boolean verificador;

        try {
            if (uriFoto != null){
                insereFoto(uriFoto, produto);
            }
            FirebaseController.getFirebase().child("produto").child(FirebaseController.getUidUser()).push().setValue(produto);
            verificador = true;
        }catch (DatabaseException e){
            verificador = false;
        }
        return verificador;
    }
    //Inserindo imagem no banco
    private static void insereFoto(Uri uriFoto, final Produto produto) {
        StorageReference ref = storageReference.child("images/produto/" + "/" + FirebaseController.getUidUser() + "/" + produto.getNomeProduto() + ".bmp");
        ref.putFile(uriFoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                produto.setUrlImagemProduto(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }
}
