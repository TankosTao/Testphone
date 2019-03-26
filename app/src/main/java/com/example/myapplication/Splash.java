package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
//getSupportActionBar().hide();

   Thread mythread=new Thread()
   {
       @Override
       public void run() {
           try {
               sleep(2000);
               Intent it =new Intent(getApplicationContext(),MainActivity.class);
               startActivity(it);
               finish();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
   };
   mythread.run();
    }
}
