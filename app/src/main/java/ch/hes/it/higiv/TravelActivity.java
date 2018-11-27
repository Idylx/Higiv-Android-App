package ch.hes.it.higiv;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TravelActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        viewPager = (ViewPager) findViewById(R.id.travelContainer);
        setUpViewPager(viewPager);
    }

    public void setUpViewPager(ViewPager viewPager) {
        TravelStateSectionAdapter adapter = new TravelStateSectionAdapter(getSupportFragmentManager());
        adapter.addFragmentToTravelFragmentList(new TravelCreateFragment());
        adapter.addFragmentToTravelFragmentList(new TravelOnGoing());

        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }


}
