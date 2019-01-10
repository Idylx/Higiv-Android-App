package ch.hes.it.higiv.AboutSettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.Account.LoginActivity;
import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseConnection;
import ch.hes.it.higiv.firebase.UserConnection;

public class Settings extends AppCompatActivity {

    private ImageButton DeleteButton;
    private ImageButton ButtonFrench;
    private ImageButton ButtonEnglish;
    private ImageButton ButtonGerman;
    private UserConnection userConnection;
    private FirebaseUser userAuth= FirebaseAuth.getInstance().getCurrentUser();
    private Locale myLocale;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        DeleteButton = (ImageButton) findViewById(R.id.deleteButton);
        ButtonFrench = (ImageButton) findViewById(R.id.ButtonFrench);
        ButtonEnglish = (ImageButton) findViewById(R.id.ButtonEnglish);
        ButtonGerman = (ImageButton) findViewById(R.id.ButtonGerman);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSettings);
        //connection
        userConnection = new UserConnection();

        //delete the current user
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                onCreateDialog() .show();

            }
        });





        //button action for french
        ButtonFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang = "fr";
                changeLang(lang);
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Settings.this.finish();
            }
        });

        //button action for english
        ButtonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang = "en";
                changeLang(lang);
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Settings.this.finish();
            }
        });

        //button action for german
        ButtonGerman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang = "de";
                changeLang(lang);
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Settings.this.finish();
            }
        });

    }
    //method for changing language
    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
    public Dialog onCreateDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        // Set an EditText view to get user input
        final EditText input = new EditText(Settings.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //set the edit text in the alert dialog
        builder.setView(input);

        builder.setMessage(R.string.askPasswordDeleteAccount)
                //yes
                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        try {

                            //reauthenticate the user
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), input.getText().toString() );
                            if(user!=null) {
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //delete the user on the complete of the reauthentification
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                //user deleted successfully
                                                if (task.isSuccessful()) {
                                                    userConnection.deleteUser(Settings.this, user);
                                                    //return on login
                                                    finish();
                                                    Toast.makeText(Settings.this, R.string.accountDeleted, Toast.LENGTH_SHORT).show();
                                                } else{
                                                    Toast.makeText(Settings.this, R.string.failDeleteAccount, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }catch (Exception e)
                        {
                            Toast.makeText(Settings.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();//error
                        }
                    }
                })
                .setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // Do nothing
                    }
                });

        return builder.create();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            Settings.this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
