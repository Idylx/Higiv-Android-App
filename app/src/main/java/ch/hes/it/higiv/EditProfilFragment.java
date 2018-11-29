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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.firebase.FirebaseAuthentication;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.FirebaseConnection;
import ch.hes.it.higiv.firebase.UserConnection;


public class EditProfilFragment extends Fragment {
    private EditText FirstnameLabel;
    private EditText LastnameLabel;
    private EditText EmailLabel;

    private Button ResetButton;
    private Button EditButton;

    private RadioButton RadioMen;
    private RadioButton RadioWomen;
    private RadioButton RadioOther;

    private UserConnection connectionDatabase = new UserConnection();
    private FirebaseAuthentication firebaseAuthentication = new FirebaseAuthentication();
    private FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();

    private ProgressBar progressBar;

    private String gender;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil, container, false);

        FirstnameLabel = (EditText) view.findViewById(R.id.FirstnameLabel);
        LastnameLabel = (EditText) view.findViewById(R.id.LastnameLabel);
        EmailLabel = (EditText) view.findViewById(R.id.EmailLabel);

        EmailLabel.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        RadioMen = (RadioButton) view.findViewById(R.id.radioMen);
        RadioWomen = (RadioButton) view.findViewById(R.id.radioWomen);
        RadioOther = (RadioButton) view.findViewById(R.id.radioOther);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBarEdit);

        RadioGroup radioGroup = (RadioGroup) view .findViewById(R.id.radioGroup);

        EditButton = (Button) view.findViewById(R.id.EditButton);

        ResetButton = view.findViewById(R.id.resetButton);

        UserConnection userConnection = new UserConnection();

        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User)o;
                if(user != null){
                    FirstnameLabel.setText(user.getFirstname());
                    LastnameLabel.setText(user.getLastname());
                    gender= user.getGender();
                    switch(gender) {
                        case "Male":
                            RadioMen.setChecked(true);
                            break;
                        case "Female":
                            RadioWomen.setChecked(true);
                            break;
                        case "Others":
                            RadioOther.setChecked(true);
                            break;
                    }
                }

            }
        });

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


        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase
                userAuth.updateEmail(EmailLabel.getText().toString());
                final User theUser = getUserFromFragment(FirstnameLabel.getText().toString(), LastnameLabel.getText().toString(), gender);
                connectionDatabase.editUser(theUser);
                //Go back to profile fragment
                ((ActivityProfile)getActivity()).setViewPager(0);
            }
        });


        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuthentication.resetPassword(userAuth.getEmail(), new FirebaseCallBack() {
                    @Override
                    public void onCallBack(Object o) {
                        Task task = (Task)o;
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
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

