package lofy.fpt.edu.vn.lofy_ver108.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lofy.fpt.edu.vn.lofy_ver108.adapter.InforNotiMaker;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;

public class LoadMarkerMemberAsyntask extends AsyncTask<Void, Void, Bitmap> {

    private GoogleMap map;
    private Context context;
    private Marker mMaker;
    private Circle circle;
    private User user;
    private GroupUser groupUser;
    private String userID;
    private LatLng latLng;


    public LoadMarkerMemberAsyntask(Context context, GoogleMap map, User user, GroupUser groupUser, String userID, LatLng latLng) {
        this.context = context;
        this.map = map;
        this.user = user;
        this.groupUser = groupUser;
        this.userID = userID;
        this.latLng = latLng;
    }


    public LoadMarkerMemberAsyntask() {

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL("https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_police.png?alt=media&token=586d712e-505d-422c-af8e-7120675e3035");
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
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (!user.getUserId().equals(userID)) {
            MarkerOptions mMarkerOptions = new MarkerOptions()
                    .title(user.getUserName())
                    .position(new LatLng(user.getUserLati(), user.getUserLongti()))
                    .icon(BitmapDescriptorFactory.fromBitmap(result));
            mMaker = map.addMarker(mMarkerOptions);
        }
        if (groupUser.isHost() == true && groupUser.isStatusUser() == true) {
            circle = new MapMethod(context).showCircleToGoogleMap(map, circle, latLng, 3);
            Log.d("onPostExecute", circle + "");
        }
    }

    public Marker getmMaker() {
        Log.d("getmMaker", mMaker + " ");
        return mMaker;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        Log.d("getmMaker", circle + " ");
        return circle;
    }
}
