package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
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

import javax.xml.datatype.Duration;

import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinder;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinderListener;
import lofy.fpt.edu.vn.lofy_ver108.Modules.Distance;
import lofy.fpt.edu.vn.lofy_ver108.Modules.Route;
import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.PlaceArrayAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import petrov.kristiyan.colorpicker.ColorPicker;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private MapMethod mapMethod;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private AutoCompleteTextView mAutocompleteTextView2;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button btnFindPath;
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
        tvDistance = (TextView) findViewById(R.id.tv_map_distance);
        tvDuration = (TextView) findViewById(R.id.tv_map_duration);
        mAutocompleteTextView2.setThreshold(3);
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        //groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        btnSaveTrack = (Button) findViewById(R.id.btn_main2_saveTrack);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        group = mFirebaseDatabase.getReference("groups");
        btnSaveTrack.setOnClickListener(this);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteTextView2.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        mAutocompleteTextView2.setAdapter(mPlaceArrayAdapter);
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
        if (myMarker == null) {
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
//            for (Polyline polyline : polylinePaths) {
//                polyline.remove();
//            }
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
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();
//        for (int s = 0; s < sumRoutes.size(); s++) {

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
                sumDistance += route.distance.value;
                sumDuration += route.duration.value;

                executeDurDis();
            }

            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylinePaths.add(polyline);
            polyline.setClickable(true);
            durations.put(polyline.getId(), route.duration.text);
            distances.put(polyline.getId(), route.distance.text);


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
//                                if (durations.containsKey(id) && distances.containsKey(id)) {
//                                    ((TextView) findViewById(R.id.tv_map_duration)).setText(durations.get(id));
//                                    ((TextView) findViewById(R.id.tv_map_distance)).setText(distances.get(id));
//                                }
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
//        }
//        try {
//            double lat = myMarker.getPosition().latitude;
//            double lng = myMarker.getPosition().longitude;
//            mAutocompleteTextView.setText(lat + "," + lng);
//        } catch (Exception e) {
//
//        }
        //blueArc();
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
            new DownloadRawData().execute("https://roads.googleapis.com/v1/snapToRoads?path=" + myMarker.getPosition().latitude + "," + myMarker.getPosition().longitude + "&interpolate=true&key=AIzaSyBEIiySPSYD4Y0E0mGqlpYvErj99oj77fE");
//            "https://roads.googleapis.com/v1/snapToRoads?path=-35.27801,149.12958|-35.28032,149.12907|-35.28099,149.12929|-35.28144,149.12984|-35.28194,149.13003|-35.28282,149.12956|-35.28302,149.12881|-35.28473,149.12836&interpolate=true&key=AIzaSyBEIiySPSYD4Y0E0mGqlpYvErj99oj77fE"
//            try {
//                URL url = new URL(            "https://roads.googleapis.com/v1/snapToRoads?path=" +myMarker.getPosition().latitude +  ", " + myMarker.getPosition().longitude + "&interpolate=true&key=AIzaSyBEIiySPSYD4Y0E0mGqlpYvErj99oj77fE");
//                InputStream is = url.openConnection().getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//
//                Log.d("bufferk", buffer.toString());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            tenMarkers.add(myMarker);
            listRest.add(myMarker.getPosition());
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

        Toast.makeText(this, "hay vai", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng mLocation = marker.getPosition();
//        Toast.makeText(this, marker.getId().toString() + "", Toast.LENGTH_SHORT).show();
        try {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            addresses = geocoder.getFromLocation(mLocation.latitude, mLocation.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String province = addresses.get(0).getAdminArea();
//                marker.setSnippet(address.getThoroughfare() + ", " + province);
            }
            String address = addresses.get(0).getAddressLine(0);
            //get current province/City
//            mAutocompleteTextView2.setText(address);
            double lat = myMarker.getPosition().latitude;
            double lng = myMarker.getPosition().longitude;
            mAutocompleteTextView2.setText(lat + "," + lng);
            btnFindPath.setEnabled(true);
            marker.setTitle(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        for (int i = 0; i < polylinePaths.size(); i++) {
////            polylinePaths.get(i).setClickable(false);
//        }
//        LatLng mLocation = marker.getPosition();
//        String ori = mAutocompleteTextView.getText().toString();
//        String des = mAutocompleteTextView2.getText().toString();
//        String rest = mLocation.latitude + "," + mLocation.longitude;
//
//        try {
//            new DirectionFinder(this, ori, rest, tenMarkers, des).execute();
//            mAutocompleteTextView.setText(rest);
//            mAutocompleteTextView2.setText(des);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        marker.hideInfoWindow();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        for (int i = 0; i < tenMarkers.size(); i++) {
            if (marker.getId().equals(tenMarkers.get(i).getId())) {
                marker.remove();
                tenMarkers.remove(i);
                Log.d("markersize", tenMarkers.size() + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        DatabaseReference newRef = group.child(ciCode.toUpperCase());

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
    }
}
