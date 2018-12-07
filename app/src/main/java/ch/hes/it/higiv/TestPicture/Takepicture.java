package ch.hes.it.higiv.TestPicture;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.hes.it.higiv.R;

public class Takepicture extends AppCompatActivity {

    private StorageReference mStorageRef, filepath;
    private Button takePicture, savePlateNumber;
    private EditText retrieveTextFromImage;
    private ImageView plate;
    private Bitmap bitmap;
    private TextRecognizer textRecognizer;
    private SparseArray<TextBlock> items;
    private Frame frame;
    private StringBuilder sb;
    private String textOfImage = "";

    //A Uri object to store file path
    private Uri uri;

    private static final int CAMERA_REQUEST_CODE = 3333;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hitch-guide-to-the-valais.appspot.com");

        takePicture = (Button)findViewById(R.id.btn_takepicture);
        plate = (ImageView)findViewById(R.id.imageplate);
        savePlateNumber = (Button)findViewById(R.id.btn_savePlateNumber);
        retrieveTextFromImage = (EditText) findViewById(R.id.retrieveTextImage);


        //check if the permission is already allow
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                //if not, request the permission to the user
                requestPermissions();
            }
        }

        // Button that opens the camera
        takePicture.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();

                final ProgressDialog mProgress = new ProgressDialog(Takepicture.this);

                mProgress.setTitle("Uploading");
                mProgress.show();

                //name of the image file (add time to have different files to avoid rewrite on the same file)

                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                filepath = mStorageRef.child(retrieveTextFromImage.getText().toString().toUpperCase() + "/" + sdfDate.format(new Date()));

                //upload image

                filepath.putBytes(dataBAOS)
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
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

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
            bitmap = (Bitmap)data.getExtras().get("data");
            plate.setImageBitmap(bitmap);

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
            frame = new Frame.Builder().setBitmap(bitmap).build();
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
        String[] result = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                result[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                result[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                result[2]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //method for request the permission to the user
    private void requestPermissions() {
        ActivityCompat.requestPermissions(Takepicture.this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
}