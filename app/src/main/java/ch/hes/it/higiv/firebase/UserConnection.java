package ch.hes.it.higiv.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Account.LoginActivity;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.R;

//Class that handles all queries to Firebase concerning the Users
//edit method only needs a user as input
//getUser needs an instance of the FirebaseCallBack interface to make a callback for async methods from Firebase --> wait until data is fetched from users from Firebase
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
        mDatabaseReference.child("users").child(uid).child("emergencyPhone").setValue(User.getEmergencyPhone());
    }

    public void getUser(String uid, final FirebaseCallBack firebaseCallBack){
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

    public void deleteUser(final Activity thisActivity, FirebaseUser user){
        String uid = user.getUid();
        mDatabaseReference.child("users").child(uid).removeValue();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(thisActivity, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            thisActivity.startActivity(intent);
                            Toast.makeText(thisActivity, R.string.DeleteProfil, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }






}
