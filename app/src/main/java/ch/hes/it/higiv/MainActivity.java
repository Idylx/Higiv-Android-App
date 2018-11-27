package ch.hes.it.higiv;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Button btnSignout, btnCreateTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
        }

        btnSignout = (Button) findViewById(R.id.sign_out);
        btnCreateTravel = (Button) findViewById(R.id.btn_create_travel);


        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });


        btnCreateTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TravelActivity.class));
            }
        });

    }
    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {

        }
    }
}