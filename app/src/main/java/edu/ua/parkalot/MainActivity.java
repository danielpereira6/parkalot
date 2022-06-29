package edu.ua.parkalot;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When click on button List, change activity
        ImageView imgList = (ImageView) findViewById(R.id.imgInfo);
        imgList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent park_activity = new Intent(MainActivity.this, ParkActivity.class);
                startActivity(park_activity);
            }
        });

        // When click on button List, change activity
        ImageView imgLocation = (ImageView) findViewById(R.id.imgLocation);
        imgLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent map_activity = new Intent(MainActivity.this, MapActivity.class);
                startActivity(map_activity);
            }
        });
    }
}