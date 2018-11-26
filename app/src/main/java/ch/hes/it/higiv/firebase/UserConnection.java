package ch.hes.it.higiv.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.User;

public class UserConnection extends  FirebaseConnection {

    public UserConnection(){
        super();
    }

    public void editUser(User User){
        String uid = FirebaseAuth.getInstance().getUid();
        mDatabaseReference.child("users").child(uid).child("firstname").setValue(User.getFirstname());
        mDatabaseReference.child("users").child(uid).child("lastname").setValue(User.getLastname());
        mDatabaseReference.child("users").child(uid).child("gender").setValue(User.getGender());
    }
 /*
    public User getUser(String User){

        User returnedUser;

        mDatabaseReference.child("clients").addValueEventListener(new ValueEventListener() {
            @Override
            //Retrieve data from firebase
            //DataSnapShot : contient les données provenant d'un emplacement de bd Firebase - on recoit les données en tant que DataSnapShot
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User User = postSnapshot.getValue(User.class);
                    returnedUser = User;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LoadPost:onCancelled", databaseError.toException());
            }
        });
    }

    */




}
