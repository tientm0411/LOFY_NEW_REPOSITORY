package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinder;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinderListener;
import lofy.fpt.edu.vn.lofy_ver108.Modules.Route;
import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.ImageLoadTask;
import lofy.fpt.edu.vn.lofy_ver108.business.LoadMarkerMemberAsyntask;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapGroupFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener, DirectionFinderListener {
    private View rootView;
    private GoogleMap mMap;
    private Circle mapCircle;
    private FloatingActionButton fabNoti;

    private SharedPreferences mSharedPreferences;
    private String groupID = "";
    private List<Marker> tenMarkers = new ArrayList<>();
    private String userID = "";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference notiRef;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private DatabaseReference groupUserRef;
    private MapMethod mapMethod;

    private PermissionManager permissionManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private Marker mMarker;
    private ArrayList<Marker> alMarkerNoti; // list marker noti
    private ArrayList<Marker> alMarkerMember; // list marker member
    private ArrayList<GroupUser> alGroupUser; // list group user
    private ArrayList<User> alUser; // list user
    private String ciCode;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<List> sumPaths = new ArrayList<>();
    private List<List> sumRoutes = new ArrayList<>();
    private List<Route> routes = new ArrayList<>();

    public MapGroupFragment() {
    }

    public void setCode(String code) {
        ciCode = code;
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

        Intent intent = new Intent(rootView.getContext(), GPS_Service.class);
        rootView.getContext().startService(intent);
        PowerManager mgr = (PowerManager) rootView.getContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

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
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        mapMethod = new MapMethod(rootView.getContext());
        mapCircle = null;
        alMarkerNoti = new ArrayList<>();
        final DatabaseReference newRef = groupRef.child(groupID);
        final DirectionFinder directionFinder = new DirectionFinder(this, "", "", null, "");
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                directionFinder.setOrigin(dataSnapshot.child("start_Lat").getValue().toString() + "," + dataSnapshot.child("start_Long").getValue().toString());
//                directionFinder.setDestination(dataSnapshot.child("end_Lat").getValue().toString() + "," + dataSnapshot.child("end_Long").getValue().toString());
//                try {
//                    directionFinder.execute();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                List<List> listLatLng = new ArrayList<>();
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.child("paths").getChildren()) {
                    for (DataSnapshot dsp2 : dsp.child("points").getChildren()) {
                        List<String> point = new ArrayList<>();
                        for (DataSnapshot dsp3 : dsp2.getChildren()) {
                            point.add(String.valueOf(dsp3.getValue())); //add result into array list
                        }
                        listLatLng.add(point);
                    }
                }
                Log.d("childcount", listLatLng + "");
                PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
                for (int i = 0; i < listLatLng.size(); i++) {
                    String[] latlong = listLatLng.get(i).toString().split(",");
                    double latitude = Double.parseDouble(latlong[0].trim().substring(1));
                    double longitude = Double.parseDouble(latlong[1].trim().substring(0, latlong[1].length() - 2));
                    LatLng point = new LatLng(latitude, longitude);
                    polylineOptions.add(point).color(Color.BLUE).zIndex(10).width(20);
                }
                Polyline polyline = mMap.addPolyline(polylineOptions);
                polylinePaths.add(polyline);
                Log.d("hasagi", polyline + "");
                polyline.setClickable(true);

                List<List> listMarker = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.child("restPoints").getChildren()) {
                    List<String> point = new ArrayList<>();
                    for (DataSnapshot dsp2 : dsp.getChildren()) {
                        point.add(String.valueOf(dsp2.getValue())); //add result into array list
                    }
                    listMarker.add(point);
                }
                for (int i = 0; i < listMarker.size(); i++) {
                    String[] latlong = listMarker.get(i).toString().split(",");
                    double latitude = Double.parseDouble(latlong[0].trim().substring(1));
                    double longitude = Double.parseDouble(latlong[1].trim().substring(0, latlong[1].length() - 2));
                    LatLng point = new LatLng(latitude, longitude);
                    Marker myMarker = mMap.addMarker(new MarkerOptions().position(point).title("Rest here!!!"));
                    tenMarkers.add(myMarker);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap(mMap);
        loadMarkerNoti(mMap);

        loadLocationMember(mMap);
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
    }

    private ArrayList<Notification> alNoti; // list noti

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
                if (!alNoti.isEmpty() && alNoti.size() > 0) {
                    // mMarker = null;
                    for (int i = 0; i < alNoti.size(); i++) {
                        ImageLoadTask imageLoadTask = new ImageLoadTask(rootView.getContext(), googleMap, alNoti.get(i));
                        imageLoadTask.execute();
                        alMarkerNoti.add(new ImageLoadTask().retriveMarkerNoti());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // load location member
    private void loadLocationMember(final GoogleMap googleMap) {
//        if (googleMap == null) {
//            Toast.makeText(rootView.getContext(), "Google map null ! ", Toast.LENGTH_SHORT).show();
//        } else {
        alGroupUser = new ArrayList<>();
        alMarkerMember = new ArrayList<>();
        alUser = new ArrayList<>();
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GroupUser groupUser = ds.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(groupID) && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                    }
                    Log.d("alGroupUser: ", alGroupUser.size() + " ");
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            alUser.clear();
                            if (dataSnapshot.hasChildren()) {
                                if (!alMarkerMember.isEmpty() && alMarkerMember.size() > 0) {
                                    for (int i = 0; i < alMarkerMember.size() - 1; i++) {
                                        // alMarkerMember.get(i).setVisible(true);
                                        alMarkerMember.get(i).remove();
                                    }
                                }
                                alMarkerMember.clear();
                                LatLng lng;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User u = ds.getValue(User.class);
                                    for (int i = 0; i < alGroupUser.size(); i++) {
                                        if (u.getUserId().equals(alGroupUser.get(i).getUserId()) && alGroupUser.get(i).isStatusUser() == true) {
                                            alUser.add(u);
                                        }
                                    }
                                }
                                Log.d("alUser: ", alUser.size() + "");
                                Log.d("userID: ", userID);
                                if (!alUser.isEmpty() && alUser.size() > 0) {
                                    for (int i = 0; i < alUser.size(); i++) {
                                        if (!alUser.get(i).getUserId().equals(userID)) {
                                            lng = new LatLng(alUser.get(i).getUserLati(), alUser.get(i).getUserLongti());
                                            Log.d("alPing", "lng: "+lng );
                                            Marker markerMem = googleMap.addMarker(new MarkerOptions()
                                                    .position(lng)
                                                    .title(alUser.get(i).getUserName())
                                                    .icon(BitmapDescriptorFactory
                                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                            alMarkerMember.add(markerMem);
                                            Log.d("alPing ", "alMarkerMember1: "+alMarkerMember.size());
                                        }
                                        if (alGroupUser.get(i).isHost() == true && alGroupUser.get(i).isStatusUser() == true) {
                                            mapMethod.showCircleToGoogleMap(googleMap, mapCircle, new LatLng(alUser.get(i).getUserLati(), alUser.get(i).getUserLongti()), 5);
                                            Log.d("alPing", "mapCircle: " + mapMethod.showCircleToGoogleMap(googleMap, mapCircle, new LatLng(alUser.get(i).getUserLati(), alUser.get(i).getUserLongti()), 5));
                                        }
                                    }
                                    Log.d("alPing: ", "alMarkerMember2: " +alMarkerMember.size());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // }
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
        marker.remove();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        int minDistanceIndex = 0;
        int minDistance = Integer.MAX_VALUE;
        for (Route route : routes) {
            // get min distance
            int distance = route.distance.value;
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceIndex = routes.indexOf(route);
            }
        }

        Log.d("123", minDistance + "");

        for (final Route route : routes) {
            LatLng camCover = new LatLng(route.startLocation.latitude, route.endLocation.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camCover, 7));

//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
//                    .title(route.startAddress)
//                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
//                    .title(route.endAddress)
//                    .position(route.endLocation)));
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.BLUE).
//                    width(18);
//            PolylineOptions polylineOptionsGray = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.GRAY).
//                    width(14);
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);

            if (routes.indexOf(route) != minDistanceIndex) {
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i)).color(Color.GRAY).width(14).zIndex(9);
                }
            } else {
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i)).color(Color.BLUE).width(18).zIndex(10);
                }


            }

            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylinePaths.add(polyline);
            polyline.setClickable(true);


//            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//                @Override
//                public void onPolylineClick(Polyline polyline) {
//                    // make color
////                    blueId[0] = polyline.getId();
////                    for (int i = 0; i < polylinePaths.size(); i++) {
////                        if (!blueId[0].equals(polylinePaths.get(i).getId())) {
////                            polylinePaths.get(i).setColor(Color.GRAY);
////                            polylinePaths.get(i).setZIndex(9);
////                            polylinePaths.get(i).setWidth(14);
////                        } else {
////                            polyline.setColor(Color.BLUE);
////                            polylinePaths.get(i).setZIndex(10);
////                            polyline.setWidth(18);
////                            Log.d("polyid", polyline.getId());
////                        }
////                    }
//                    for (int i = 0; i < polylinePaths.size(); i++) {
//                        if (polyline.getId().equals(polylinePaths.get(i).getId())) {
//                            polyline.setColor(Color.BLUE);
//                            polylinePaths.get(i).setWidth(18);
//                            polylinePaths.get(i).setZIndex(10);
//                        } else {
//                            polylinePaths.get(i).setColor(Color.GRAY);
//                            polylinePaths.get(i).setWidth(14);
//                            polylinePaths.get(i).setZIndex(9);
//
//                        }
//                    }
//
//                    Log.d("poliId", polyline.getId());
//                }
//            });
//                final String[] polyId = {""};
            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    // make color
//                        polyId[0] = polyline.getId();
//                    for (int u = 0; u < sumPaths.size(); u++) {
//                        Log.d("sumpaths", sumPaths.size() + "");
//                    }
                    String id = polyline.getId();
                    for (int u = 0; u < sumPaths.size(); u++) {
                        List<Polyline> containedOne = new ArrayList<>();
                        polylinePaths = sumPaths.get(u);
                        for (int i = 0; i < polylinePaths.size(); i++) {
                            if (id.equals(polylinePaths.get(i).getId())) {
                                containedOne = polylinePaths;
                            }
                        }
                        for (int i = 0; i < polylinePaths.size(); i++) {
                            if (id.equals(polylinePaths.get(i).getId())) {
                                polyline.setColor(Color.BLUE);
                                polyline.setZIndex(10);
                                polyline.setWidth(18);

//                                if (durations.containsKey(id) && distances.containsKey(id)) {
//                                    ((TextView) findViewById(R.id.tv_map_duration)).setText(durations.get(id));
//                                    ((TextView) findViewById(R.id.tv_map_distance)).setText(distances.get(id));
//                                }
                            } else if (!id.equals(polylinePaths.get(i).getId()) && containedOne.equals(polylinePaths)) {
                                polylinePaths.get(i).setColor(Color.GRAY);
                                polylinePaths.get(i).setZIndex(9);
                                polylinePaths.get(i).setWidth(14);
                            }
                        }
                    }
                }
            });
        }
    }
}
