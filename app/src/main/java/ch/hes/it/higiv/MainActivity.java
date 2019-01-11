package ch.hes.it.higiv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ch.hes.it.higiv.AboutSettings.About;
import ch.hes.it.higiv.AboutSettings.Settings;
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
                mLocationPermissionGranted = permissionsServices.simpleCheckLocationPermission(getApplicationContext());
                if(mLocationPermissionGranted)
                    startActivity(new Intent(MainActivity.this, TravelActivity.class));
                else
                //add snackbar to main to ask for the location permissions
                    Snackbar.make(view, R.string.SnackBarButtonText, Snackbar.LENGTH_LONG)
                            .setAction(R.string.SnackBarButtonActionText, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    permissionsServices.checkAndRequestLocationPermissions(MainActivity.this, getApplicationContext());
                                }
                            }).setActionTextColor(Color.WHITE)
                            .show();

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

    //Calls the manager to verify is the permissions have been allowed
    public void getLocationPermission() {
        if(permissionsServices.checkAndRequestLocationPermissions(this, this.getApplicationContext())){
            mLocationPermissionGranted = true;
            initMap();
        }
    }
    //Calls the manager to get the location of the device
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

    //Centers the map with the device location in the middle
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //As soon as the map is ready, get the location and display it on the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(permissionsServices.checkAndRequestLocationPermissions(this, this.getApplicationContext())){
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
        }
        addMarker(46.283033, 7.540020, "Spot Technopole");
        addMarker(46.291836, 7.531598, "Spot Sierre");
        addMarker(46.228390, 7.363108, "Spot Sion");
        addMarker(46.097702, 7.219110, "Spot Verbier");
        addMarker(46.178343, 7.576370, "Spot Grimentz");
        addMarker(46.105749, 7.080271, "Spot Martigny");
        addMarker(46.211320, 7.398243, "Spot Vex");
        addMarker(46.183452, 7.294325, "Spot Nendaz");
        addMarker(46.172417, 7.421416, "Spot Hérémence");
        addMarker(46.271760, 7.380845, "Spot Arbaz");
        addMarker(46.151253, 7.178783, "Spot Saxon");
        addMarker(46.181638, 6.874700, "Spot Champery");
        addMarker(46.261071, 6.949517, "Spot Monthey");
    }
    public void addMarker(double lat, double lon, String spot){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(spot)
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_bitmap_little)));

    }

    //Initiailize the map
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

    //Calls the manager to check is the device can support the latest
    public boolean isServicesOK(){
            return permissionsServices.isServicesMapOK(this);
    }



    //Back button
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //For the menu on the top bar
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

    //for the drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_create_travel:
                if(permissionsServices.simpleCheckLocationPermission(this.getApplicationContext()))
                    startActivity(new Intent(MainActivity.this, TravelActivity.class));
                else
                    Toast.makeText(MainActivity.this, R.string.NoPermissionsLocationForTravel, Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.nav_find_spot:
                //bla bla
                break;*/
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
                                if (!checkPermission()) {
                                    //if not, request the permission to the user
                                    requestPermission();
                                }
                                onCreateDialog().show();
                            }
                        }

                    }

                });

                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                MainActivity.this.finish();
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, About.class));
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    //Displays the dialog to send the SMS as an Alert signal
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
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    //Check SMS permissions
    private boolean checkPermission() {
        return permissionsServices.isServicesSMSOK(this);
    }
    //method for request the permission for the SMS to the user
    private void requestPermission() {
        permissionsServices.requestSMSPermissions(this);
    }
    //Create the message to be sent by SMS
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
