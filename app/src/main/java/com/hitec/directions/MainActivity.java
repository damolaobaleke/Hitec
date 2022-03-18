package com.hitec.directions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hitec.directions.views.map.MapActivity;

public class MainActivity extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        Runnable run  = () -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        };

        handler.postDelayed(run, 1000);

    }
}