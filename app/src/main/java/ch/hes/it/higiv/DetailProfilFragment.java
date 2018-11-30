package ch.hes.it.higiv;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.jar.Attributes;

import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.FirebaseConnection;
import ch.hes.it.higiv.firebase.UserConnection;

public class DetailProfilFragment extends Fragment {
    private TextView NameLabel;
    private TextView EmailLabel;
    private TextView GenderLabel;
    private Button EditProfileButton;

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_profil, container, false);

        NameLabel = (TextView) view.findViewById(R.id.NameLabel);
        EmailLabel = (TextView) view.findViewById(R.id.EmailLabel);
        GenderLabel = (TextView) view.findViewById(R.id.GenderLabel);
        EditProfileButton = (Button) view.findViewById(R.id.editProfileButton);

        //Calls the Firebase Manager --> link to Firebase
        UserConnection userConnection = new UserConnection();
        //Calls the getUser methode from the manager and wait for the callback
        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User)o;
                if(user != null){
                    NameLabel.setText(user.getFirstname() + " " + user.getLastname());
                    switch (user.getGender()){
                        case "Male": GenderLabel.setText(R.string.GenderMale);
                            break;
                        case "Female": GenderLabel.setText(R.string.GenderFemale);
                            break;
                        case "Others": GenderLabel.setText(R.string.GenderOther);
                            break;
                        default: GenderLabel.setText("");
                            break;
                    }
                }

            }
        });
        //Retrieve the email from Firebase account (Authentication)
        EmailLabel.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //Go to the next fragment --> Edit profile fragment
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
