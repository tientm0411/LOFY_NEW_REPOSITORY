package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class GPS_Service extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int REQUEST_LOCATION = 1111;
    private double mLatitude;
    private double mLongtitude;
    private LocationManager locationManager;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;
    private SharedPreferences mSharedPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mSharedPreferences = getApplication().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        final String userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID,"NA");
        String provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLatitude = location.getLatitude();
                mLongtitude = location.getLongitude();
              //  Toast.makeText(getBaseContext(), mLatitude + " " + mLongtitude, Toast.LENGTH_SHORT).show();
                try {
                    updateLatLngToFirebase(location,userID);
                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        });

        return START_STICKY;
    }


    // update LatLng to firebase
    public void updateLatLngToFirebase(Location location,String userID) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference("users");

        userRef.child(userID).child("userLati").setValue(location.getLatitude());
        userRef.child(userID).child("userLongti").setValue(location.getLatitude());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
