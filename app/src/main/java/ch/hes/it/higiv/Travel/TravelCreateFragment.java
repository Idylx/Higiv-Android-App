package ch.hes.it.higiv.Travel;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.Model.Travel;
import ch.hes.it.higiv.Model.Plate;
import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.PlateConnection;
import ch.hes.it.higiv.firebase.TravelConnection;

public class TravelCreateFragment extends Fragment {
    //Access the current user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String inputDestination;
    private NumberPicker inputNbPersons;
    private Button btnBeginTravel, btnCancelTravel, btnTakePicture;

    private StorageReference mStorageRef, filepath;
    private EditText retrieveTextFromImage;
    private ImageView plateImage;
    private TextRecognizer textRecognizer;
    private SparseArray<TextBlock> items;
    private ByteArrayOutputStream baos;
    private Frame frame;

    private File outputDir, photoFile, photoDir;
    private StringBuilder sb;
    private Intent intent;
    private String textOfImage, externalStorageStagte;
    private double progress;
    private ProgressDialog mProgress;

    //A Uri object to store file path
    private Uri uri, photoFileUri;
    private static final int CAMERA_REQUEST_CODE = 666;

    //Objects to save into FireBase
    private Travel travel;
    private Plate plate;

    //existing plate
    private Plate plateExisting;

    //current var
    private String numberPlate;
    private int nbPerson = 1;


    // connection to firebase
    private TravelConnection travelConnection = new TravelConnection();
    private PlateConnection plateConnection = new PlateConnection();

    private PermissionsServices permissionsServices = new PermissionsServices();


    private String travelID;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_travel_create, container, false);

        inputNbPersons = (NumberPicker) rootView.findViewById(R.id.number_of_places);
        btnBeginTravel = (Button) rootView.findViewById(R.id.btn_begin_travel);
        btnCancelTravel = (Button) rootView.findViewById(R.id.btn_cancel_travel);
        btnTakePicture = (Button) rootView.findViewById(R.id.takePicture);

        /* début de code qui ne fonctionne pas encore ...
        if (savedInstanceState != null){
            plateImage.setImageURI(uri);
        }
        */

        //Set min max values for the NumberPicker
        inputNbPersons.setMinValue(1);
        inputNbPersons.setMaxValue(9);
        inputNbPersons.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        inputNbPersons.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Get the newly selected number from picker
                nbPerson = newVal;
            }
        });


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Destination");
        autocompleteFragment.setOnPlaceSelectedListener(

                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        inputDestination = (String) place.getName();
                    }

                    @Override
                    public void onError(Status status) {

                    }
                });

        inputNbPersons.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        //Cancel the travel
        btnCancelTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        //Listener used to react to the button click
        btnBeginTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((TravelActivity) getActivity()).getDeviceLocation();

                //Checking if there isn't empty fields, and if it's the case set the focus on the empty field
                if (TextUtils.isEmpty(inputDestination)) {
                    Toast.makeText(getActivity(), R.string.enter_destination, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(retrieveTextFromImage.getText())) {
                    Toast.makeText(getActivity(), R.string.enter_plate_number, Toast.LENGTH_SHORT).show();
                    retrieveTextFromImage.requestFocus();
                    return;
                }

                // setting the travel information
                travel = new Travel();
                travel.setDestination(inputDestination);
                travel.setNumberOfPerson(nbPerson);
                travel.setIdUser(user.getUid());
                travel.setTimeBegin(new SimpleDateFormat("dd-MM-yyyy kk:mm:ss").format(System.currentTimeMillis()));

                // create id for the travel
                travelID = UUID.randomUUID().toString();
                ((TravelActivity) getActivity()).setIDtravel(travelID);

                numberPlate = retrieveTextFromImage.getText().toString().toUpperCase();

                //get plate with current number plate
                plateConnection.getPlate(numberPlate, new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        plateExisting = (Plate) o;

                        // if plate doesn't exist create it
                        if (plateExisting == null) {
                            //Creation of new plate
                            plate = new Plate();
                            plate.setNumber(numberPlate);
                            //Insertion of the object Plate in firebase
                            plateConnection.setPlate(plate, numberPlate);
                            travel.setIdPlate(numberPlate);
                            //if exist set the existing id plate
                        } else {
                            travel.setIdPlate(plateExisting.getNumber());
                        }

                        // set travel and locatioon
                        travelConnection.setTravel(travel, travelID);
                        travelConnection.setBeginLocationTravel(((TravelActivity) getActivity()).getCurrentLocation(), travelID);

                        //create next fragment
                        ((TravelActivity) getActivity()).addFragmentToAdapter(new TravelOnGoing());
                        ((TravelActivity) getActivity()).setViewPager(1);


                    }
                });

                if (TextUtils.isEmpty(retrieveTextFromImage.getText())) {
                    Toast.makeText(getContext(), "Enter the plate number of the car", Toast.LENGTH_LONG).show();
                    return;
                }

                //if the user hasn't taken a picture
                if (plateImage.getDrawable() != null)
                {
                    //get the camera image
                    baos = new ByteArrayOutputStream();
                    plateImage.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    mProgress.setTitle("Uploading");
                    mProgress.show();

                    //name of the image file (add time to have different files to avoid rewrite on the same file)
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    filepath = mStorageRef.child(retrieveTextFromImage.getText().toString().toUpperCase() + "/" + sdfDate.format(new Date()));

                    travel.setImagePath(filepath.toString());

                    //upload image
                    filepath.putBytes(baos.toByteArray())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //if the upload is successfull
                                    //hiding the progress dialog
                                    mProgress.dismiss();

                                    //and displaying a success toast
                                    Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //if the upload is not successfull
                                    //hiding the progress dialog
                                    mProgress.dismiss();

                                    //and displaying error message
                                    Toast.makeText(getContext(), "Failed again" + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    //calculating progress percentage
                                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                    //displaying percentage in progress dialog
                                    mProgress.setMessage("Uploaded " + ((int) progress) + "%...");
                                }
                            });
                }
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitch-guide-to-the-valais.appspot.com");
        mProgress = new ProgressDialog(getContext());

        plateImage = (ImageView) rootView.findViewById(R.id.imageplate);
        retrieveTextFromImage = (EditText) rootView.findViewById(R.id.retrieveTextImage);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startCamera();
                } else {
                    requestPermissions();
                }
            }
        });
        return rootView;

    }

    //Start the new activity for the Camera
    private void startCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        uri = generateTimeStampPhotoFileUri();
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            plateImage.setImageURI(uri);
            getTextFromImage();
        }
    }


    //If there is text on the photo, retrieve it
    public void getTextFromImage() {
        textRecognizer = new TextRecognizer.Builder(getContext()).build();

        if (!textRecognizer.isOperational()) {
            Toast.makeText(getContext(), "Could not get the text", Toast.LENGTH_LONG).show();
        } else {
            plateImage.buildDrawingCache();
            frame = new Frame.Builder().setBitmap(plateImage.getDrawingCache()).build();
            items = textRecognizer.detect(frame);

            sb = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                sb.append(items.valueAt(i).getValue());
            }

            textOfImage = (sb.toString()).replace("-", "");
            textOfImage = textOfImage.replace(" ", "");
            textOfImage = textOfImage.replace(".", "");
            textOfImage = textOfImage.toUpperCase();
            textOfImage = textOfImage.replace("\n","");

            if (!textOfImage.isEmpty())
            {
                retrieveTextFromImage.setText(textOfImage);
            }
        }
    }

    //Check the permissions for the camera and storage
    private boolean checkPermissions() {
        return permissionsServices.isServicesCameraOK(getContext().getApplicationContext());
    }

    //method for request the permission to the user
    private void requestPermissions() {
        permissionsServices.requestCameraPermissions(getActivity());
    }


    //this is for higher quality image from camera
    private Uri generateTimeStampPhotoFileUri() {
        outputDir = getPhotoDirectory();
        if (outputDir != null) {
            photoFile = new File(outputDir, System.currentTimeMillis() + ".jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    //this is for higher quality image from camera
    private File getPhotoDirectory() {
        externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
            photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(photoDir, getString(R.string.app_name));

            if (!outputDir.exists() && !outputDir.mkdirs()) {
                Toast.makeText(getContext(),
                        "Failed to create directory "
                                + outputDir.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                outputDir = null;
            }
        }
        return outputDir;
    }
}