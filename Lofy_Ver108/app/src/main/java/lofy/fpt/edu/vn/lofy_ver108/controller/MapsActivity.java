package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lofy.fpt.edu.vn.lofy_ver108.Modules.DataParser;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinder;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinderListener;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DownloadURL;
import lofy.fpt.edu.vn.lofy_ver108.entity.Route;
import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.PlaceArrayAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private MapMethod mapMethod;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoCompleteTextView mAutocompleteTextView2;
    private AutoCompleteTextView mAutocompleteTextView3;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button btnFindPath;
    private Button btnAddRest;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private TextView tvDuration;
    private TextView tvDistance;
    private TextView mNameView;
    private Marker myMarker = null;
    private boolean isClicked = false;
    private SharedPreferences mSharedPreferences;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference group;
    private List<Marker> tenMarkers = new ArrayList<>();
    private List<Marker> listNearby = new ArrayList<>();

    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<List> sumPaths = new ArrayList<>();
    private List<List> sumRoutes = new ArrayList<>();
    private List<Route> routes = new ArrayList<>();
    private List<Route> listArc = new ArrayList<>();
    private List<LatLng> listRest = new ArrayList<>();
    private String groupID = "";
    private String groupName = "";
    private int sumDuration = 0, sumDistance = 0;
    String durDisp = "", disDisp = "";
    private Button btnSaveTrack;
    private static final int REQUEST_LOCATION = 1111;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private String ciCode;

    private String userID = "";
    private DatabaseReference notiRef;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private DatabaseReference groupUserRef;

    private Button btnReset;
    private Button btnNearbyHospital;
    private Button btnNearbyGastation;
    private Button btnNearbyRestaurant;

    private String googlePlacesData;
    String url;
    private boolean isAlertShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMain2);
        mapFragment.getMapAsync(this);
        askGrantLocationPermission(); // ask grant location permisstion
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        Bundle bundle = getIntent().getExtras();
        ciCode = bundle.getString("groupId");
        Log.d("cCode:", ciCode);
        initView();
    }

    private PermissionManager permissionManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    // ask grant lccation permisstion
    private void askGrantLocationPermission() {
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
    }    // initial api client

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

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
                            status.startResolutionForResult((Activity) getApplicationContext(), REQUEST_CHECK_SETTINGS);
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


    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
    private void initView() {
        btnFindPath = (Button) findViewById(R.id.btn_main2_findPath);
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        mAutocompleteTextView3 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        tvDistance = (TextView) findViewById(R.id.tv_map_distance);
        tvDuration = (TextView) findViewById(R.id.tv_map_duration);
        mAutocompleteTextView2.setThreshold(3);
        mAutocompleteTextView3.setThreshold(3);
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        //groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        btnSaveTrack = (Button) findViewById(R.id.btn_main2_saveTrack);
        btnAddRest = (Button) findViewById(R.id.btn_main2_addRest);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        group = mFirebaseDatabase.getReference("groups");
        btnSaveTrack.setOnClickListener(this);
        btnAddRest.setOnClickListener(this);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView2.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView3.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        mAutocompleteTextView2.setAdapter(mPlaceArrayAdapter);
        mAutocompleteTextView3.setAdapter(mPlaceArrayAdapter);
        mAutocompleteTextView3.setVisibility(View.GONE);

        btnReset = (Button) findViewById(R.id.btn_main2_Reset);
        btnNearbyHospital = (Button) findViewById(R.id.btn_main2_nearbyHospital);
        btnNearbyGastation = (Button) findViewById(R.id.btn_main2_nearbyGasStation);
        btnNearbyRestaurant = (Button) findViewById(R.id.btn_main2_nearbyRestaurant);
        btnReset.setOnClickListener(this);
        btnNearbyGastation.setOnClickListener(this);
        btnNearbyHospital.setOnClickListener(this);
        btnNearbyRestaurant.setOnClickListener(this);
        btnNearbyGastation.setVisibility(View.GONE);
        btnNearbyHospital.setVisibility(View.GONE);
        btnNearbyRestaurant.setVisibility(View.GONE);
        mapMethod = new MapMethod(this);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                isAlertShow = false;
                sendRequest();
            }
        });
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void initView2() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        notiRef = mFirebaseDatabase.getReference("notifications");
        groupRef = mFirebaseDatabase.getReference("groups");
        userRef = mFirebaseDatabase.getReference("users");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        mSharedPreferences = this.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        mapMethod = new MapMethod(this);
        final DatabaseReference newRef = groupRef.child(groupID);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                    myMarker = mMap.addMarker(new MarkerOptions().position(point).title("Rest here!!!"));
                    tenMarkers.add(myMarker);
                    listRest.add(point);
                }
                try {
                    mAutocompleteTextView.setText(dataSnapshot.child("origin").getValue().toString());
                    mAutocompleteTextView2.setText(dataSnapshot.child("destination").getValue().toString());
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendRequest() {

        DatabaseReference newRef = group.child(ciCode.toUpperCase());
        String destination = mAutocompleteTextView2.getText().toString();
        String rest = mAutocompleteTextView3.getText().toString();

        if (!rest.isEmpty() && tenMarkers.size() < 10) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(mAutocompleteTextView3.getText().toString(), 1);
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                myMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Vị trí không xác định"));
                tenMarkers.add(myMarker);
                listRest.add(myMarker.getPosition());
                if (!groupID.toString().equals("NA")) {
                    newRef.child("restPoints").setValue(listRest);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            mAutocompleteTextView3.setText("");
            mAutocompleteTextView3.setVisibility(View.GONE);
            btnNearbyGastation.setVisibility(View.GONE);
            btnNearbyHospital.setVisibility(View.GONE);
            btnNearbyRestaurant.setVisibility(View.GONE);
        } else {

        }

        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAutocompleteTextView.getText().toString().isEmpty()) {
            mAutocompleteTextView.setText(mapMethod.getMyLocation().latitude + "," + mapMethod.getMyLocation().longitude);
        }

        /* If myMarker null, only find direction between origin and destination.
           Else, first from origin to myMarker, and so on to destination. */
        if (tenMarkers.size() == 0) {
            try {
                new DirectionFinder(this, mAutocompleteTextView.getText().toString(), null, mAutocompleteTextView2.getText().toString()).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else
            try {
                new DirectionFinder(this, mAutocompleteTextView.getText().toString(), tenMarkers, mAutocompleteTextView2.getText().toString()).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        autoHideKeyboard();
        initView2();
    }

    private void autoHideKeyboard() {
        mAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        mAutocompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
        mAutocompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();


        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    HashMap<String, String> durations = new HashMap<>();
    HashMap<String, String> distances = new HashMap<>();

    @Override
    public void onDirectionFinderStart() {
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setMessage("Đang tìm hành trình...!");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        progressDialog.show();
        Runnable progressRunnable = () -> {
            progressDialog.setTitle("Đã xảy ra lỗi");
            progressDialog.setMessage("Vui lòng thử lại!");
            return;
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 15000);

        if (polylinePaths != null) {
            isClicked = false;

            sumDistance = 0;
            sumDuration = 0;
            for (int i = 0; i < sumPaths.size(); i++) {
                for (Polyline polyline : (List<Polyline>) sumPaths.get(i)) {
                    polyline.remove();
                }
            }
            routes.clear();
            sumRoutes.clear();
            sumPaths.clear();
        }
    }


    @Override
    public void onDirectionFinderSuccess(final List<Route> routes) {
        progressDialog.dismiss();
        this.routes = routes;
        if (routes.size() == 0) {
            if (isAlertShow == false) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Đã xảy ra lỗi...")
                        .setMessage("Vui lòng kiểm tra lại các vị trí không có đường trên bản đồ")
                        .setPositiveButton("Xác nhận", null)
                        .create().show();
                isAlertShow = true;
            }
            return;
        }
        polylinePaths = new ArrayList<>();

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
        Log.d("routesize", routes.size() + "");
        durations.clear();
        distances.clear();

        for (final Route route : routes) {
            LatLng camCover = new LatLng(route.startLocation.latitude, route.endLocation.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camCover, 7));

            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);

            if (routes.indexOf(route) != minDistanceIndex) {
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i)).color(Color.GRAY).width(14).zIndex(9);
                }
            } else {
                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i)).color(Color.BLUE).width(18).zIndex(10);
                }
                sumDistance += route.distance.value;
                sumDuration += route.duration.value;

                executeDurDis();
            }

            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylinePaths.add(polyline);
            polyline.setClickable(true);
            durations.put(polyline.getId(), route.duration.text);
            distances.put(polyline.getId(), route.distance.text);

            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    // make color
                    String id = polyline.getId();
                    Log.d("hahahah", sumPaths.size() + ", " + sumRoutes.size());
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

                            } else if (!id.equals(polylinePaths.get(i).getId()) && containedOne.equals(polylinePaths)) {
                                polylinePaths.get(i).setColor(Color.GRAY);
                                polylinePaths.get(i).setZIndex(9);
                                polylinePaths.get(i).setWidth(14);
                            }
                            sumDuration = 0;
                            sumDistance = 0;

                        }
                    }
                    for (int u = 0; u < sumPaths.size(); u++) {
                        List<Polyline> polylineNew = sumPaths.get(u);
                        List<Route> routeNew = sumRoutes.get(u);
                        for (int i = 0; i < polylineNew.size(); i++) {
                            Polyline polyline1 = polylineNew.get(i);
                            Route route1 = routeNew.get(i);
                            if (polyline1.getColor() == Color.BLUE) {
                                sumDistance += route1.distance.value;
                                sumDuration += route1.duration.value;

                                executeDurDis();
                            }
                        }
                    }
                }
            });
        }

        sumRoutes.add(routes);
        sumPaths.add(polylinePaths);

    }

    public void executeDurDis() {
        int days = sumDuration / 86400;
        int hours = (sumDuration % 86400) / 3600;
        int minutes = ((sumDuration % 86400) % 3600) / 60;
        durDisp = days + " ngày " + hours + " giờ " + minutes + " phút";
        if (sumDistance > 1000) {
            disDisp = NumberFormat.getNumberInstance(Locale.US).format((double) sumDistance / 1000) + " km";

        } else {
            disDisp = NumberFormat.getNumberInstance(Locale.US).format(sumDistance) + " m";
        }
        ((TextView) findViewById(R.id.tv_map_duration)).setText(durDisp);
        ((TextView) findViewById(R.id.tv_map_distance)).setText(disDisp);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // First check if markers number is lower than 10
        Log.d("markersize", tenMarkers.size() + "");
        if (tenMarkers.size() < 10) {
            // Marker was not set yet. Add marker:
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Địa điểm không xác định")
                    .snippet("Có thể không tìm được đường ở vị trí này..."));
            tenMarkers.add(myMarker);
            listRest.add(myMarker.getPosition());
            if (!groupID.toString().equals("NA")) {
                Log.d("groupid", "1" + groupID + "1");
                DatabaseReference newRef = group.child(ciCode.toUpperCase());
                newRef.child("restPoints").setValue(listRest);
            }

        } else {
            // Number of markers is 10, just update the last one's position
            Toast.makeText(this, "Số điểm dừng tối đa là 10", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<LatLng> point = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonPoints = jsonData.getJSONArray("snappedPoints");
        JSONObject jsonLocation = jsonPoints.getJSONObject(0);
        for (int i = 0; i < jsonLocation.length(); i++) {
            String jsonLat = jsonPoints.getJSONObject(0).toString();
            String jsonLong = jsonPoints.getJSONObject(1).toString();
            LatLng latLng = new LatLng(Double.valueOf(jsonLat), Double.valueOf(jsonLong));

            myMarker.setPosition(latLng);
        }

    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Handle Nearby list
        for (int i = 0; i < listNearby.size(); i++) {
            if (marker.getPosition().latitude == listNearby.get(i).getPosition().latitude) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle(marker.getTitle() + ". Thêm điểm dừng tại đây?")
                        .setPositiveButton("Xác nhận",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // do something...
                                        if (tenMarkers.size() < 10) {
                                            // Marker was not set yet. Add marker:
                                            myMarker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()));
                                            tenMarkers.add(myMarker);
                                            listRest.add(myMarker.getPosition());
                                            if (!groupID.toString().equals("NA")) {
                                                DatabaseReference newRef = group.child(ciCode.toUpperCase());
                                                newRef.child("restPoints").setValue(listRest);
                                            }

                                        } else {
                                            // Number of markers is 10, just update the last one's position
                                            Toast.makeText(MapsActivity.this, "Số điểm dừng tối đa là 10", Toast.LENGTH_SHORT).show();
                                        }
                                        for (int j = 0; j < listNearby.size(); j++) {
                                            myMarker = listNearby.get(j);
                                            myMarker.remove();
                                        }
                                        listNearby.clear();
                                        return;
                                    }
                                }
                        )
                        .setNegativeButton("HỦY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        return;
                                    }
                                }
                        )
                        .create().show();
                return true;
            }
        }

        // Handle Rest markers
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        String addresses;
        try {
            if (geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).size() != 0) {
                addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0).getAddressLine(0);
                new AlertDialog.Builder(this)
                        .setTitle(addresses)
                        .setPositiveButton("Xóa điểm dừng?",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // do something...
                                        new AlertDialog.Builder(MapsActivity.this)
                                                .setTitle("Bạn có chắc chắn muốn xóa?")
                                                .setPositiveButton("Xác nhận",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                // do something...
                                                                marker.remove();
                                                                for (int i = 0; i < tenMarkers.size(); i++) {
                                                                    if (marker.getPosition().latitude == tenMarkers.get(i).getPosition().latitude && marker.getPosition().longitude == tenMarkers.get(i).getPosition().longitude) {
                                                                        tenMarkers.remove(i);
                                                                    }
                                                                }
                                                                for (int j = 0; j < listRest.size(); j++) {
                                                                    if (marker.getPosition().latitude == listRest.get(j).latitude && marker.getPosition().longitude == listRest.get(j).longitude) {
                                                                        listRest.remove(j);
                                                                        if (!groupID.toString().equals("NA")) {
                                                                            DatabaseReference newRef = group.child(ciCode.toUpperCase());
                                                                            newRef.child("restPoints").setValue(listRest);
                                                                        }

                                                                    }
                                                                }
                                                                Log.d("markersize", tenMarkers.size() + "");

                                                            }
                                                        }
                                                )
                                                .setNegativeButton("HỦY",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                )
                                                .create().show();
                                    }
                                }
                        )
                        .setNegativeButton("HỦY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .create().show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(marker.getTitle().toString())
                        .setPositiveButton("Xóa vị trí này?",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // do something...
                                        new AlertDialog.Builder(MapsActivity.this)
                                                .setTitle("Bạn có chắc chắn muốn xóa?")
                                                .setPositiveButton("Xác nhận",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                // do something...
                                                                marker.remove();
                                                                for (int i = 0; i < tenMarkers.size(); i++) {
                                                                    if (marker.getPosition().latitude == tenMarkers.get(i).getPosition().latitude && marker.getPosition().longitude == tenMarkers.get(i).getPosition().longitude) {
                                                                        tenMarkers.remove(i);
                                                                    }
                                                                }
                                                                for (int j = 0; j < listRest.size(); j++) {
                                                                    if (marker.getPosition().latitude == listRest.get(j).latitude && marker.getPosition().longitude == listRest.get(j).longitude) {
                                                                        listRest.remove(j);
                                                                        if (!groupID.toString().equals("NA")) {
                                                                            DatabaseReference newRef = group.child(ciCode.toUpperCase());
                                                                            newRef.child("restPoints").setValue(listRest);
                                                                        }

                                                                    }
                                                                }
                                                                Log.d("markersize", tenMarkers.size() + "");

                                                            }
                                                        }
                                                )
                                                .setNegativeButton("HỦY",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                )
                                                .create().show();
                                    }
                                }
                        )
                        .setNegativeButton("HỦY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .create().show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... objects) {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];

            DownloadURL downloadURL = new DownloadURL();
            try {
                googlePlacesData = downloadURL.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s) {

            List<HashMap<String, String>> nearbyPlaceList;
            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);
            Log.d("nearbyplacesdata", "called parse method");
            showNearbyPlaces(nearbyPlaceList);
            Log.d("nearby1", "" + listNearby.size());
        }

        private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
            for (int j = 0; j < listNearby.size(); j++) {
                myMarker = listNearby.get(j);
                myMarker.remove();
            }
            listNearby.clear();
            if (nearbyPlaceList.size() == 0) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Tìm kiếm không thành công")
                        .setMessage("Vui lòng thử lại sau...")
                        .setPositiveButton("Xác nhận", null)
                        .create().show();
                return;
            }
            for (int i = 0; i < nearbyPlaceList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlaceList.get(i);
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                listNearby.add(mMap.addMarker(markerOptions));
                Log.d("listNearby", listNearby.size() + "");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        String url;
        String station;
        DatabaseReference newRef = group.child(ciCode.toUpperCase());
        switch (v.getId()) {
            case R.id.btn_main2_saveTrack:
                listArc.clear();
                for (int u = 0; u < sumRoutes.size(); u++) {
                    for (int i = 0; i < sumRoutes.get(u).size(); i++) {
                        Route routeArc = (Route) sumRoutes.get(u).get(i);
                        Polyline polyArc = (Polyline) sumPaths.get(u).get(i);
                        if (polyArc.getColor() == Color.BLUE && polyArc != null) {
                            listArc.add(routeArc);

                            Date currentTime = Calendar.getInstance().getTime();
                            newRef.child("start_Date").setValue(currentTime.toString());
                            newRef.child("start_Lat").setValue(routeArc.startLocation.latitude);
                            newRef.child("start_Long").setValue(routeArc.startLocation.longitude);
                            newRef.child("end_Lat").setValue(routeArc.endLocation.latitude);
                            newRef.child("end_Long").setValue(routeArc.endLocation.longitude);
                            newRef.child("paths").setValue(listArc);
                            newRef.child("restPoints").setValue(listRest);
                            newRef.child("origin").setValue(mAutocompleteTextView.getText().toString());
                            newRef.child("destination").setValue(mAutocompleteTextView2.getText().toString());
                        }
                    }
                }
                Toast.makeText(this, "Đã lưu hành trình!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_main2_addRest:
                if (mAutocompleteTextView3.getVisibility() == View.GONE) {
                    mAutocompleteTextView3.setVisibility(View.VISIBLE);
                    btnNearbyGastation.setVisibility(View.VISIBLE);
                    btnNearbyHospital.setVisibility(View.VISIBLE);
                    btnNearbyRestaurant.setVisibility(View.VISIBLE);
                } else if (!mAutocompleteTextView3.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocationName(mAutocompleteTextView3.getText().toString(), 1);
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        myMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Vị trí không xác định..."));
                        tenMarkers.add(myMarker);
                        listRest.add(myMarker.getPosition());
                        if (!groupID.toString().equals("NA")) {
                            newRef.child("restPoints").setValue(listRest);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mAutocompleteTextView3.setText("");
                    mAutocompleteTextView3.setVisibility(View.GONE);
                    btnNearbyGastation.setVisibility(View.GONE);
                    btnNearbyHospital.setVisibility(View.GONE);
                    btnNearbyRestaurant.setVisibility(View.GONE);

                } else  if (tenMarkers.size() >= 10){
                    Toast.makeText(this, "Số điểm dừng tối đa là 10", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_main2_Reset:
                mMap.clear();
                mAutocompleteTextView.setText("");
                mAutocompleteTextView2.setText("");
                listNearby.clear();
                listArc.clear();
                listRest.clear();
                sumDistance = 0;
                sumDuration = 0;
                sumPaths.clear();
                sumRoutes.clear();
                tenMarkers.clear();
                ((TextView) findViewById(R.id.tv_map_duration)).setText("0 min");
                ((TextView) findViewById(R.id.tv_map_distance)).setText("0 km");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mapMethod.getMyLocation()));
                mAutocompleteTextView3.setText("");
                mAutocompleteTextView3.setVisibility(View.GONE);
                btnNearbyGastation.setVisibility(View.GONE);
                btnNearbyHospital.setVisibility(View.GONE);
                btnNearbyRestaurant.setVisibility(View.GONE);

                newRef.child("start_Date").removeValue();
                newRef.child("start_Lat").removeValue();
                newRef.child("start_Long").removeValue();
                newRef.child("end_Lat").removeValue();
                newRef.child("end_Long").removeValue();
                newRef.child("paths").removeValue();
                newRef.child("restPoints").removeValue();
                newRef.child("origin").removeValue();
                newRef.child("destination").removeValue();
                break;
            case R.id.btn_main2_nearbyGasStation:
                station = "gas_station";
                url = mapMethod.getUrl(mapMethod.getMyLocation().latitude, mapMethod.getMyLocation().longitude, station);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                mAutocompleteTextView3.setText("");
                mAutocompleteTextView3.setVisibility(View.GONE);
                btnNearbyGastation.setVisibility(View.GONE);
                btnNearbyHospital.setVisibility(View.GONE);
                btnNearbyRestaurant.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Tìm kiếm các Trạm Xăng xung quanh đây", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_main2_nearbyHospital:
                station = "hospital";
                url = mapMethod.getUrl(mapMethod.getMyLocation().latitude, mapMethod.getMyLocation().longitude, station);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                mAutocompleteTextView3.setText("");
                mAutocompleteTextView3.setVisibility(View.GONE);
                btnNearbyGastation.setVisibility(View.GONE);
                btnNearbyHospital.setVisibility(View.GONE);
                btnNearbyRestaurant.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Tìm kiếm các Bệnh Viện xung quanh đây", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_main2_nearbyRestaurant:
                station = "restaurant";
                url = mapMethod.getUrl(mapMethod.getMyLocation().latitude, mapMethod.getMyLocation().longitude, station);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                mAutocompleteTextView3.setText("");
                mAutocompleteTextView3.setVisibility(View.GONE);
                btnNearbyGastation.setVisibility(View.GONE);
                btnNearbyHospital.setVisibility(View.GONE);
                btnNearbyRestaurant.setVisibility(View.GONE);
                Log.d("listNearby", listNearby.size() + "");
                Toast.makeText(getApplicationContext(), "Tìm kiếm các Nhà Hàng xung quanh đây", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}
