package ch.hes.it.higiv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TravelOnGoing extends Fragment {

    private Button StopButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_on_going, container, false);

        StopButton = (Button) view.findViewById(R.id.btn_stop_travel);


        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to Edit profile fragment
                ((TravelActivity)getActivity()).setViewPager(2);
            }
        });


        return  view;
    }
}
