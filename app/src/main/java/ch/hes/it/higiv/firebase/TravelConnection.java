package ch.hes.it.higiv.firebase;

import android.location.Location;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

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

    public void setReport(Travel travel, String idTravel) {
        mDatabaseReference.child("travels").child(idTravel).child("badComment").setValue(travel.getBadComment());
    }


    public void setBeginLocationTravel(Location location, String idTravel) {
        mDatabaseReference.child("travels").child(idTravel).child("beginLocation").child("accuracy").setValue(location.getAccuracy());
        mDatabaseReference.child("travels").child(idTravel).child("beginLocation").child("latitude").setValue(location.getLatitude());
        mDatabaseReference.child("travels").child(idTravel).child("beginLocation").child("longitude").setValue(location.getLongitude());
    }

    public void setEndLocationTravel(Location location, String idTravel) {
        mDatabaseReference.child("travels").child(idTravel).child("endLocation").child("accuracy").setValue(location.getAccuracy());
        mDatabaseReference.child("travels").child(idTravel).child("endLocation").child("latitude").setValue(location.getLatitude());
        mDatabaseReference.child("travels").child(idTravel).child("endLocation").child("longitude").setValue(location.getLongitude());
    }
    public void setTrackedLocation(Location location, String idTravel, String no) {

        mDatabaseReference.child("travels").child(idTravel).child("trackedLocation").child(no).child("accuracy").setValue(location.getAccuracy());
        mDatabaseReference.child("travels").child(idTravel).child("trackedLocation").child(no).child("latitude").setValue(location.getLatitude());
        mDatabaseReference.child("travels").child(idTravel).child("trackedLocation").child(no).child("longitude").setValue(location.getLongitude());
        mDatabaseReference.child("travels").child(idTravel).child("trackedLocation").child(no).child("time").setValue(new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(location.getTime()));
    }



}
