package edu.ua.parkalot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    //ArrayList<String> listNames = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    //ArrayAdapter<String> adapter;

    //RECORDING HOW ITEMS
    int clickCounter=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_list);

        Log.d(TAG, "onCreate: started");

        //listItems.add( new ParkAdapter.Park("Item 1") );
        //listItems.add( new ParkAdapter.Park("Item 2") );
        //listItems.add( new ParkAdapter.Park("Item 3") );
        //listItems.add( new ParkAdapter.Park("Item 4") );

        for (int n=0;n<=listItems.size();n++) {
            clickCounter = n;
        }

        try {
            initParkAdapter();
            fillParkList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //// ADD PARK
        /*Button btnAddPark = (Button) findViewById(R.id.btnAddPark);
        btnAddPark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNewItem(clickCounter);
                System.out.println(clickCounter);
                clickCounter++;
                initParkAdapter();
            }
        });*/

        //// Item Click
        /*listItems.indexOf(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: clicked on item from listItems" );
                setContentView(R.layout.park_list);
            }
        });*/
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
        //listNames.add("Name "+v);
        //adapter.notifyDataSetChanged();
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

            /*JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject obj = jsonArray.optJSONObject(i);
                //if (obj.getString(i)) {
                listItems.add(new ParkAdapter.Park(
                        obj.getString("name")
                ));

                //}
                Toast.makeText(getApplicationContext(), listItems.toString(), Toast.LENGTH_LONG).show();
                clickCounter++;
            }*/

            // Using Gson Library to create object
            Gson gObj = new Gson();
            ParkAdapter.Park[] parks = gObj.fromJson(json, ParkAdapter.Park[].class);
            for (ParkAdapter.Park park : parks) {
                listItems.add(park);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } /*catch (JSONException ex) {
            ex.printStackTrace();
        }*/

    }
}

