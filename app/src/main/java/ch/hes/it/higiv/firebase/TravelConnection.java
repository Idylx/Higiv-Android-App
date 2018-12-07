package ch.hes.it.higiv.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.Travel;

public class TravelConnection extends FirebaseConnection {

    public TravelConnection(){
        super();
    }

    public void getTravel(String uid, final FirebaseCallBack firebaseCallBack){
        mDatabaseReference.child("travels").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Travel travel = dataSnapshot.getValue(Travel.class);
                firebaseCallBack.onCallBack(travel);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setTravel(Travel travel, String uid){
        mDatabaseReference.child("travels").child(uid).setValue(travel);
    }

    public void setEndTravel(Travel travel, String idTravel) {
    mDatabaseReference.child("travels").child(idTravel).child("timeEnd").setValue(travel.getTimeEnd());
    }



}
