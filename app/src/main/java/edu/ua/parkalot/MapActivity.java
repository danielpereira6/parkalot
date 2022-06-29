package edu.ua.parkalot;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = MapActivity.class.getSimpleName();

    //String jsonFile = "parks.json"; // local file
    String url = "https://gist.github.com/danielpereira6/6a3186c1bdf1c46a0a562c49b8d2bf0d/raw";
    RequestQueue queue;

    ArrayList<ParkAdapter.Park> listItems = new ArrayList<ParkAdapter.Park>();
    ParkAdapter.Park[] parks;

    private GoogleMap mMap;
    private Place place1 = new Place("ESTGA", new LatLng(-23.5868031, -46.6843406), "Escola Superior de Tecnologia e Gestão de Águeda");
    private Place place2 = new Place("Park 1", new LatLng(40.5728649, -8.4447313), "Estacionamento do Mercado");

    private Marker markers;
    private HashMap<Marker, Integer> markerHashMap = new HashMap<Marker, Integer>();

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationClient;

    private LatLng mylocation;

    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(40.5749, -8.4448);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        queue = Volley.newRequestQueue(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        StringRequest request = new StringRequest(url, (res) -> {
            parks = loadJSONFromAsset(res);
            //Log.d(TAG, "PARKS ->" + parks);
            for (ParkAdapter.Park park : parks) {
                markers = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(park.getCoordinates().getLat(), park.getCoordinates().getLongitude()))
                        .title(park.getName())
                        .snippet(park.getCoordinates().getLat() + "," + park.getCoordinates().getLongitude()));
                markers.setTag(park.getId());
                markerHashMap.put(markers, park.getId());
                markers.showInfoWindow();
                Log.d(TAG, "PARK ID -> " + park.getId() + " | name: " + park.getName());
                Log.d(TAG, "PARKS coords -> " + park.getCoordinates().getLat() + "; " + park.getCoordinates().getLongitude());
                //Log.d(TAG, "markerHashMap -> " + markerHashMap);
            }
        }, error -> {
            Log.e(TAG,"ERROR -> "+error);
        });
        queue.add(request);

        // Set the map type to Hybrid.
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Set the map coordinates
        //LatLng ESTGA = new LatLng(40.5745066,-8.4438357);
        //Marker marker_estga;
        // Add a marker on the map coordinates.

        googleMap.addMarker(new MarkerOptions()
                .position(place2.getLatLng())
                .title(place2.getName()));


        /* getExtras */
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e(TAG, "Extra Keys -> " + key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        }

        Double lat_put = intent.getDoubleExtra("lat_coords", defaultLocation.latitude);
        //Double lat_put = Double.parseDouble(intent.getExtras().getString("lat_coords"));

        Double long_put = intent.getDoubleExtra("long_coords", defaultLocation.longitude);
        //Double long_put = Double.parseDouble(intent.getExtras().getString("long_coords"));

        Log.d(TAG, "getExtra -> Coords: " + lat_put + "," + long_put);

        if (lat_put >= 0) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat_put, long_put)));
        } else {
            // Move the camera to the map coordinates and zoom in closer.
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        }
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

        // Set a listener for marker click and windows info.
        //googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        // Display traffic.
        googleMap.setTrafficEnabled(true);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        //updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        //Mylocation();

    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            Log.d(TAG, "lastKnownLocation -> " + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());

                            if (lastKnownLocation != null) {
                                mylocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                Log.d(TAG, "mylocation -> " + mylocation);
                            } else {
                                mylocation = defaultLocation;
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            Log.d(TAG, "Default location -> " + defaultLocation);

                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            mylocation = defaultLocation;
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Called when the user clicks a marker.
     */
    //@Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Log.d(TAG, "Counter -> " + clickCount);
            //Log.d(TAG, "Marker -> "+marker.getTag());

            /* Fill mylocation variable */
            getDeviceLocation();

            String ESTGA = "40.5745066,-8.4438357";

            /* Gets the marker id on click */
            int markID = markerHashMap.get(marker);
            Log.d(TAG, "markID -> " + markID);

            ParkAdapter.Park park = parks[markID - 1];
            Log.d(TAG, "Selected Park -> " + park.getName());

            /* Create and Uri. Make the Intent. Start an activity that can handle the Intent  */
            String uri = "https://www.google.pt/maps/dir/" + mylocation.latitude + "," + mylocation.longitude + "/" + park.getCoordinates().getLat() + "," + park.getCoordinates().getLongitude();
            Log.d(TAG, "URL -> " + uri);

            /* Open Google maps */
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }

            /*String ESTGA = "40.5745066,-8.4438357";
             *//* Create and Uri. Make the Intent. Start an activity that can handle the Intent  */
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick -> " + marker);

        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();

        onMarkerClick(marker);

        //mMap.setOnMarkerClickListener(this);
    }

    //LOAD LOCAL JSON FILE
    private ParkAdapter.Park[] loadJSONFromAsset(String json) {
        // Using Gson Library to create object
        Gson gObj = new Gson();
        //String parks = gObj.toJson(json);
        ParkAdapter.Park[] parks = gObj.fromJson(json, ParkAdapter.Park[].class);
        for (ParkAdapter.Park park : parks) {
            listItems.add(park);
        }
        return parks;
    }

    static class Place {
        String name;
        LatLng latLng;
        String address;

        public Place(String name, LatLng latLng, String s) {
            this.name = name;
            this.latLng = latLng;
            this.address = s;
        }

        public String getName() {
            return name;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public String getAddress() {
            return address;
        }
    }
}
