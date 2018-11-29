package ch.hes.it.higiv.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class FirebaseConnection{

    //Mother class called for Firebase connection manager.
    //handles the Firebase singleton

    protected FirebaseDatabase mFirebaseDatabase;
    protected static DatabaseReference mDatabaseReference;

    public FirebaseConnection(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

}
