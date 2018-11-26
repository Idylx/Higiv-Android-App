package ch.hes.it.higiv;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class ActivityProfile extends AppCompatActivity {

    private EditStateSectionAdapter editStateSectionAdapter;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editStateSectionAdapter = new EditStateSectionAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.profileContainer);

        setUpViewPager(viewPager);

    }

    public void setUpViewPager(ViewPager viewPager) {
        EditStateSectionAdapter adapter = new EditStateSectionAdapter(getSupportFragmentManager());
        adapter.addFragmentToList(new DetailProfilFragment(), "Detail User");
        adapter.addFragmentToList(new EditProfilFragment(), "Edit User");

        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }


}
