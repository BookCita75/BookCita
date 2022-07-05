package com.dam.bookcita.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.dam.bookcita.R;
import com.dam.bookcita.login.LoginActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivSplash = findViewById(R.id.ivSplash);

        ivSplash.animate()
                .alpha(1)
                .rotation(1080)
                .setDuration(2000)
                .withEndAction(new Runnable() {
                    // pour que le logo s'arrete de tourner une seconde
                    @Override
                    public void run() {
                        ivSplash.animate()
                                .setDuration(1000)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Splash.this, ListeCitationsFromOneBookActivity.class));
                                    }
                                })
                                .start();


                    }
                })
                .start();
    }
}