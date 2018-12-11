package ch.hes.it.higiv.Travel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.UserConnection;
import ch.hes.it.higiv.firebase.TravelConnection;

public class TravelOnGoing extends Fragment {

    //Access the database
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();


    private Button StopButton, AlertButton;
    private TextView DestinationTv, CarPlateTv, NumberOfPersonTv;

    //Object Travel to retrieve from firebase
    private Travel travel;
    private String plateString = "";
    private String idTravel;


    TravelConnection travelConnection = new TravelConnection();

    private User user;

    private String phoneNumber;
    private String message;
    private PermissionsServices permissionsServices = new PermissionsServices();

    private Boolean mLocationPermissionGranted = false;
    private double latitude;
    private double longitude;

    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float DEFAULT_ZOOM = 15f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_travel_on_going, container, false);

        StopButton = (Button) view.findViewById(R.id.btn_cancel_travel);
        AlertButton = (Button) view.findViewById(R.id.btn_send_alert);

        DestinationTv = (TextView) view.findViewById(R.id.destination_tv);
        CarPlateTv = (TextView) view.findViewById(R.id.car_plate_tv);
        NumberOfPersonTv = (TextView) view.findViewById(R.id.number_persons_tv);



        //Calls the Firebase Manager --> link to Firebase
        UserConnection userConnection = new UserConnection();

        //Calls the getUser methode from the manager and wait for the callback
        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User)o;
                if(user != null){
                    if(!user.getEmergencyPhone().isEmpty()){
                        phoneNumber = user.getEmergencyPhone();
                    }
                }

            }
        });

        idTravel = ((TravelActivity) getActivity()).getidTravel();

        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go to safe finished fragment
                ((TravelActivity) getActivity()).addFragmentToAdapter(new TravelSafeFinished());
                ((TravelActivity)getActivity()).setViewPager(2);
            }
        });


        AlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the permission is already allow
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        //if not, request the permission to the user
                        requestPermission();
                    }
                }
                //display a dialog for know if the user will make really that
                onCreateDialog().show();

            }
        });

        return  view;
    }
    public Dialog onCreateDialog() {
        //test if the location is allow
        if(isServicesOK()){
            getLocationPermission();
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialogSendMessage)
                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //check if the permission is allow
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!checkPermission()) {
                                //the permission is not allow
                                Toast.makeText(getActivity().getApplicationContext(), R.string.permissionNotActivToast, Toast.LENGTH_SHORT).show();
                            }else if(user.getEmergencyPhone().isEmpty()){
                                //the phone number is not enter
                                Toast.makeText(getActivity().getApplicationContext(), R.string.phoneNumberDontExist, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                plateString = travel.getIdPlate();
                                CreateMessage();
                            }
                        }

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
    //Method for check if the user have already allow the permission for send message
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    //method for request the permission to the user
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);
    }
    private void CreateMessage() {
        //create the text for the SMS
        String alert = getString(R.string.messageContainAlert);
        String statName = "";
        String firstname = "";
        String lastname = "";
        String plate = plateString;

        String geolocalisation ="https://www.google.com/search?q=" + latitude + "%2C" + longitude;
        //Check if the user have enter the information
        if(!user.getFirstname().isEmpty() || !user.getLastname().isEmpty()){
            statName = getString(R.string.messageContainUser);
        }
        if(!user.getFirstname().isEmpty()){
            firstname=user.getFirstname() + " ";
        }
        if(!user.getLastname().isEmpty()){
            lastname=user.getLastname();
        }

        message = alert + "\n" + firstname + lastname + " " + statName + "\n" + getString(R.string.messageContainPlate)+ " " + plate + "\n" + getString(R.string.messageContainLocalisation) + " ";
        getDeviceLocation();
    }

    @Override
    public void onStart() {
        super.onStart();

        // get current travel, cannot be on create view
        // need to be call during the start
        travelConnection.getTravel(idTravel, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                travel = (Travel)o;
                DestinationTv.setText(travel.getDestination());
                NumberOfPersonTv.setText(Integer.toString(travel.getNumberOfPerson()));
                CarPlateTv.setText(travel.getIdPlate());
            }
        });
    }
    private void getDeviceLocation() {

        permissionsServices.getDeviceLocation(getActivity(), true/*mLocationPermissionGranted*/, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {

                Task task = (Task) o;
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    longitude=currentLocation.getLongitude();
                    latitude=currentLocation.getLatitude();
                    String geolocalisation ="https://www.google.com/search?q=" + latitude + "%2C" + longitude;
                    message = message + geolocalisation;
                    //Send the SMS
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(getActivity().getApplicationContext(), R.string.messageSendToast, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.NoDeviceLocation, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean isServicesOK(){
        return permissionsServices.isServicesMapOK(getActivity());
    }
    public void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }
}