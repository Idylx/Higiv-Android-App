package ch.hes.it.higiv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditStateSectionAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> profileFragmentList = new ArrayList<>();
    private final List<String> profileFragmentListTitles = new ArrayList<>();

    public EditStateSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragmentToList(Fragment fragment, String title){
        profileFragmentList.add(fragment);
        profileFragmentListTitles.add(title);
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
