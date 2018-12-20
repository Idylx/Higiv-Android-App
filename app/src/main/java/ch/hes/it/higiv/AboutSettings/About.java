package ch.hes.it.higiv.AboutSettings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import ch.hes.it.higiv.R;

public class About extends AppCompatActivity {

    private ImageView wheelImg1;
    private ImageView wheelImg2;
    private ImageView carImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        wheelImg1 = (ImageView) findViewById(R.id.wheel1);
        wheelImg2 = (ImageView) findViewById(R.id.wheel2);
        carImg =(ImageView) findViewById(R.id.Car);

        //animation for the rotation and deplacement of wheels
        final AnimationSet anim = new AnimationSet(true);
        Animation animWheel = AnimationUtils.loadAnimation(About.this, R.anim.rotate);
        Animation animWheel2 = AnimationUtils.loadAnimation(About.this, R.anim.lefttorightwheel);
        animWheel.restrictDuration(2800);
        anim.addAnimation(animWheel);
        anim.addAnimation(animWheel2);
        anim.setInterpolator(new LinearInterpolator());
        wheelImg1.startAnimation(anim);
        wheelImg2.startAnimation(anim);

        //animation for deplacement of car
        final Animation animRightCar = AnimationUtils.loadAnimation(About.this, R.anim.lefttorightcar);
        animRightCar.setInterpolator(new LinearInterpolator());
        carImg.startAnimation(animRightCar);
        //for restart the car after the stop
        animRightCar.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animRightCar2 = AnimationUtils.loadAnimation(About.this, R.anim.lefttorightcar2);
                carImg.startAnimation(animRightCar2);
                AnimationSet anim2 = new AnimationSet(true);
                Animation animWheel3 = AnimationUtils.loadAnimation(About.this, R.anim.rotate);
                Animation animWheel4 = AnimationUtils.loadAnimation(About.this, R.anim.lefttorightwheel2);
                animWheel3.restrictDuration(2800);
                anim2.addAnimation(animWheel3);
                anim2.addAnimation(animWheel4);
                anim2.setInterpolator(new LinearInterpolator());
                wheelImg1.startAnimation(anim2);
                wheelImg2.startAnimation(anim2);
                animRightCar2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //for restart the animation
                        carImg.startAnimation(animRightCar);
                        wheelImg1.startAnimation(anim);
                        wheelImg2.startAnimation(anim);
                    }
                });

            }
        });



    }
}
