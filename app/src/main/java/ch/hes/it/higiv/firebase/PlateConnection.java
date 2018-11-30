package ch.hes.it.higiv.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.Model.User;

public class PlateConnection extends FirebaseConnection {

    //Method called from any activity to edit or add the user's information
    public void goodEvaluation(Plate plate){
        int eval = plate.getNoGoodEvaluation();
        eval++;
        mDatabaseReference.child("plates").child(plate.getUid()).child("noGoodEvaluation").setValue(eval);
    }

    public void badEvaluation(Plate plate) {
        int eval = plate.getNoBadEvaluation();
        eval++;
        mDatabaseReference.child("plates").child(plate.getUid()).child("noBadEvaluation").setValue(eval);
    }

        public void getPlate(String uid, final FirebaseCallBack firebaseCallBack){
        mDatabaseReference.child("plate").child(uid).addValueEventListener(new ValueEventListener() {
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
