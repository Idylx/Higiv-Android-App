package ch.hes.it.higiv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

//Class to set up the adapter for the fragments.
//Has an arraylist of fragments that are managed using this PagerAdapter
public class EditStateSectionAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> profileFragmentList = new ArrayList<>();

    public EditStateSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragmentToList(Fragment fragment){
        profileFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return profileFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return profileFragmentList.size();
    }
}
