package ch.hes.it.higiv.Travel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.UserConnection;


public class TravelOnGoing extends Fragment {

    //Access the database
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private Button StopButton, AlertButton;
    private TextView DestinationTv, CarPlateTv, NumberOfPersonTv;
    //Object Travel to retrieve from firebase
    private Travel travel;
    private String plateString = "";

    private User user;

    private String phoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_on_going, container, false);

        StopButton = (Button) view.findViewById(R.id.btn_cancel_travel);
        AlertButton = (Button) view.findViewById(R.id.btn_send_alert);

        DestinationTv = (TextView) view.findViewById(R.id.destination_tv);
        CarPlateTv = (TextView) view.findViewById(R.id.car_plate_tv);
        NumberOfPersonTv = (TextView) view.findViewById(R.id.number_persons_tv);
        plateString = ((TravelActivity)getActivity()).getIntent().getExtras().getString("Plate");


        //Calls the Firebase Manager --> link to Firebase
        UserConnection userConnection = new UserConnection();

        //Calls the getUser methode from the manager and wait for the callback
        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User)o;
                if(user != null){
                    if(user.getEmergencyPhone().isEmpty()){
                        AlertButton.setEnabled(false);
                    }else{
                        phoneNumber = user.getEmergencyPhone();
                    }
                }

            }
        });

        mDatabaseReference.child("travels").child(((TravelActivity)getActivity()).getUUID_travel()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                travel = dataSnapshot.getValue(Travel.class);

                //GET THE VALUES OF THE travel AND PUT THEM IT INTO THE EDITTEXTS
                DestinationTv.setText(travel.getDestination());
                CarPlateTv.setText(plateString);
                NumberOfPersonTv.setText(String.valueOf(travel.getNumberOfPerson()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
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
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        requestPermission();
                    }
                }
                onCreateDialog().show();
                //Go to send alert fragment
                //  ((TravelActivity)getActivity()).setViewPager(?);

            }
        });

        return  view;
    }
    public Dialog onCreateDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialogSendMessage)
                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SmsManager.getDefault().sendTextMessage(phoneNumber, null, getString(R.string.messageContain), null, null);
                        Toast.makeText(getActivity().getApplicationContext(), R.string.messageSendToast, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);

    }
}