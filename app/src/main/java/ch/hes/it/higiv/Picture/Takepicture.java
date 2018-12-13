package ch.hes.it.higiv.Picture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.hes.it.higiv.PermissionsServices.PermissionsServices;
import ch.hes.it.higiv.R;


public class Takepicture extends AppCompatActivity {

    private StorageReference mStorageRef, filepath;
    private Button takePicture, savePlateNumber;
    private EditText retrieveTextFromImage;
    private ImageView plate;
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

    private PermissionsServices permissionsServices = new PermissionsServices();

    //A Uri object to store file path
    private Uri uri, photoFileUri;
    private static final int CAMERA_REQUEST_CODE = 666;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitch-guide-to-the-valais.appspot.com");
        mProgress = new ProgressDialog(this);

        takePicture = (Button)findViewById(R.id.btn_takepicture);
        plate = (ImageView)findViewById(R.id.imageplate);
        savePlateNumber = (Button)findViewById(R.id.btn_savePlateNumber);
        retrieveTextFromImage = (EditText) findViewById(R.id.retrieveTextImage);


        //check if the permission is already allow
        if (!checkPermissions()) {
            //if not, request the permission to the user
            requestPermissions();
        }

        // Button that opens the camera
        takePicture.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                uri = generateTimeStampPhotoFileUri();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });


        // Button that saves the image we took with the camera on Firebase
        savePlateNumber.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if(plate.getDrawable() == null)
                {
                    Toast.makeText(getApplicationContext(), "Take a picture first", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(retrieveTextFromImage.getText()))
                {
                    Toast.makeText(getApplicationContext(), "Enter the plate number of the car", Toast.LENGTH_LONG).show();
                    return;
                }

                //get the camera image
                baos = new ByteArrayOutputStream();
                plate.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, baos);

                mProgress.setTitle("Uploading");
                mProgress.show();

                //name of the image file (add time to have different files to avoid rewrite on the same file)
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                filepath = mStorageRef.child(retrieveTextFromImage.getText().toString().toUpperCase() + "/" + sdfDate.format(new Date()));

                //upload image
                filepath.putBytes(baos.toByteArray())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                mProgress.dismiss();

                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                mProgress.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), "Failed again"+exception.getMessage(), Toast.LENGTH_LONG).show();
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
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            plate.setImageURI(uri);
            getTextFromImage();
        }
    }

    public void getTextFromImage()
    {
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational())
        {
            Toast.makeText(getApplicationContext(), "Could not get the text", Toast.LENGTH_LONG).show();
        }
        else
        {
            plate.buildDrawingCache();
            frame = new Frame.Builder().setBitmap(plate.getDrawingCache()).build();
            items = textRecognizer.detect(frame);

            sb = new StringBuilder();

            for (int i=0 ; i<items.size() ; i++)
            {
                sb.append(items.valueAt(i).getValue());
            }

            textOfImage = (sb.toString()).replace("-", "");
            textOfImage = textOfImage.replace(" ", "");
            textOfImage = textOfImage.replace(".", "");
            textOfImage = textOfImage.toUpperCase();

            retrieveTextFromImage.setText(textOfImage);
        }
    }

    //Check the permissions for the camera and storage
    private boolean checkPermissions() {
        return permissionsServices.isServicesCameraOK(this.getApplicationContext());
    }

    //method for request the permission to the user
    private void requestPermissions() {
        permissionsServices.requestCameraPermissions(this);
    }


    //this is for higher quality image from camera
    private Uri generateTimeStampPhotoFileUri()
    {
        outputDir = getPhotoDirectory();
        if (outputDir != null)
        {
            photoFile = new File(outputDir, System.currentTimeMillis() + ".jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    //this is for higher quality image from camera
    private File getPhotoDirectory()
    {
        externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED))
        {
            photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(photoDir, getString(R.string.app_name));

            if (!outputDir.exists() && !outputDir.mkdirs())
            {
                Toast.makeText(this,R.string.FailedDirectoryCreation + outputDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                outputDir = null;
            }
        }
        return outputDir;
    }
}