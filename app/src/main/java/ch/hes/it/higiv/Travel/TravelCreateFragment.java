package ch.hes.it.higiv.Travel;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.UUID;

import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;
import ch.hes.it.higiv.firebase.TravelConnection;


public class TravelCreateFragment extends Fragment {

    //Access the database
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    //Access the current user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText  inputPlateNumberState, inputPlateNumber;
    private String inputDestination;
    private NumberPicker inputNbPersons;
    private Button btnBeginTravel, btnStopTravel;


    //Objects to save into FireBase
    private Travel travel;
    private Plate plate;

    //existing plate
    private Plate plateExisting ;

    //current var
    private String numberPlate;
    private int nbPerson = 1;


    // connection to firebase
    private TravelConnection travelConnection = new TravelConnection();
    private PlateConnection plateConnection = new PlateConnection();


    private String travelID;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {



        final View rootView = inflater.inflate(R.layout.fragment_travel_create, container, false);

  //      inputDestination = (EditText) rootView.findViewById(R.id.destination);
        inputPlateNumberState = (EditText) rootView.findViewById(R.id.plate_number_state);
        inputPlateNumber = (EditText) rootView.findViewById(R.id.plate_number);
        inputNbPersons = (NumberPicker) rootView.findViewById(R.id.number_of_places);
        btnBeginTravel = (Button) rootView.findViewById(R.id.btn_begin_travel);
        btnStopTravel = (Button) rootView.findViewById(R.id.btn_cancel_travel);

        //Set min max values for the NumberPicker
        inputNbPersons.setMinValue(1);
        inputNbPersons.setMaxValue(9);
        inputNbPersons.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        inputNbPersons.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Get the newly selected number from picker
                nbPerson = newVal ;
            }
        });


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Destination");
        autocompleteFragment.setOnPlaceSelectedListener(

            new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
               inputDestination = (String) place.getName();
            }

            @Override
            public void onError(Status status) {

            }
        });


        inputNbPersons.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        //Disable stop travel button
        btnStopTravel.setEnabled(false);

        //Change the background color because it's disabled
        btnStopTravel.setBackground(getActivity().getResources().getDrawable(R.color.colorPrimaryDark));

        //Listener used to go to the next edittext which is plate number, when the field is filled in
        inputPlateNumberState.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(inputPlateNumberState.getText().toString().length() == 2)
                {
                    inputPlateNumber.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }

        });



        //Listener used to react to the button click
        btnBeginTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((TravelActivity)getActivity()).getDeviceLocation();

                //Checking if there isn't empty fields, and if it's the case set the focus on the empty field
                if (TextUtils.isEmpty(inputDestination)) {
                    Toast.makeText(getActivity(), R.string.enter_destination, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(inputPlateNumber.getText())) {
                    Toast.makeText(getActivity(), R.string.enter_plate_number, Toast.LENGTH_SHORT).show();
                    inputPlateNumber.requestFocus();
                    return;
                }

                // setting the travel information
                travel = new Travel();
                travel.setDestination(inputDestination);
                travel.setNumberOfPerson(nbPerson);
                travel.setIdUser(user.getUid());
                travel.setTimeBegin(new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(System.currentTimeMillis()));

                // create id for the travel
                travelID = UUID.randomUUID().toString();
                ((TravelActivity)getActivity()).setIDtravel(travelID);

                numberPlate = inputPlateNumberState.getText().toString().toUpperCase() + inputPlateNumber.getText().toString().toUpperCase();


                //get plate with current number plate
                plateConnection.getPlate(numberPlate, new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        plateExisting = (Plate) o;

                        // if plate doesn't exist create it
                        if(plateExisting==null) {
                            //Creation of new plate
                            plate = new Plate();
                            plate.setNumber(numberPlate);
                            //Insertion of the object Plate in firebase
                            mDatabaseReference.child("plates").child(numberPlate).setValue(plate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    travel.setIdPlate(numberPlate);
                                }
                            });

                            //if exist set the existing id plate
                        }else{
                            travel.setIdPlate(plateExisting.getNumber());
                        }

                        // set travel and locatioon
                        travelConnection.setTravel(travel, travelID);
                        travelConnection.setBeginLocationTravel(((TravelActivity) getActivity()).getCurrentLocation(), travelID);

                        //create next fragment
                        ((TravelActivity) getActivity()).addFragmentToAdapter(new TravelOnGoing());
                        ((TravelActivity) getActivity()).setViewPager(1);


                    }
                });
            }
        });
        return rootView;
    }
}