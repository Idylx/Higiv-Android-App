package ch.hes.it.higiv.PermissionsServices;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;

public class PermissionsServices {

    private final int ERROR_DIALOG_REQUEST = 9001;

    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //=====================================================
    //Concerning the SMS
    //=====================================================

    //Verifies that the device has the SMS permissions allowed
    public boolean isServicesSMSOK(Context thisActivity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.SEND_SMS);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    //If the device doesn't have the SMS permissions allowed, displays a dialog for the user to enable them
    public void requestSMSPermissions(Activity thisActivity){
        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.SEND_SMS}, 1);
    }

    //=====================================================
    //Concerning the camera
    //=====================================================

    //Verifies that the device has the Camera and External Storage permissions allowed
    public boolean isServicesCameraOK(Context thisActivity) {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] result = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(thisActivity,
                    result[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(thisActivity,
                    result[1]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(thisActivity,
                    result[2]) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //If the device doesn't have the Camera and External Storage permissions allowed, displays a dialog for the user to enable them
    public void requestCameraPermissions(Activity thisActivity) {
        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    //=====================================================
    //Concerning the map
    //=====================================================

    //verify that the services of the phone can support the latest map API
    public boolean isServicesMapOK(Activity thisActivity) {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(thisActivity);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(thisActivity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(thisActivity, R.string.WrongServicesMap, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Check location permissions without pop up the dialog to enable the permission
    public boolean simpleCheckLocationPermission(Context thisContext){

        if (ContextCompat.checkSelfPermission(thisContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(thisContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return true;

        return false;
    }

    //Check if the device has the location permissions allowed. if not, it will display a dialog for the user to enable them.
    public boolean checkAndRequestLocationPermissions(Activity thisActivity, Context thisContext) {
        boolean mLocationPermissionGranted = false;
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(thisContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(thisContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(thisActivity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(thisActivity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
        return mLocationPermissionGranted;
    }

    //Method to retrieve the current device location
    public void getDeviceLocation(Activity thisActivity, boolean mLocationPermissionGranted, final FirebaseCallBack firebaseCallBack) {
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
        } catch (SecurityException e) {
        }
    }


}
