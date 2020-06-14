package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity{
    private static int SPLASH_SCREEN_TIME_OUT=4000;
    private static String TAG = "splashscreen";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser() ;
    String id = mAuth.getUid();
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
               if(user !=null){
                   Log.e(TAG, "run: user found");
                   Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                   startActivity(intent);
                   finish();
               }else {
                   Log.e(TAG, "run: user not found");
                   Intent i=new Intent(SplashScreen.this,signIn.class);
                   startActivity(i);
                   finish();
               }

           }
       }, SPLASH_SCREEN_TIME_OUT);
    }
}
