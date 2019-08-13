package com.nerya.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class driverRequestList extends AppCompatActivity implements AdapterView.OnItemClickListener {
private Button refresh;
private LocationManager locationManager;
private LocationListener locationListener;
private ListView listView;
private ArrayList<String> driveRequests;
private ArrayList<Double> passLat;
private ArrayList<Double> passLon;
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);

        listView = findViewById(R.id.requestList);
        driveRequests = new ArrayList<>();
        adapter = new ArrayAdapter(driverRequestList.this, android.R.layout.simple_list_item_1, driveRequests);
        refresh = findViewById(R.id.updateList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        driveRequests.clear();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateRequests(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if(Build.VERSION.SDK_INT < 23) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);
                    Location drLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateRequests(drLoc);
                }else {
                    if(ContextCompat.checkSelfPermission(driverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(driverRequestList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

                    }else{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location drLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        updateRequests(drLoc);
                    }
                }
            }

        });

        if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        passLat = new ArrayList<>();
        passLon = new ArrayList<>();
    }

    private void updateRequests(Location drLoc) {
        if(drLoc != null) {


            final ParseGeoPoint driverLoc = new ParseGeoPoint(drLoc.getLatitude(), drLoc.getLongitude());
            final ParseQuery<ParseObject> requestCar = ParseQuery.getQuery("request");
            requestCar.whereNear("passloc", driverLoc);

            requestCar.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                    if (objects.size() > 0) {
                        if(driveRequests.size() > 0) {
                            driveRequests.clear();
                        }
                        if(passLon.size() > 0) {
                            passLon.clear();
                        }
                        if(passLat.size() > 0) {
                            passLat.clear();
                        }
                        for (ParseObject nearestReq : objects) {
                            ParseGeoPoint pLoc = (ParseGeoPoint)  nearestReq.get("passloc");

                            Double kilometersDistPas = driverLoc.distanceInKilometersTo(pLoc);
                            float RoundedDist = Math.round(kilometersDistPas * 10) / 10;
                            driveRequests.add("there are " + RoundedDist + " kilometers to " + nearestReq.get("username"));
                            passLat.add(pLoc.getLatitude());
                            passLon.add(pLoc.getLongitude());
                        }

                    }else {
                        Toast.makeText(driverRequestList.this, "Sorry, no requests yet...", Toast.LENGTH_SHORT).show();
                    }
                        adapter.notifyDataSetChanged();
                }
            }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(driverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location drLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateRequests(drLoc);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menua, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(driverRequestList.this, "logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }
}
