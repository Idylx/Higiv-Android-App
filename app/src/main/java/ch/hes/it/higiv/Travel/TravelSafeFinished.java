package ch.hes.it.higiv.Travel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;

import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;
import ch.hes.it.higiv.firebase.TravelConnection;


public class TravelSafeFinished extends Fragment {


    private ImageButton goodEvalButton;
    private ImageButton badEvalButton;

    private PlateConnection plateConnection = new PlateConnection();
    private TravelConnection travelConnection = new TravelConnection();
    private Plate plate;
    private Travel travel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_travel_safe_finished, container, false);
        travelConnection.getTravel(((TravelActivity) getActivity()).getidTravel(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                travel = (Travel) o;
                plateConnection.getPlate(travel.getIdPlate(), new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        plate = (Plate) o;
                    }
                });
            }
        });

        goodEvalButton = (ImageButton) view.findViewById(R.id.imageButtonYes);

        goodEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndTravel();
                plateConnection.setGoodEvaluation(plate);

                // end activity
                ((TravelActivity)getActivity()).finish();
            }
        });
        badEvalButton = (ImageButton) view.findViewById(R.id.imageButtonNo);

        badEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndTravel();
                plateConnection.setBadEvaluation(plate);

                // end activity
                ((TravelActivity)getActivity()).finish();
            }
        });

        return  view;

    }


    private void setEndTravel(){
        // get device location
        ((TravelActivity)getActivity()).getDeviceLocation();


        // set end travel information
        travel.setTimeEnd(new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(System.currentTimeMillis()));
        travelConnection.setEndTravel(travel, ((TravelActivity) getActivity()).getidTravel());
        travelConnection.setEndLocationTravel(((TravelActivity) getActivity()).getCurrentLocation(), ((TravelActivity) getActivity()).getidTravel());
    }

}