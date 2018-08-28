package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinder;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinderListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMain2);
        mapFragment.getMapAsync(this);
        Bundle bundle = getIntent().getExtras();
        ciCode = bundle.getString("groupId");
        Log.d("cCode:", ciCode);
        initView();
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
        mapMethod = new MapMethod(this);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
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
//                    Toast.makeText(MapsActivity.this, "" + tenMarkers.size(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MapsActivity.this, "" + listRest.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendRequest() {

        String destination = mAutocompleteTextView2.getText().toString();

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
                new DirectionFinder(this, mAutocompleteTextView.getText().toString(), "", null, mAutocompleteTextView2.getText().toString()).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else
            try {
                new DirectionFinder(this, mAutocompleteTextView.getText().toString(), myMarker.getPosition().latitude + "," + myMarker.getPosition().longitude, tenMarkers, mAutocompleteTextView2.getText().toString()).execute();
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

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

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
        this.routes = routes;
        progressDialog.dismiss();
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
        if (tenMarkers.size() < 10) {
            // Marker was not set yet. Add marker:
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Thêm điểm dừng?")
                    .snippet("Có phải bạn muốn thêm điểm dừng?"));
            tenMarkers.add(myMarker);
            listRest.add(myMarker.getPosition());
            DatabaseReference newRef = group.child(ciCode.toUpperCase());
            newRef.child("restPoints").setValue(listRest);
        } else {
            // Number of markers is 10, just update the last one's position
            myMarker.setPosition(latLng);
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
                                                                        DatabaseReference newRef = group.child(ciCode.toUpperCase());
                                                                        newRef.child("restPoints").setValue(listRest);
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
                for (int i = 0; i < tenMarkers.size(); i++) {
                    if (marker.getId().equals(tenMarkers.get(i).getId())) {
                        marker.remove();
                        myMarker.remove();
                        for (int j = 0; j < tenMarkers.size(); j++) {
                            if (marker.getPosition().latitude == tenMarkers.get(j).getPosition().latitude && marker.getPosition().longitude == tenMarkers.get(j).getPosition().longitude) {
                                tenMarkers.remove(j);
                            }
                        }
                        for (int j = 0; j < listRest.size(); j++) {
                            if (marker.getPosition().latitude == listRest.get(j).latitude && marker.getPosition().longitude == listRest.get(j).longitude) {
                                listRest.remove(j);
                                DatabaseReference newRef = group.child(ciCode.toUpperCase());
                                newRef.child("restPoints").setValue(listRest);
                            }
                        }

                        Log.d("markersize", tenMarkers.size() + "");
                    }
                }
                return false;
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

    @Override
    public void onClick(View v) {
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
                        }
                    }
                }
                Toast.makeText(this, "Đã lưu hành trình!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_main2_addRest:
                if (mAutocompleteTextView3.getVisibility() == View.GONE) {
                    mAutocompleteTextView3.setVisibility(View.VISIBLE);
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
                                .title("Rest"));
                        tenMarkers.add(myMarker);
                        listRest.add(myMarker.getPosition());
                        newRef.child("restPoints").setValue(listRest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mAutocompleteTextView3.setText("");
                    mAutocompleteTextView3.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_main2_Reset:
                mMap.clear();
                break;
            default:
                break;
        }

    }
}
