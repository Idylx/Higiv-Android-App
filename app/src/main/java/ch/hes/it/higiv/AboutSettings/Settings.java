package ch.hes.it.higiv.AboutSettings;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.UserConnection;

public class Settings extends AppCompatActivity {

    private ImageButton DeleteButton;
    private ImageButton ButtonFrench;
    private ImageButton ButtonEnglish;
    private ImageButton ButtonGerman;
    private UserConnection userConnection;
    private FirebaseUser userAuth= FirebaseAuth.getInstance().getCurrentUser();
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        DeleteButton = (ImageButton) findViewById(R.id.deleteButton);
        ButtonFrench = (ImageButton) findViewById(R.id.ButtonFrench);
        ButtonEnglish = (ImageButton) findViewById(R.id.ButtonEnglish);
        ButtonGerman = (ImageButton) findViewById(R.id.ButtonGerman);

        //connection
        userConnection = new UserConnection();

        //delete the current user
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userConnection.deleteUser(Settings.this, userAuth);
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
}
