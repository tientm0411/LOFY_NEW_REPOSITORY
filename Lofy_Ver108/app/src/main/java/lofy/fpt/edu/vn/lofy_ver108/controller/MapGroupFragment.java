package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinder;
import lofy.fpt.edu.vn.lofy_ver108.Modules.DirectionFinderListener;
import lofy.fpt.edu.vn.lofy_ver108.dbo.QueryFirebase;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.Route;
import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.AppFunctions;
import lofy.fpt.edu.vn.lofy_ver108.business.ImageLoadTask;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

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
    private Marker mMarkerMem;
    private FloatingActionButton fabNoti;

    private SharedPreferences mSharedPreferences;
    private String groupID = "";
    private List<Marker> tenMarkers = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private String userID = "";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference notiRef;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private DatabaseReference groupUserRef;
    private MapMethod mapMethod;
    private AppFunctions appFunctions;
    private QueryFirebase queryFirebase = new QueryFirebase();

    private PermissionManager permissionManager;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";


    private ArrayList<Marker> alMarkerNoti; // list marker noti
    private ArrayList<Marker> alMarkerMember; // list marker member

    private ArrayList<GroupUser> alGroupUser; // list group user
    private ArrayList<User> alUser; // list user
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<List> sumPaths = new ArrayList<>();


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
        try {
            askGrantLocationPermission(); // ask grant location permisstion
            initGoogleAPIClient();//Init Google API Client
            checkPermissions();//Check Permission
        } catch (Exception e) {

        }

        try {
            Intent intent = new Intent(rootView.getContext(), GPS_Service.class);
            rootView.getContext().startService(intent);
            PowerManager mgr = (PowerManager) rootView.getContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
            wakeLock.acquire();

            setHasOptionsMenu(true);
            rootView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (i == KeyEvent.KEYCODE_BACK) {
                            callParentMethod();
                            return true;
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {

        }

        return rootView;
    }


    public void callParentMethod() {
        getActivity().onBackPressed();
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
        appFunctions = new AppFunctions();
        alMarkerNoti = new ArrayList<>();

        alMarkerMember = new ArrayList<>();
        alGroupUser = new ArrayList<>();
        alUser = new ArrayList<>();

//        mTopToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(mTopToolbar);
//

//       queryFirebase = new QueryFirebase();
    }

    private Toolbar mTopToolbar;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_menu_set_radius:
//
                try {
                    Toast.makeText(rootView.getContext(), "Đặt bán kính !", Toast.LENGTH_SHORT).show();
                    DialogSetRadius dialogSetRadius = new DialogSetRadius(rootView.getContext());
                    dialogSetRadius.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialogSetRadius.show();
                } catch (Exception e) {

                }
            default:
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                Toast.makeText(rootView.getContext(), "Clicked !", Toast.LENGTH_SHORT).show();
//                zoomToMyLocation(googleMap);
//                return false;
//            }
//        });
            setUpMap(mMap);
            loadMarkerNoti(mMap);
            loadRoute(mMap);
            loadMarkerMember();
        } catch (Exception e) {

        }

//        int iadsa = queryFirebase.getAlUser().size();
//        Log.d("onMapReady_3", iadsa+" ");
    }

    private void loadRoute(final GoogleMap googleMap) {
        final DatabaseReference newRef = groupRef.child(groupID);
        final DirectionFinder directionFinder = new DirectionFinder(this, "", null, "");
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                    if (i == 0) {
                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_point))
                                .position(point)));
                    }
                    if (i == listLatLng.size() - 1) {
                        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_point))
                                .position(point)));
                    }
                }
                Polyline polyline = googleMap.addPolyline(polylineOptions);
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
                    Marker myMarker = googleMap.addMarker(new MarkerOptions().position(point).title("Rest here!!!"));
                    tenMarkers.add(myMarker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // set up map
    public void setUpMap(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setPadding(5, 80, 10, 10);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        zoomToMyLocation(googleMap);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerDragListener(this);
    }

    private ArrayList<Notification> alNoti; // list noti

    // load marker noti
    private void loadMarkerNoti(final GoogleMap googleMap) {
        final ArrayList<Notification> alCheckNoti = new ArrayList<>();
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
                    for (int i = 0; i < alNoti.size(); i++) {

                        ImageLoadTask imageLoadTask = new ImageLoadTask(rootView.getContext(), googleMap, alNoti.get(i));
                        imageLoadTask.execute();
                        alMarkerNoti.add(new ImageLoadTask().retriveMarkerNoti());
                    }

                }
                Log.d("alMarkerNoti", alMarkerNoti.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        notiRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notification nt = dataSnapshot.getValue(Notification.class);
                if (nt.getGroupID().equals(groupID)) {
                    Intent intent = new Intent(rootView.getContext(), NotificationDisplayService.class);
                    intent.putExtra("KEY_NOTI", nt);
                    rootView.getContext().startService(intent);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // load marker member
    ArrayList<User> alUserOutRange;
    private double mSizeRadius;

    private void loadMarkerMember() {
        String hostId = queryFirebase.getHostGroupUserByGroupId(queryFirebase.getAlGroupUser(), groupID);
        mSizeRadius = queryFirebase.getSizeByGroupUserId(queryFirebase.getAlGroupUser(), hostId);
        Log.d("loadMarkerMember", mSizeRadius + " ");
        if (mSizeRadius <= 0) {
            mSizeRadius = 1;
        }

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

                    alUserOutRange = new ArrayList<>();
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            alUser.clear();
                            alUserOutRange.clear();
                            LatLng lng = null;
                            if (!alMarkerMember.isEmpty() || alMarkerMember.size() > 0) {
                                for (Marker marker : alMarkerMember) {
                                    marker.remove();
                                }
                                alMarkerMember.clear();
                            }
                            if (dataSnapshot.hasChildren()) {
                                LatLng hostLng = null;
                                float mResult[] = null;
                                int mCount = 0;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User u = ds.getValue(User.class);
                                    for (int i = 0; i < alGroupUser.size(); i++) {
                                        if (u.getUserId().equals(alGroupUser.get(i).getUserId()) && alGroupUser.get(i).isStatusUser() == true) {
                                            alUser.add(u);
                                        }
                                    }
                                }

                                for (int i = 0; i < alUser.size(); i++) {
                                    if (alGroupUser.get(i).isHost() && alGroupUser.get(i).isStatusUser()) {
                                        hostLng = new LatLng(alUser.get(i).getUserLati(), alUser.get(i).getUserLongti());
                                        break;
                                    }
                                }
                                for (int k = 0; k < alUser.size(); k++) {
                                    lng = new LatLng(alUser.get(k).getUserLati(), alUser.get(k).getUserLongti());
                                    LoaddMarkerMemberAsyntask loaddMarkerMemberAsyntask = new LoaddMarkerMemberAsyntask(alUser.get(k), alGroupUser.get(k), lng);
                                    loaddMarkerMemberAsyntask.execute();
                                    mResult = new float[10];
                                    try {

                                        Location.distanceBetween(hostLng.latitude, hostLng.longitude, alUser.get(k).getUserLati(), alUser.get(k).getUserLongti(), mResult);
                                    } catch (Exception e) {
                                    }
                                    Log.d("distance", mResult[0] + " ");
                                    if (mResult[0] >= mSizeRadius * 1000) {
                                        mCount++;
//                                        alUserOutRange.add(alUser.get(k));
//                                        Intent intent = new Intent(rootView.getContext(), OutRangeService.class);
//                                        intent.putExtra("KEY_NOTI", alUser.get(k));
//                                        rootView.getContext().startService(intent);
                                        groupRef.child(alGroupUser.get(k).getGroupId()).child("members-out-range").child(alUser.get(k).getUserName().toString()).setValue(alUser.get(k).getUserName().toString());
                                    } else {
                                        groupRef.child(alGroupUser.get(k).getGroupId()).child("members-out-range").child(alUser.get(k).getUserName().toString()).removeValue();
                                    }
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

        groupRef.child(groupID).child("members-out-range").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String nt = dataSnapshot.getValue(String.class);
                Intent intent = new Intent(rootView.getContext(), OutRangeService.class);
                intent.putExtra("KEY_NOTI_USER_NAME", nt);
                rootView.getContext().startService(intent);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // load marker member asyn
    public class LoaddMarkerMemberAsyntask extends AsyncTask<Void, Void, Bitmap> {
        private User user;
        private GroupUser groupUser;
        private LatLng latLng;

        public LoaddMarkerMemberAsyntask(User user, GroupUser groupUser, LatLng latLng) {
            this.user = user;
            this.groupUser = groupUser;
            this.latLng = latLng;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
//                 "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/test_image%2Ftest_image.PNG?alt=media&token=cf2ddd69-9809-49b0-9697-abb507796378"
//                 "https://graph.facebook.com/692839717728567/picture?with=250&height=250"
                URL urlConnection = new URL(user.getUrlAvatar());
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                if (myBitmap == null)
                    return null;
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);
            if (!user.getUserId().equals(userID)) {
                MarkerOptions mMarkerOptions = new MarkerOptions()
                        .title(user.getUserName())
                        .position(new LatLng(user.getUserLati(), user.getUserLongti()))
                        .icon(BitmapDescriptorFactory.fromBitmap(appFunctions.getRoundedCornerBitmap(result, 68)))
                        .anchor(0.5f, 1);
                mMarkerMem = mMap.addMarker(mMarkerOptions);
                alMarkerMember.add(mMarkerMem);
            }
            if (groupUser.isHost() && groupUser.isStatusUser() == true) {
                String gId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
                String hostId = queryFirebase.getHostGroupUserByGroupId(queryFirebase.getAlGroupUser(), gId);
                double sizeRadius = queryFirebase.getSizeByGroupUserId(queryFirebase.getAlGroupUser(), hostId);

                Log.d("onPostExecute_4", gId + "");
                Log.d("onPostExecute_0", sizeRadius + "");
                Log.d("onPostExecute_1", hostId + "");
                Log.d("onPostExecute_2", queryFirebase.getAlGroupUser().size() + "");
                if (sizeRadius <= 0) {
                    sizeRadius = 1;
                }
                Log.d("onPostExecute_3", sizeRadius + "");
                mapCircle = new MapMethod(rootView.getContext()).showCircleToGoogleMap(mMap, mapCircle, latLng, (float) sizeRadius);
            }
        }
    }

    // show noti
    private void showDialogNoti() {
        //  Toast.makeText(this, "Clicked !", Toast.LENGTH_SHORT).show();
        MapMethod mapMethod = new MapMethod(rootView.getContext());
        Location myLocation = new Location("My_Location");
        myLocation.setLatitude((mapMethod.getMyLocation().latitude));
        myLocation.setLongitude((mapMethod.getMyLocation().longitude));
        new DialogNotifyIcon(rootView.getContext(), myLocation).show();

    }

    // zoom camera to my location
    private void zoomToMyLocation(GoogleMap googleMap) {
        Location location = getMyLocation();
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
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
        try {
            rootView.getContext().registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
            getView().setFocusable(true);
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
        } catch (Exception e) {

        }
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


            mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    // make color
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_noti:
                try {
                    showDialogNoti();
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

}
