package ch.hes.it.higiv.AboutSettings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.UserConnection;

public class Settings extends AppCompatActivity {

    private Button DeleteButton;
    private UserConnection userConnection;
    private FirebaseUser userAuth= FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        DeleteButton = (Button) findViewById(R.id.deleteButton);

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
