package ch.hes.it.higiv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.firebase.FirebaseConnection;
import ch.hes.it.higiv.firebase.UserConnection;


public class EditProfilFragment extends Fragment {
    private EditText FirstnameLabel;
    private EditText LastnameLabel;
    private EditText EmailLabel;
    private EditText PasswordLabel;

    private Button EditButton;

    private RadioButton RadioMen;
    private RadioButton RadioWomen;
    private RadioButton RadioOther;

    private UserConnection connectionDatabase = new UserConnection();

    private String gender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil, container, false);

        FirstnameLabel = (EditText) view.findViewById(R.id.FirstnameLabel);
        LastnameLabel = (EditText) view.findViewById(R.id.LastnameLabel);
        EmailLabel = (EditText) view.findViewById(R.id.EmailLabel);
        PasswordLabel = (EditText) view.findViewById(R.id.PasswordLabel);

        RadioMen = (RadioButton) view.findViewById(R.id.radioMen);
        RadioWomen = (RadioButton) view.findViewById(R.id.radioWomen);
        RadioOther = (RadioButton) view.findViewById(R.id.radioOther);

        RadioGroup radioGroup = (RadioGroup) view .findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioMen:
                        gender = "Male";
                        break;
                    case R.id.radioWomen:
                        gender = "Female";
                        break;
                    case R.id.radioOther:
                        gender = "Others";
                        break;
                }
            }
        });

        EditButton = (Button) view.findViewById(R.id.EditButton);

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase
                final User theUser = getUserFromFragment(FirstnameLabel.getText().toString(), LastnameLabel.getText().toString(), gender);
                connectionDatabase.editUser(theUser);
                //Go back to profile fragment
                ((ActivityProfile)getActivity()).setViewPager(0);
            }
        });


        return  view;
    }

    public User getUserFromFragment(String firstname, String lastname, String gender){
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        return user;
    }


}

