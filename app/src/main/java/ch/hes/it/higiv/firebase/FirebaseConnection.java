package ch.hes.it.higiv.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection{

    protected FirebaseDatabase mFirebaseDatabase;
    protected DatabaseReference mDatabaseReference;

    public void initFirebase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }


}
