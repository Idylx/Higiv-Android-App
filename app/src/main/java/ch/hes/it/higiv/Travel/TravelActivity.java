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

import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;


public class TravelActivity extends AppCompatActivity {

    private Boolean mLocationPermissionGranted = false;

    PermissionsServices permissionsServices = new PermissionsServices();



    Location currentLocation;

    ViewPager viewPager;
    private String idTravel = "";

    TravelStateSectionAdapter adapter = new TravelStateSectionAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        viewPager = (ViewPager) findViewById(R.id.travelContainer);
        setUpViewPager(viewPager);

        mLocationPermissionGranted = permissionsServices.checkAndRequestLocationPermissions(this, this.getApplicationContext());

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

        permissionsServices.getDeviceLocation(this, mLocationPermissionGranted, new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                Task task = (Task) o;
                if (task.isSuccessful()) {
                    currentLocation = (Location) task.getResult();
                } else {
                    Toast.makeText(TravelActivity.this, R.string.NoDeviceLocation, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}