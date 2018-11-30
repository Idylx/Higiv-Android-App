package ch.hes.it.higiv.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.Travel;

public class TravelConnection extends FirebaseConnection {

    public TravelConnection(){
        super();
    }

    //Method called from any activity to edit or add the user's information
    public void editTravel(Travel travel){
        String uid = FirebaseAuth.getInstance().getUid();
        mDatabaseReference.child("travels").child(uid).child("numberOfPerson").setValue(travel.getNumberOfPerson());
        mDatabaseReference.child("travels").child(uid).child("destination").setValue(travel.getDestination());
        mDatabaseReference.child("travels").child(uid).child("idPlate").setValue(travel.getIdPlate());
        mDatabaseReference.child("travels").child(uid).child("idUser").setValue(travel.getIdUser());
    }

    public void getTravel(String tid, final FirebaseCallBack firebaseCallBack){
        mDatabaseReference.child("travels").child(tid).addValueEventListener(new ValueEventListener() {
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


}
