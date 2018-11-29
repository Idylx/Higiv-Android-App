package ch.hes.it.higiv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ch.hes.it.higiv.Model.Plate;


public class TravelSafeFinished extends Fragment {


    private ImageButton goodEvalButton;
    private ImageButton badEvalButton;
    private Plate plate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        plate.setNumber(((TravelActivity)getActivity()).getIntent().getExtras().getString("Plate"));
        View view = inflater.inflate(R.layout.fragment_travel_safe_finished, container, false);


        goodEvalButton = (ImageButton) view.findViewById(R.id.imageButtonYes);

        goodEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Go to Edit profile fragment
                ((TravelActivity)getActivity()).setViewPager(0);
            }
        });
        badEvalButton = (ImageButton) view.findViewById(R.id.imageButtonNo);

        badEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                //Go to Edit profile fragment
                ((TravelActivity)getActivity()).setViewPager(0);
            }
        });

        return  view;

    }

}
