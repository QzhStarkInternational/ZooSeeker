package com.example.sandiegozooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;

public class SplashActivity extends AppCompatActivity {

    AnimationDrawable logoAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Logo Animation initiation
        ImageView animatedLogo = findViewById(R.id.animatedLogo);
        animatedLogo.setBackgroundResource(R.drawable.logo_animation);
        logoAnimation = (AnimationDrawable) animatedLogo.getBackground();
        logoAnimation.start();

        // New thread for timer (AnimationDrawable does not provide a onFinish Method)
        Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish(); // destroy activity on change
                        });
                        t.cancel(); // destroy timer if garbage collector hasn't done so yet (memory efficient)
                    }
                },
                2600
        );


    }
}