package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button button_location, button1;
    TextView latitude, longitude;
    LocationManager locationManager;

    private SensorManager sm;
    private float acelVal;
    private float acelLast;
    private float shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

        button_location = findViewById(R.id.locatebutton);
        button1 = findViewById(R.id.openbutton);
        latitude = findViewById(R.id.lat);
        longitude = findViewById(R.id.lon);

        // Runtime permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingAnimation();
                locateMe();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }

                }, 5000);

            }

        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocation();

            }
        });
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x  = event.values[0];
            float y  = event.values[1];
            float z  = event.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;

            if (shake > 10){
                double randlat = -90 + Math.random() * (90 - (-90));
                double randlon = -180 + Math.random() * (180 - (-180));
                //latitude.setText(String.valueOf(randlat));
                //longitude.setText(String.valueOf(randlon));
                String uri = String.format(Locale.ENGLISH, "geo: %f, %f", randlat, randlon);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void openLocation() {

        String latit = latitude.getText().toString();
        String lonit = longitude.getText().toString();
        try{

            String uri = String.format(Locale.ENGLISH, "geo: %f, %f", Double.valueOf(latit), Double.valueOf(lonit));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);


            /*
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> adresses = geocoder.getFromLocation(Double.valueOf(latit), Double.valueOf(lonit), 1);
            String address = adresses.get(0).getAddressLine(0);
            String city = adresses.get(0).getLocality();
            String country = adresses.get(0).getCountryName();

            Intent intent = new Intent(MainActivity.this, Activity2.class);
            intent.putExtra("keyaddr", address);
            intent.putExtra("keycity", city);
            intent.putExtra("keylat", Double.valueOf(latit));
            intent.putExtra("keylon", Double.valueOf(lonit));
            intent.putExtra("keycountry", country);
            startActivity(intent);
             */

        }catch(Exception e){
            Toast toast = Toast.makeText(this, "Enter valid coordinates", Toast.LENGTH_SHORT);
            toast.show();
            //e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    private void locateMe() {

        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(this, ""+location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();

        try{
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            //List<Address> adresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            //String address = adresses.get(0).getAddressLine(0);
            //String city = adresses.get(0).getLocality();
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            //latitude.setText(String.valueOf(lat));
            //longitude.setText(String.valueOf(lon));

            String uri = String.format(Locale.ENGLISH, "geo: %f, %f", lat, lon);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}