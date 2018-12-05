package ch.hes.it.higiv.Travel;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;
import ch.hes.it.higiv.firebase.TravelConnection;


public class TravelOnGoing extends Fragment {

    //Access the database
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();


    private Button StopButton, AlertButton;
    private TextView DestinationTv, CarPlateTv, NumberOfPersonTv;
    //Object Travel to retrieve from firebase
    private Travel travel;
    private Plate plate ;


    TravelConnection travelConnection = new TravelConnection();
    PlateConnection plateConnection = new PlateConnection();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_on_going, container, false);

        StopButton = (Button) view.findViewById(R.id.btn_cancel_travel);
        AlertButton = (Button) view.findViewById(R.id.btn_send_alert);

        DestinationTv = (TextView) view.findViewById(R.id.destination_tv);
        CarPlateTv = (TextView) view.findViewById(R.id.car_plate_tv);
        NumberOfPersonTv = (TextView) view.findViewById(R.id.number_persons_tv);
        String uuidTravel = ((TravelActivity) getActivity()).getUUID_travel();


        travelConnection.getTravel(uuidTravel, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                travel = (Travel)o;
                DestinationTv.setText(travel.getDestination());
                NumberOfPersonTv.setText(Integer.toString(travel.getNumberOfPerson()));
                plateConnection.getPlate(travel.getIdPlate(), new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        plate = (Plate)o;
                        CarPlateTv.setText(plate.getNumber());

                    }
                });

            }
        });





        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to safe finished fragment

                ((TravelActivity)getActivity()).adapter.addFragmentToTravelFragmentList(new TravelSafeFinished());
                ((TravelActivity)getActivity()).viewPager.setAdapter(((TravelActivity)getActivity()).adapter);
                ((TravelActivity)getActivity()).setViewPager(2);
            }
        });


        AlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to send alert fragment

            }
        });

        return  view;
    }
}