package ch.hes.it.higiv.Profile;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import ch.hes.it.higiv.R;

public class ActivityProfile extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Main view Pager on the activity
        //Needed to manage the fragments presented on the activity
        viewPager = (ViewPager) findViewById(R.id.profileContainer);

        setUpViewPager(viewPager);

    }

    //Link the fragments with the adapter
    //Sets the adapter to the viewpager.
    public void setUpViewPager(ViewPager viewPager) {
        EditStateSectionAdapter adapter = new EditStateSectionAdapter(getSupportFragmentManager());
        adapter.addFragmentToList(new DetailProfilFragment());
        adapter.addFragmentToList(new EditProfilFragment());

        viewPager.setAdapter(adapter);

    }
    //Method called to change the fragment shown on the activity
    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }


}
