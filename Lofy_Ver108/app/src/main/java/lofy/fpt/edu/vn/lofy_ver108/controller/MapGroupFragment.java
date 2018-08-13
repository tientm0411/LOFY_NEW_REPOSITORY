package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.ImageLoadTask;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import petrov.kristiyan.colorpicker.CustomDialog;

import android.location.LocationListener;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapGroupFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    private View rootView;
    private GoogleMap mMap;
    private double mLatitude;
    private double mLongtitude;
    private LocationManager locationManager;
    private Circle mCircle = null;
    private FloatingActionButton fabNoti;
    private DialogNotifyIcon dialogNotifyIcon;

    private SharedPreferences mSharedPreferences;
    private String groupID = "";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference notiRef;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private DatabaseReference groupUserRef;

    private ArrayList<Notification> alNoti; // list all noti
    private ArrayList<User> alUser; // list member
    private ArrayList<GroupUser> alGroupUser; // list group user
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private MarkerOptions mMarkerOption;
    private Marker mMarker;

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
        locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);
        Intent intent = new Intent(rootView.getContext(), GPS_Service.class);
        rootView.getContext().startService(intent);
        PowerManager mgr = (PowerManager) rootView.getContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

        return rootView;
    }

    private void showAlert(final int status) {
        String message, title, btnText;

        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(rootView.getContext());
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        getActivity().finish();
                    }
                });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted() {
        if (rootView.getContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || rootView.getContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
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

    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        setUpMap(mMap);
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
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                zoomToMyLocation(googleMap);
                return false;
            }
        });
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        zoomToMyLocation(googleMap);
        loadMarkerNoti(googleMap);
    }

    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    // load marker noti
    private void loadMarkerNoti(final GoogleMap googleMap) {
        alNoti = new ArrayList();
        notiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alNoti.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Notification nt = ds.getValue(Notification.class);
                        if (nt.getGroupID().equals(groupID)) {
                            alNoti.add(new Notification(nt.getNotiID(), nt.getNotiName(), nt.getGroupID(), nt.getUserID(), nt.getNoti_type(),
                                    nt.getNoti_time(), nt.getNoti_icon(), nt.getNoti_content(), nt.getLatitude(), nt.getLongtitude(), nt.getMess()));
                        }
                    }
                }

                if (!alNoti.isEmpty() && alNoti.size() > 0) {
                    LatLng lng;
                    for (int i = 0; i < alNoti.size(); i++) {
                        lng = new LatLng(alNoti.get(i).getLatitude(), alNoti.get(i).getLongtitude());
                        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_police);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        mMarkerOption = new MarkerOptions().position(lng)
                                .title(alNoti.get(i).getNotiName())
                                .snippet(alNoti.get(i).getMess())
                                .icon(markerIcon)
                                .draggable(true)
                        ;
                        mMarker = googleMap.addMarker(mMarkerOption);
                        String ur ="https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_police.png?alt=media&token=5d9bf761-1655-421b-8556-312f8aae8314";
                        ImageLoadTask imgTask=new ImageLoadTask(rootView.getContext(),ur,googleMap,mMarker);
                        imgTask.execute();
                        mHashMap.put(mMarker, i);
                        mMarker.setTag(0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    // set icon for marker
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // load location member
    private void loadLocationMember(final GoogleMap googleMap) {
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
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                LatLng lng;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User u = ds.getValue(User.class);
                                    for (int i = 0; i < alGroupUser.size(); i++) {
                                        if (alGroupUser.get(i).getUserId().equals(u.getUserId())) {
                                            lng = new LatLng(u.getUserLati(), u.getUserLongti());
                                            Marker melbourne = mMap.addMarker(new MarkerOptions()
                                                    .position(lng)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(300)));
                                        }
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

    // show noti
    private void showDialogNoti() {
        //  Toast.makeText(this, "Clicked !", Toast.LENGTH_SHORT).show();
        new DialogNotifyIcon(rootView.getContext(), getMyLocation()).show();

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
       // if(marker.equals(mMarker)){
            Integer clickCount = (Integer) marker.getTag();
            // Check if a click count was set, then display the click count.
            if (clickCount != null) {
                clickCount = clickCount + 1;
                marker.setTag(clickCount);
            Toast.makeText(rootView.getContext(),
                        marker.getTitle() +
                                " has been clicked " + clickCount + " times.",
                        Toast.LENGTH_SHORT).show();
                if(clickCount%2==1){
                    marker.showInfoWindow();
                }else if(clickCount%2==0){
                    marker.hideInfoWindow();
                }
            }
     //   }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
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
}
