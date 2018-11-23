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


public class EditProfilFragment extends Fragment {
    private EditText FirstnameLabel;
    private EditText LastnameLabel;
    private EditText EmailLabel;
    private EditText PasswordLabel;

    private Button EditButton;

    private RadioButton RadioMen;
    private RadioButton RadioWomen;
    private RadioButton RadioOther;


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


        EditButton = (Button) view.findViewById(R.id.EditButton);

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase
                //Go back to profile fragment
                ((ActivityProfile)getActivity()).setViewPager(0);
            }
        });


        return  view;
    }
}

