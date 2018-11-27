package ch.hes.it.higiv.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.User;

public class UserConnection extends  FirebaseConnection{

    public UserConnection(){
        super();
    }

    //Method called from any activity to edit or add the user's information
    public void editUser(User User){
        String uid = FirebaseAuth.getInstance().getUid();
        mDatabaseReference.child("users").child(uid).child("firstname").setValue(User.getFirstname());
        mDatabaseReference.child("users").child(uid).child("lastname").setValue(User.getLastname());
        mDatabaseReference.child("users").child(uid).child("gender").setValue(User.getGender());
    }

    //Method called from any activity to retrieve specific information from a user
    public User getUser(String uid){

        final User user;

        mReadDataOnce(uid, new OnGetDataListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                user = (User) data.getValue(User.class);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
            }
        });
        return null;
    }

    public void mReadDataOnce(String uid, final OnGetDataListener listener) {
        listener.onStart();
        mDatabaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }






}
