package com.hitvardhan.project_app.activity;

/**
 * Created by Hitvardhan on 08-12-2016.
 *
 * Splash Activity: First Screen to come up on UI and last for 2 seconds
 */

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.hitvardhan.project_app.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        super.onCreate(savedInstanceState);



        //Handler to make the screen last for 2 sec
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}