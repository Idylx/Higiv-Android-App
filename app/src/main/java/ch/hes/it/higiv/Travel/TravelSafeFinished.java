package ch.hes.it.higiv.Travel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;


public class TravelSafeFinished extends Fragment {


    private ImageButton goodEvalButton;
    private ImageButton badEvalButton;

    private PlateConnection plateConnection = new PlateConnection();
    private Plate plate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_travel_safe_finished, container, false);

        plateConnection.getPlate(((TravelActivity) getActivity()).getUUID_plate(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
            plate = (Plate) o;
            }
        });


        goodEvalButton = (ImageButton) view.findViewById(R.id.imageButtonYes);

        goodEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                plateConnection.setGoodEvaluation(plate);
                //Go to main activity profile fragment
                ((TravelActivity)getActivity()).setViewPager(0);


            }
        });
        badEvalButton = (ImageButton) view.findViewById(R.id.imageButtonNo);

        badEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                plateConnection.setBadEvaluation(plate);
                //Go to Edit profile fragment
                ((TravelActivity)getActivity()).setViewPager(0);
            }
        });

        return  view;

    }

}