package com.example.apple.fyp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread=new Thread()
        {
            public  void run()
            {
                try
                   {
                       sleep(1000);
                   }
                   catch (Exception e)
                   {

                   }
                   finally {

                    Intent i = new Intent(getApplicationContext(), configureActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };

        thread.start();

    }
}
