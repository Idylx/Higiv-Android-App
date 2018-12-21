package ch.hes.it.higiv.Travel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.UserConnection;
import ch.hes.it.higiv.firebase.TravelConnection;

public class TravelOnGoing extends Fragment{

    private Button StopButton, AlertButton;
    private TextView DestinationTv, CarPlateTv, NumberOfPersonTv;

    //Object Travel to retrieve from firebase
    private Travel travel;
    private String plateString = "";
    private String idTravel;

    PermissionsServices permissionsServices = new PermissionsServices();

    TravelConnection travelConnection = new TravelConnection();
    LocationListener locationListener;

    private LocationManager locationManager;

    private User user;

    private String phoneNumber;
    private String message;

    private Boolean mLocationPermissionGranted = false;
    private double latitude;
    private double longitude;
    private int locationCounter = 0;

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

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                locationCounter++;
                // Called when a new location is found by the network location provider.
                travelConnection.setTrackedLocation(location, idTravel, Integer.toString(locationCounter));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        //Calls the getUser methode from the manager and wait for the callback
        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User) o;
                if (user != null) {
                    if (!user.getEmergencyPhone().isEmpty()) {
                        phoneNumber = user.getEmergencyPhone();
                    }
                }

            }
        });

        idTravel = ((TravelActivity) getActivity()).getidTravel();



        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationManager.removeUpdates(locationListener);

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

    //Dialog for the alert SMS to be sent
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
        return permissionsServices.isServicesSMSOK(getActivity().getApplicationContext());
    }
    //method for request the permission to the user
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);
    }

    //Create the SMS message to be sent
    private void CreateMessage() {
        //create the text for the SMS
        String alert = getString(R.string.messageContainAlert);
        String statName = "";
        String firstname = "";
        String lastname = "";
        String plate = plateString;

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


    //Calls the manager to retrieve the device's location
    private void getDeviceLocation() {

        permissionsServices.getDeviceLocation(getActivity(), mLocationPermissionGranted, new FirebaseCallBack() {
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

    //Call the manager to verify that the devices can support the Map API
    public boolean isServicesOK(){
        return permissionsServices.isServicesMapOK(getActivity());
    }
    //Call the manager to verify and request if needed the location permissions
    public void getLocationPermission() {
        if(checkPermission()){
            mLocationPermissionGranted = true;
        }
    }



}