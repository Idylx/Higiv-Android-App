package ch.hes.it.higiv.Travel;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.hes.it.higiv.R;


public class TravelActivity extends AppCompatActivity {

    ViewPager viewPager;
    private String idTravel = "";

    TravelStateSectionAdapter adapter = new TravelStateSectionAdapter(getSupportFragmentManager());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        viewPager = (ViewPager) findViewById(R.id.travelContainer);
        setUpViewPager(viewPager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setUpViewPager(ViewPager viewPager) {
        adapter.addFragmentToTravelFragmentList(new TravelCreateFragment());
        viewPager.setAdapter(adapter);
    }
    public void addFragmentToAdapter(Fragment fragment){
        adapter.addFragmentToTravelFragmentList(fragment);
        adapter.notifyDataSetChanged();
    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }

    public void setIDtravel(String idTravel) {
        this.idTravel = idTravel;
    }
    public String getidTravel() {
        return idTravel;
    }

    public void finishActivity(){
        this.finish();
    }
}