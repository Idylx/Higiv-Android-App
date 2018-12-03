package ch.hes.it.higiv;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ch.hes.it.higiv.Account.LoginActivity;
import ch.hes.it.higiv.Profile.ActivityProfile;
import ch.hes.it.higiv.Travel.TravelActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get the instance of firebase auhtentifcation
        auth = FirebaseAuth.getInstance();

        
        //get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //floating button logic
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TravelActivity.class));
                //add snackbar to main CAN BE REMOVED VERY SOON
//                Snackbar.make(view, "Create a new Travel", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        //drawer layout and logic
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // add navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get view header
        View header =navigationView.getHeaderView(0);

        //modifying textView to see login email
        TextView emailUser = header.findViewById(R.id.textView_navbar_emailUser);
        emailUser.setText(auth.getCurrentUser().getEmail());

        //modifying textView to see login email
        TextView userName = header.findViewById(R.id.textView_navbar_nameUser);
        userName.setText(auth.getCurrentUser().getDisplayName());

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_edit_profile:
                startActivity(new Intent(MainActivity.this, ActivityProfile.class));
                break;
            case R.id.action_logout:
                //logout logic
                auth.signOut();

                //return on login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_create_travel:
                startActivity(new Intent(MainActivity.this, TravelActivity.class));
                break;
            case R.id.nav_find_spot:
                //bla bla
                break;
            case R.id.nav_send_alert:
                //bla bla
                break;
            case R.id.nav_settings:
                // bla bla
                break;
            case R.id.nav_about:
                //bla bla
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    }




}
