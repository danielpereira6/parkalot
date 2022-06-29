package edu.ua.parkalot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    private static final String TAG = InfoActivity.class.getSimpleName();

    TextView tv_park_title;
    TextView tv_info_area;
    TextView tv_info_sched;
    TextView tv_info_type;
    TextView tv_info_desc;
    ImageView park_image;
    ImageView image_type;

    String url = "https://gist.github.com/danielpereira6/6a3186c1bdf1c46a0a562c49b8d2bf0d/raw";
    RequestQueue queue;

    String src;
    String parkType;
    Uri uri;
    ParkAdapter.Park[] parks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_techinfo);

        tv_park_title = findViewById(R.id.tv_park_title);
        tv_info_area = findViewById(R.id.tv_info_area);
        tv_info_sched = findViewById(R.id.tv_info_sched);
        tv_info_type = findViewById(R.id.tv_info_type);
        tv_info_desc = findViewById(R.id.tv_info_desc);
        park_image = findViewById(R.id.park_image);
        image_type = findViewById(R.id.image_type);

        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(url, (res) -> {
            parks = loadJSONFromAsset(res);

            Intent intent = getIntent();
            Integer parkInfoID = intent.getIntExtra("parkID", intent.getIntExtra("parkID",0));
            Log.d(TAG, "parkInfoID -> " + parkInfoID);

            ParkAdapter.Park park = parks[parkInfoID-1];
            Log.d(TAG, "PARK -> " + park.getImg());

            if (park != null) {
                tv_park_title.setText(park.getName());
                tv_info_area.setText(park.getArea().getValue() + " " + park.getArea().getUnits());
                tv_info_sched.setText(String.valueOf(park.getSchedule()));

                src = park.getImg();
                if (src != null || src != "") {
                    /*uri = Uri.parse(src);
                    park_image.setImageURI(uri);*/
                    // For a simple view:
                    Glide.with(this).load(src).into(park_image);
                }

                parkType = park.getType();
                tv_info_type.setText(parkType);

                Log.d(TAG, "parkType -> " + parkType);
                if (parkType.equals("privado")) {
                    Uri uri = Uri.parse("android.resource://edu.ua.parkalot/drawable/presence_busy");
                    Log.e(TAG, "RED -> " + parkType);
                    // change image to prohibited
                    Glide.with(this).load(uri).into(image_type);
                }
                else {
                    Uri uri = Uri.parse("android.resource://edu.ua.parkalot/drawable/presence_online");
                    Log.d(TAG, "GREEN -> " + parkType);
                    // change image to free
                    Glide.with(this).load(uri).into(image_type);
                }
            }
        }, error -> {
            Log.e(TAG,"ERROR -> "+error);
        });
        queue.add(request);
    }

    private ParkAdapter.Park[] loadJSONFromAsset(String json) {
        // Using Gson Library to create object
        Gson gObj = new Gson();
        //String parks = gObj.toJson(json);
        ParkAdapter.Park[] parks = gObj.fromJson(json, ParkAdapter.Park[].class);

        return parks;
    }
}
