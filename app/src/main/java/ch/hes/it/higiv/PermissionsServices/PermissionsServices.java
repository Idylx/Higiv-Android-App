package ch.hes.it.higiv.PermissionsServices;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;

public class PermissionsServices {

    private final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesMapOK(Activity thisActivity){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(thisActivity);
        if(available == ConnectionResult.SUCCESS){
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available) ){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(thisActivity, available, ERROR_DIALOG_REQUEST );
            dialog.show();
        } else {
            Toast.makeText(thisActivity, R.string.WrongServicesMap, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean isServicesSMSOK(Activity thisActivity){
        int result = ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void getDeviceLocation(Activity thisActivity, boolean mLocationPermissionGranted, final FirebaseCallBack firebaseCallBack){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(thisActivity);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        firebaseCallBack.onCallBack(task);
                    }
                });
            }

        }
        catch (SecurityException e) {
        }
    }


}
