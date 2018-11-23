package ch.hes.it.higiv;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DetailProfilFragment extends Fragment {
    private Button EditProfileButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_profil, container, false);

        EditProfileButton = (Button) view.findViewById(R.id.editProfileButton);

        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to Edit profile fragment
                ((ActivityProfile)getActivity()).setViewPager(1);
            }
        });


        return view;
    }
}
