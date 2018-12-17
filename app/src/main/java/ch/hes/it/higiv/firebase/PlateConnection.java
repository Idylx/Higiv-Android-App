package ch.hes.it.higiv.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.Plate;

public class PlateConnection extends FirebaseConnection {

    //Method called from any activity to edit or add the user's information
    public void setGoodEvaluation(Plate plate){
        int eval = plate.getNoGoodEvaluation() + 1;
        mDatabaseReference.child("plates").child(plate.getNumber()).child("noGoodEvaluation").setValue(eval);
    }

    public void setPlate(Plate plate, String numberPlate){
        mDatabaseReference.child("plates").child(numberPlate).setValue(plate);
    }

    public void setBadEvaluation(Plate plate) {
        int eval = plate.getNoBadEvaluation() + 1;
        mDatabaseReference.child("plates").child(plate.getNumber()).child("noBadEvaluation").setValue(eval);
    }

    public void getPlate(String number, final FirebaseCallBack firebaseCallBack){
        mDatabaseReference.child("plates").child(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Plate plate = dataSnapshot.getValue(Plate.class);
                firebaseCallBack.onCallBack(plate);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }





}
