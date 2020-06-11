package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity{
    private static int SPLASH_SCREEN_TIME_OUT=4000;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.splash);
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashScreen.this,signIn.class);

                startActivity(i);

                finish();
               }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
