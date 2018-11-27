package ch.hes.it.higiv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TravelStateSectionAdapter  extends FragmentStatePagerAdapter {

    private final List<Fragment> travelFragmentList = new ArrayList<>();

    public TravelStateSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragmentToTravelFragmentList(Fragment fragment){
        travelFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return travelFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return travelFragmentList.size();
    }
}
