package lofy.fpt.edu.vn.lofy_ver108.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lofy.fpt.edu.vn.lofy_ver108.adapter.InforNotiMaker;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;

public class LoadMarkerMemberAsyntask extends AsyncTask<Void, Void, Bitmap>  {


    //Link url hình ảnh bất kỳ
    private GoogleMap map;
    private Context context;
    private Marker mMaker=null;
    private User user;


    public LoadMarkerMemberAsyntask(Context context, GoogleMap map, User user) {
        this.context = context;
        this.map = map;
         this.user = user;
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
        //new BitmapLoadTask(context,mNoti.getNoti_icon()).execute();
        MarkerOptions mMarkerOptions = new MarkerOptions()
                .title(user.getUserName())
                .position(new LatLng(user.getUserLati(), user.getUserLongti()))
                .icon(BitmapDescriptorFactory.fromBitmap(result));
      map.addMarker(mMarkerOptions);
        //  mMaker.setTag(0);
    }

    // return marker
//    public Marker retriveMarkerNoti() {
//        return mMaker;
//    }

}
