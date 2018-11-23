package ch.hes.it.higiv;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import ch.hes.it.higiv.Model.State;
import ch.hes.it.higiv.Model.Travel;


public class TravelCreateFragment extends Fragment {

    //Access the database
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    //Access the current user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText inputDestination, inputPlateNumberSate, inputPlateNumber;
    private NumberPicker inputNbPersons;
    private Button btnBeginTravel, btnStopTravel, btnSendAlert;
    //Object Travel to save into FireBase
    private Travel travel;
    //States possible entered in FireBase
    private ArrayList<String> states;
    private int nbPerson;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_travel_create, container, false);

        inputDestination = (EditText) rootView.findViewById(R.id.destination);
        inputPlateNumberSate = (EditText) rootView.findViewById(R.id.plate_number_state);
        inputPlateNumber = (EditText) rootView.findViewById(R.id.plate_number);
        //inputNbPersons = (EditText) rootView.findViewById(R.id.number_of_places);
        inputNbPersons = (NumberPicker) rootView.findViewById(R.id.number_of_places);
        btnBeginTravel = (Button) rootView.findViewById(R.id.btn_begin_travel);
        btnStopTravel = (Button) rootView.findViewById(R.id.btn_stop_travel);
        btnSendAlert = (Button) rootView.findViewById(R.id.btn_send_alert);

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

        //Hide the send alert button when we launch the fragment
        btnSendAlert.setVisibility(View.GONE);

        //Disable stop travel button
        btnStopTravel.setEnabled(false);

        //Change the background color because it's disabled
        btnStopTravel.setBackground(getActivity().getResources().getDrawable(R.color.colorPrimaryDark));

        //Listener used to go to the next edittext which is plate number, when the field is filled in
        inputPlateNumberSate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                if(inputPlateNumberSate.getText().toString().length() == 2)
                {
                    inputPlateNumber.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }

        });

        //Acess the states node to retrieve all states possible when we create a travel
        mDatabaseReference.child("states").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                states = new ArrayList<String>();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    states.add(ds.getValue(State.class).getName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

        //Listener used to react to the button click
        btnBeginTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking if there isn't empty fields, and if it's the case set the focus on the empty field
                if (TextUtils.isEmpty(inputDestination.getText())) {
                    Toast.makeText(getActivity(), R.string.enter_destination, Toast.LENGTH_SHORT).show();
                    inputDestination.requestFocus();
                    return;
                }

                //Check if the state entered is valid
                if (!states.contains(inputPlateNumberSate.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.enter_state, Toast.LENGTH_SHORT).show();
                    inputPlateNumberSate.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(inputPlateNumber.getText())) {
                    Toast.makeText(getActivity(), R.string.enter_plate_number, Toast.LENGTH_SHORT).show();
                    inputPlateNumber.requestFocus();
                    return;
                }

                //Creation of the object Travel
                travel = new Travel(inputDestination.getText().toString(),
                        inputPlateNumberSate.getText().toString(),
                        Integer.parseInt(inputPlateNumber.getText().toString()),
                        nbPerson,
                        user.getUid()
                );

                //Insertion of the object Travel in firebase
                mDatabaseReference.child("travels").child(UUID.randomUUID().toString()).setValue(travel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            //Show the button Send alert
                            btnSendAlert.setVisibility(View.VISIBLE);

                            //Enable the button stop travel
                            btnStopTravel.setEnabled(true);
                            btnStopTravel.setBackground(getActivity().getResources().getDrawable(R.color.colorPrimary));

                            //Disable the button begin travel
                            btnBeginTravel.setEnabled(false);
                            btnBeginTravel.setBackground(getActivity().getResources().getDrawable(R.color.colorPrimaryDark));

                            //Display a message success
                            Toast.makeText(getActivity(), R.string.CreationTravelSuccessful, Toast.LENGTH_LONG).show();
                        }
                        else {
                            //Display a message error
                            Toast.makeText(getActivity(), R.string.CreationTravelFailed, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return rootView;
    }
}