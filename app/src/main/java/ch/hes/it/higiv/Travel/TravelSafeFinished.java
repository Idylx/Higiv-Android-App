package ch.hes.it.higiv.Travel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;
import ch.hes.it.higiv.firebase.TravelConnection;


public class TravelSafeFinished extends Fragment {


    private ImageButton goodEvalButton;
    private ImageButton badEvalButton;


    private PlateConnection plateConnection = new PlateConnection();
    private TravelConnection travelConnection = new TravelConnection();

    private PermissionsServices permissionsServices = new PermissionsServices();

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
                try {
                    setEndTravel();
                    plateConnection.setGoodEvaluation(plate);

                    Toast.makeText(getActivity(), R.string.satisfied, Toast.LENGTH_SHORT).show();
                    // end activity

                    ((TravelActivity) getActivity()).finish();
                }catch(NullPointerException e){
                    Toast.makeText(getActivity(), R.string.bad_happens, Toast.LENGTH_SHORT).show();
                }

            }
        });
        badEvalButton = (ImageButton) view.findViewById(R.id.imageButtonNo);

        badEvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {




                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage(R.string.Add_comment);

                    // Set an EditText view to get user input
                    final EditText input = new EditText(getActivity());
                    alert.setView(input);

                    alert.setPositiveButton(R.string.Send_comment, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            Toast.makeText(getActivity(),R.string.unsatisfied, Toast.LENGTH_SHORT).show();
                            setEndTravel();
                            plateConnection.setBadEvaluation(plate);

                            travel.setBadComment(input.getText().toString());
                            travelConnection.setReport(travel, ((TravelActivity) getActivity()).getidTravel());
                            ((TravelActivity)getActivity()).finish();
                        }
                    });

                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    alert.show();
                // end activity
                }catch(NullPointerException e){
                    Toast.makeText(getActivity(),  R.string.bad_happens, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  view;

    }

    //Save the travel and finish it
    private void setEndTravel(){
        // get device location
        ((TravelActivity)getActivity()).getDeviceLocation();

        // set end travel information
        travel.setTimeEnd(new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(System.currentTimeMillis()));
        travelConnection.setEndTravel(travel, ((TravelActivity) getActivity()).getidTravel());
        if(permissionsServices.simpleCheckLocationPermission(getContext()))
            travelConnection.setEndLocationTravel(((TravelActivity) getActivity()).getCurrentLocation(), ((TravelActivity) getActivity()).getidTravel());
    }

}