package ch.hes.it.higiv.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

//Handles the reset Password Authentication method
//Uses the FirebasCallBack interface for the async
public class FirebaseAuthentication {

    private FirebaseAuth auth  = FirebaseAuth.getInstance();

    public void resetPassword(String email, final FirebaseCallBack firebaseCallBack){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseCallBack.onCallBack(task);
                    }
                });
    }


}
