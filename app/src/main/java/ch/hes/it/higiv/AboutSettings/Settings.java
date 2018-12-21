package ch.hes.it.higiv.AboutSettings;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.UserConnection;

public class Settings extends AppCompatActivity {

    private ImageButton DeleteButton;
    private UserConnection userConnection;
    private FirebaseUser userAuth= FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        DeleteButton = (ImageButton) findViewById(R.id.deleteButton);

        //connection
        userConnection = new UserConnection();

        //delete the current user
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userConnection.deleteUser(Settings.this, userAuth);
            }
        });

    }
}
