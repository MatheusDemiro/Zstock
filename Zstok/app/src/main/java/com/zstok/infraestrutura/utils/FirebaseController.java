package com.zstok.infraestrutura.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseController {
    private static DatabaseReference referenceDatabase;
    private static FirebaseAuth referenceAuthentication;

    public static DatabaseReference getFirebase(){
        if (referenceDatabase == null){
            referenceDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceDatabase;
    }
    public static FirebaseAuth getFirebaseAuthentication(){
        if (referenceAuthentication == null){
            referenceAuthentication = FirebaseAuth.getInstance();
        }
        return referenceAuthentication;
    }
    public static String getUidUser(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
