package ch.hes.it.higiv.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.hes.it.higiv.MainActivity;
import ch.hes.it.higiv.Model.User;
import ch.hes.it.higiv.R;
import ch.hes.it.higiv.firebase.FirebaseAuthentication;
import ch.hes.it.higiv.firebase.FirebaseCallBack;
import ch.hes.it.higiv.firebase.UserConnection;


public class EditProfilFragment extends Fragment {
    private EditText FirstnameLabel;
    private EditText LastnameLabel;
    private EditText EmailLabel;
    private EditText PhoneLabel;

    private Button ResetButton;
    private ImageButton EditButton;


    private RadioGroup radioGroup;
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
        PhoneLabel = (EditText) view.findViewById(R.id.PhoneLabel);

        EmailLabel.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //Make the email not modifiable
        EmailLabel.setKeyListener(null);

        RadioMen = (RadioButton) view.findViewById(R.id.radioMen);
        RadioWomen = (RadioButton) view.findViewById(R.id.radioWomen);
        RadioOther = (RadioButton) view.findViewById(R.id.radioOther);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBarEdit);

        radioGroup = (RadioGroup) view .findViewById(R.id.radioGroup);

        EditButton = (ImageButton) view.findViewById(R.id.EditButton);
        ResetButton = view.findViewById(R.id.resetButton);


        FirstnameLabel.setText("");
        LastnameLabel.setText("");
        PhoneLabel.setText("");

        //Manager for Firebase Called
        final UserConnection userConnection = new UserConnection();
        //Method from manager to retrieve the user
        userConnection.getUser(FirebaseAuth.getInstance().getUid(), new FirebaseCallBack() {
            @Override
            public void onCallBack(Object o) {
                user = (User)o;
                if(user != null){
                    FirstnameLabel.setText(user.getFirstname());
                    LastnameLabel.setText(user.getLastname());
                    PhoneLabel.setText(user.getEmergencyPhone());
                    gender= user.getGender();
                    if(user.getGender()!= null){
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
                            default: RadioMen.setChecked(false);
                                RadioWomen.setChecked(false);
                                RadioOther.setChecked(false);
                                break;
                        }
                    }

                }

            }
        });

        //Retrieve which radiobutton was chosen
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
                    default:
                        gender = "";
                        break;
                }
            }
        });


        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(allInputFilled()){
                    //Update Email in Firebase
                    userAuth.updateEmail(EmailLabel.getText().toString());
                    final User theUser = getUserFromFragment(FirstnameLabel.getText().toString(), LastnameLabel.getText().toString(),PhoneLabel.getText().toString(), gender);
                    //calls the edit method from the manager of Firebase
                    connectionDatabase.editUser(theUser);

                    //Go back to profile fragment
                    ((ActivityProfile)getActivity()).setViewPager(0);
                    getActivity().getFragmentManager().popBackStackImmediate();
                    Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                //}
                //else{
                 //   Toast.makeText(getActivity(), R.string.ProfilVerificationToast, Toast.LENGTH_SHORT).show();
                //}
            }
        });

        //Reset button
        //Calls the FirebaseAuthentication manager for the reset method and blocks the screen with progress bar until finished with password reset. (sends an email for reset)
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
    public boolean allInputFilled(){
        boolean isFilled=true;
        if(FirstnameLabel.getText().toString().equals("")||LastnameLabel.getText().toString().equals("")|| PhoneLabel.getText().toString().equals("")|| radioGroup.getCheckedRadioButtonId()==-1){
            isFilled=false;
        }
       return isFilled;
    }

    public User getUserFromFragment(String firstname, String lastname, String phone, String gender){

        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        user.setEmergencyPhone(phone);
        return user;
    }


}

