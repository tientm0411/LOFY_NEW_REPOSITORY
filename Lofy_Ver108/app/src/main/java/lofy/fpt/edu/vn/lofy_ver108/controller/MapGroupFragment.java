package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.ImageLoadTask;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapGroupFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {
    private View rootView;
    private GoogleMap mMap;
    private FloatingActionButton fabNoti;

    private SharedPreferences mSharedPreferences;
    private String groupID = "";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference notiRef;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private DatabaseReference groupUserRef;

    private PermissionManager permissionManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private Marker mMarker;
    private ArrayList<Marker> alMarkerNoti; // list marker noti

    public MapGroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_group, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapGroup_map_1);
        mapFragment.getMapAsync(this);
        initView();
        askGrantLocationPermission(); // ask grant location permisstion
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        return rootView;
    }


    private void initView() {
        fabNoti = (FloatingActionButton) rootView.findViewById(R.id.fab_noti);
        fabNoti.setOnClickListener(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        notiRef = mFirebaseDatabase.getReference("notifications");
        groupRef = mFirebaseDatabase.getReference("groups");
        userRef = mFirebaseDatabase.getReference("users");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");


        alMarkerNoti = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap(mMap);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_noti:
                showDialogNoti();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // marker.showInfoWindow();
        //Log.d("NOTI_SIZE_2 :", alMarkerNoti.size() + "");
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //   marker.hideInfoWindow();

    }

    // set up map
    public void setUpMap(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        zoomToMyLocation(googleMap);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                zoomToMyLocation(googleMap);
                return false;
            }
        });
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        loadMarkerNoti(googleMap);
    }

    private ArrayList<Notification> alNoti;

    // load marker noti
    private void loadMarkerNoti(final GoogleMap googleMap) {
        alNoti = new ArrayList();
        notiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alNoti.clear();
                    alMarkerNoti.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Notification nt = ds.getValue(Notification.class);
                        if (nt.getGroupID().equals(groupID)) {
                            alNoti.add(new Notification(nt.getNotiID(), nt.getNotiName(), nt.getGroupID(), nt.getUserID(),
                                    nt.getNoti_type(), nt.getNoti_time(), nt.getNoti_icon(),
                                    nt.getNoti_content(), nt.getLatitude(), nt.getLongtitude(), nt.getMess()));
                        }
                    }
                }
                Log.d("NOTI_SIZE :", String.valueOf(alNoti.size()));
                if (!alNoti.isEmpty() && alNoti.size() > 0) {
                    // mMarker = null;
                    for (int i = 0; i < alNoti.size(); i++) {
                        ImageLoadTask imageLoadTask = new ImageLoadTask(rootView.getContext(), googleMap, alNoti.get(i));
                        imageLoadTask.execute();
                        alMarkerNoti.add(new ImageLoadTask().retriveMarkerNoti());
                    }
                }
                Log.d("NOTI_SIZE_2 :", alMarkerNoti.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // show noti
    private void showDialogNoti() {
        //  Toast.makeText(this, "Clicked !", Toast.LENGTH_SHORT).show();
        new DialogNotifyIcon(rootView.getContext(), getMyLocation()).show();

    }

    // zoom camera to my location
    private void zoomToMyLocation(GoogleMap googleMap) {
        Location location = getMyLocation();
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    // get lat lng my location
    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return null;
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;
    }

    // ask grant lccation permisstion
    private void askGrantLocationPermission() {
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(getActivity());
    }

    // initial api client
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(rootView.getContext())
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(rootView.getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //                        Toast.makeText(rootView.getContext(), "GPS đang bật !", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(rootView.getContext(), "GPS đang bật !", Toast.LENGTH_SHORT).show();
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Toast.makeText(rootView.getContext(), "GPS đang tắt !", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Toast.makeText(rootView.getContext(), "GPS đang bật !", Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(rootView.getContext(), "GPS đang tắt !", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        rootView.getContext().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gpsLocationReceiver != null)
            rootView.getContext().unregisterReceiver(gpsLocationReceiver);
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if(alMarkerNoti.size()>0){
            for(int i = 0; i <alMarkerNoti.size();i++){
                if(alMarkerNoti.get(i).equals(marker)){
                    marker.remove();
                }
            }
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
