package com.nerya.uber;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class viewLocations extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnride = findViewById(R.id.btnRide);

        btnride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(viewLocations.this, getIntent().getStringExtra("Rusername"), Toast.LENGTH_SHORT);
                ParseQuery<ParseObject> carRequest = ParseQuery.getQuery("request");
                carRequest.whereEqualTo("username", getIntent().getStringExtra("Rusername"));
                carRequest.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(objects.size() > 0 && e == null){
                            for(ParseObject object : objects){
                                object.put("asDriver", ParseUser.getCurrentUser().getUsername());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Intent googleIntent = new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("https://maps.google.com?saddr=" + getIntent().getDoubleExtra("driverLat",0 ) + ","
                                                            + getIntent().getDoubleExtra("driverLon", 0)
                                                            + "&" + "daddr=" + getIntent().getDoubleExtra("passengerLat", 0) + ","
                                                            + getIntent().getDoubleExtra("PassengerLon", 0)));
                                            startActivity(googleIntent);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Toast.makeText(this, getIntent().getDoubleExtra("driverLat",0) + "", Toast.LENGTH_LONG).show();
        LatLng dLocation = new LatLng(getIntent().getDoubleExtra("driverLat", 0), getIntent().getDoubleExtra("driverLon", 0));
//        mMap.addMarker(new MarkerOptions().position(dLocation).title("Driver Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(dLocation));

        LatLng pLocation = new LatLng(getIntent().getDoubleExtra("passengerLat", 0), getIntent().getDoubleExtra("passengerLon", 0));
//        mMap.addMarker(new MarkerOptions().position(pLocation).title("Passenger"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLocation));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker DriverMark = mMap.addMarker(new MarkerOptions().position(dLocation).title("driver location"));
        Marker PassMark = mMap.addMarker(new MarkerOptions().position(pLocation).title("passenger location"));

       ArrayList<Marker> myMarkers = new ArrayList<>();
        myMarkers.add(DriverMark);
        myMarkers.add(PassMark);
        for(Marker marker : myMarkers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,0);
        mMap.moveCamera(cameraUpdate);

    }
}
