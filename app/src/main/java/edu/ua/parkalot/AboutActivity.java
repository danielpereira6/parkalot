package edu.ua.parkalot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_about);

        TextView tv_about_title = (TextView) findViewById(R.id.tv_about_title);

        Intent intent = getIntent();
        String txt_put = intent.getStringExtra("Title");
        tv_about_title.setText(txt_put);

        Double lat_coords = intent.getDoubleExtra("lat_coords", 40);
        Double long_coords = intent.getDoubleExtra("long_coords", -8);

        Bundle bundle = intent.getExtras();
        /*if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, "Extra Keys -> "+ key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }*/
        if (bundle != null) {
            lat_coords = new Double(bundle.get("lat_coords").toString());
            long_coords = new Double(bundle.get("long_coords").toString());
            Log.e(TAG, "bundle.get(LAT + LONG) -> " + lat_coords +","+ long_coords);
        }
        Double final_lat_coords = lat_coords;
        Double final_long_coords = long_coords;

        ImageView imgLocation = (ImageView) findViewById(R.id.imgLocation);
        imgLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent map_activity = new Intent(AboutActivity.this, MapActivity.class);

                map_activity.putExtra("lat_coords", final_lat_coords);
                map_activity.putExtra("long_coords", final_long_coords);

                startActivity(map_activity);
            }
        });

        ImageView imgInfo = (ImageView) findViewById(R.id.imgInfo);
        imgInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent info_activity = new Intent(AboutActivity.this, InfoActivity.class);
                info_activity.putExtra("parkID", intent.getIntExtra("parkID",0));
                startActivity(info_activity);
            }
        });
    }
}
