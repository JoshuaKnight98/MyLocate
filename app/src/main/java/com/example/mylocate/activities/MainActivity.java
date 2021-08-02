package com.example.mylocate.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mylocate.R;
import com.example.mylocate.fragments.MapsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnGetMyLocation;
    private Button btnGoMyLocation;
    private TextView textViewLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectUI();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        btnGetMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check permissions
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(Task<Location> task) {
                            //get location for the task results
                            location = task.getResult();
                            if(location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();

                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                    textViewLocation.setText("latitude: " + latitude + "\n" +
                                            "longitude: " + longitude + "\n" + "Country: " + addresses.get(0).getCountryName());


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
                }else{
                    //ask for permissions
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        btnGoMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new MapsFragment(new LatLng(location.getLatitude(), location.getLongitude())));
            }
        });

    }


    private void connectUI(){
        btnGetMyLocation = findViewById(R.id.btnGetLocation);
        textViewLocation = findViewById(R.id.textLocation);
        btnGoMyLocation = findViewById(R.id.btnGoLocation);
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, fragment)
                .commit();
    }

}