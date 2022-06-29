package edu.ua.parkalot;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ParkActivity extends AppCompatActivity {
    private final String TAG = "PackActivity";
    String jsonFile = "parks.json";

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<ParkAdapter.Park> listItems = new ArrayList<ParkAdapter.Park>();


    //RECORDING HOW ITEMS
    int clickCounter=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_list);

        Log.d(TAG, "onCreate: started");

        for (int n=0;n<=listItems.size();n++) {
            clickCounter = n;
        }

        try {
            initParkAdapter();
            fillParkList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initParkAdapter() {
        Log.d(TAG, "initParkAdapter: init Park Adapter");
        RecyclerView rv_list_parks = findViewById(R.id.rv_list_parks);

        ParkAdapter adapter = new ParkAdapter(this, listItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rv_list_parks.setLayoutManager(layoutManager);
        rv_list_parks.setHasFixedSize(true);

        rv_list_parks.setAdapter(adapter);
    }

    //METHOD FOR INSERTION
    public void addNewItem(int v) {
        listItems.add( new ParkAdapter.Park(
                v,
                "",
                "Item "+v,
                null,
                null,
                10,
                "9h-18h",
                "public"
                ));
    }

    //LOAD JSON FILE
    private void fillParkList() {
        String json = null;

        try {
            InputStream is = getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            // Using Gson Library to create object
            Gson gObj = new Gson();
            ParkAdapter.Park[] parks = gObj.fromJson(json, ParkAdapter.Park[].class);
            for (ParkAdapter.Park park : parks) {
                listItems.add(park);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

