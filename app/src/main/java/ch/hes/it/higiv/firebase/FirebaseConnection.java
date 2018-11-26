package ch.hes.it.higiv.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class FirebaseConnection{

    protected FirebaseDatabase mFirebaseDatabase;
    protected DatabaseReference mDatabaseReference;

    public FirebaseConnection(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

}
