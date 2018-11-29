package ch.hes.it.higiv.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.User;

public class UserConnection extends  FirebaseConnection{

    private User userToBeReturned = new User();
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
        readData(uid, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object userFromFirebase) {
                userToBeReturned = (User)userFromFirebase;
            }
        });

        return userToBeReturned;
    }

    public void readData(String uid, final FirebaseCallBack firebaseCallBack){
        mDatabaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                firebaseCallBack.onCallBack(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }






}
