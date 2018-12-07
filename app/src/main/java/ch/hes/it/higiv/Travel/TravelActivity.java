package ch.hes.it.higiv.Travel;


import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.hes.it.higiv.R;


public class TravelActivity extends AppCompatActivity {



    private Boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;



    Location currentLocation;


    private static final String TAG = "Travel Activity";

    ViewPager viewPager;
    private String idTravel = "";

    TravelStateSectionAdapter adapter = new TravelStateSectionAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        viewPager = (ViewPager) findViewById(R.id.travelContainer);
        setUpViewPager(viewPager);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setUpViewPager(ViewPager viewPager) {
        adapter.addFragmentToTravelFragmentList(new TravelCreateFragment());
        viewPager.setAdapter(adapter);
    }
    public void addFragmentToAdapter(Fragment fragment){
        adapter.addFragmentToTravelFragmentList(fragment);
        adapter.notifyDataSetChanged();
    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }

    public void setIDtravel(String idTravel) {
        this.idTravel = idTravel;
    }
    public String getidTravel() {
        return idTravel;
    }



    public Location getCurrentLocation() {
        return currentLocation;
    }



    public void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                             currentLocation = (Location) task.getResult();

                        } else {
                            Toast.makeText(TravelActivity.this, "Unable to retrieve device location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
        }
    }

}