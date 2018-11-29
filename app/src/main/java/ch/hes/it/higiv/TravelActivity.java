package ch.hes.it.higiv;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TravelActivity extends AppCompatActivity {

    ViewPager viewPager;
    private String uuid = "";
    TravelStateSectionAdapter adapter = new TravelStateSectionAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        viewPager = (ViewPager) findViewById(R.id.travelContainer);
        setUpViewPager(viewPager);
    }

    public void setUpViewPager(ViewPager viewPager) {
        adapter.addFragmentToTravelFragmentList(new TravelCreateFragment());
        //adapter.addFragmentToTravelFragmentList(new TravelOnGoing());
        //adapter.addFragmentToTravelFragmentList(new TravelSafeFinished());

        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }

    public void setUUID_travel (String uuid_travel)
    {
        uuid = uuid_travel;
    }

    public String getUUID_travel ()
    {
        return uuid;
    }
}