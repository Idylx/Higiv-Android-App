package ch.hes.it.higiv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ch.hes.it.higiv.Account.LoginActivity;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.Profile.ActivityProfile;
import ch.hes.it.higiv.Travel.TravelActivity;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.UserConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private FirebaseAuth auth;
    private User user;
    private String phoneNumber;

    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float DEFAULT_ZOOM = 15f;

    private PermissionsServices permissionsServices = new PermissionsServices();

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    private Double latitude = null;
    private Double longitude = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get the instance of firebase auhtentifcation
        auth = FirebaseAuth.getInstance();
        
        //get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //floating button logic
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TravelActivity.class));


                //add snackbar to main CAN BE REMOVED VERY SOON
//                Snackbar.make(view, "Create a new Travel", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        //drawer layout and logic
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // add navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get view header
        View header = navigationView.getHeaderView(0);

        //modifying textView to see login email
        TextView emailUser = header.findViewById(R.id.textView_navbar_emailUser);
        emailUser.setText(auth.getCurrentUser().getEmail());

        //modifying textView to see login email
        TextView userName = header.findViewById(R.id.textView_navbar_nameUser);
        userName.setText(auth.getCurrentUser().getDisplayName());

        //For the map
        if(isServicesOK()){
            getLocationPermission();
        }

    }

    public void getLocationPermission() {
        if(permissionsServices.checkAndRequestLocationPermissions(this, this.getApplicationContext())){
            mLocationPermissionGranted = true;
            initMap();
        }
    }

    private void getDeviceLocation() {

        permissionsServices.getDeviceLocation(this, mLocationPermissionGranted, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                Task task = (Task) o;
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                    //coordinates for alert sms
                    longitude = currentLocation.getLongitude();
                    latitude = currentLocation.getLatitude();
                } else {
                    Toast.makeText(MainActivity.this, R.string.NoDeviceLocation, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
        }
    }

    public boolean isServicesOK(){
            return permissionsServices.isServicesMapOK(this);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_edit_profile:
                startActivity(new Intent(MainActivity.this, ActivityProfile.class));
                break;
            case R.id.action_logout:
                //logout logic
                auth.signOut();

                //return on login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_create_travel:
                startActivity(new Intent(MainActivity.this, TravelActivity.class));
                break;
            case R.id.nav_find_spot:
                //bla bla
                break;
            case R.id.nav_send_alert:
                //Calls the Firebase Manager --> link to Firebase
                UserConnection userConnection = new UserConnection();

                //Calls the getUser methode from the manager and wait for the callback
                userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        user = (User)o;
                        if(user != null){
                            if(user.getEmergencyPhone().isEmpty()){
                                //the phone number is not enter
                                Toast.makeText(MainActivity.this, R.string.phoneNumberDontExist, Toast.LENGTH_SHORT).show();
                            }else{
                                phoneNumber = user.getEmergencyPhone();
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (!checkPermission()) {
                                        //if not, request the permission to the user
                                        requestPermission();
                                    }
                                }
                                onCreateDialog().show();

                            }
                        }

                    }

                });

                break;
            case R.id.nav_settings:
                // bla bla
                break;
            case R.id.nav_about:
                //bla bla
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public Dialog onCreateDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialogSendMessage)
                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //check if the permission is allow
                        if (!checkPermission()) {
                            //the permission is not allow
                            Toast.makeText(MainActivity.this, R.string.permissionNotActivToast, Toast.LENGTH_SHORT).show();
                        } else{

                            String finalMessage = CreateMessage();
                            //Send the SMS
                            SmsManager.getDefault().sendTextMessage(phoneNumber, null, finalMessage, null, null);
                            Toast.makeText(MainActivity.this, R.string.messageSendToast, Toast.LENGTH_SHORT).show();
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

    private boolean checkPermission() {
        return permissionsServices.isServicesSMSOK(this);
    }
    //method for request the permission for the SMS to the user
    private void requestPermission() {
        permissionsServices.requestSMSPermissions(this);
    }

    private String CreateMessage() {
        //create the text for the SMS
        String alert = getString(R.string.messageContainAlert);
        String statName = "";
        String firstname = "";
        String lastname = "";
        String geolocalisation = "Unknown";
        getDeviceLocation();
        if(latitude != null && longitude != null){
            geolocalisation = "https://www.google.com/search?q=" + latitude + "%2C" + longitude;
        }
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
        return alert + "\n" + firstname + lastname + " " + statName + "\n" + getString(R.string.messageContainLocalisation) + " " + geolocalisation;
    }


}
