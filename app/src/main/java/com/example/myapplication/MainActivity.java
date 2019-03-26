package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {
private Button but_1;
private Button but_2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    but_1=findViewById(R.id.but_1);
    but_2=findViewById(R.id.button2);
    but_1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent a=new Intent();
            a.setClass(MainActivity.this,Main2Activity.class);
            startActivity(a);
        }
    });
    but_2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent a=new Intent();
            a.setClass(MainActivity.this,Main3Activity.class);
            startActivity(a);
        }
    });

    }

}
