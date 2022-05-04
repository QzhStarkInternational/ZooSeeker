package com.example.sandiegozooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;

import java.util.Timer;

public class LoadActivity extends AppCompatActivity {
    AnimatedVectorDrawable logoAnimation;
    boolean keepSplashOn = true;

    boolean keepSplashOnScreen = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
            SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
            splashScreen.setKeepOnScreenCondition (()-> keepSplashOnScreen);

            Timer it = new java.util.Timer();
            it.schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    keepSplashOnScreen = false;
                                    Intent mainActivityIntent = new Intent(LoadActivity.this, MainActivity.class);
                                    startActivity(mainActivityIntent);
                                    finish();
                                });
                                it.cancel(); // destroy timer if garbage collector hasn't done so yet (memory efficient)
                            }
                        },
                    1400
            );
        } else {
            keepSplashOnScreen = false;
            setContentView(R.layout.activity_load_screen);

            // Logo Animation initiation < Version 31
            ImageView animatedLogo = findViewById(R.id.animatedLogo);
            animatedLogo.setBackgroundResource(R.drawable.logo_animated_vector_full);
            logoAnimation = (AnimatedVectorDrawable) animatedLogo.getBackground();
            logoAnimation.start();

            // New thread for timer (AnimationDrawable does not provide a onFinish Method)
            Timer t = new java.util.Timer();
            t.schedule(new java.util.TimerTask() {
                           @Override
                           public void run() {
                               runOnUiThread(() -> {
                                   Intent mainActivityIntent = new Intent(LoadActivity.this, MainActivity.class);
                                   startActivity(mainActivityIntent);
                                   finish(); // destroy activity on change
                               });
                               t.cancel(); // destroy timer if garbage collector hasn't done so yet (memory efficient)
                           }
                       },
                    1400
            );
        }

        VertexDatabase db = VertexDatabase.getSingleton(getApplicationContext());
    }
}