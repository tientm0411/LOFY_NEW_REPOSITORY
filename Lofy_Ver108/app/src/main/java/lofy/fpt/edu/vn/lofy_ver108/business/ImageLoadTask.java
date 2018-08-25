package lofy.fpt.edu.vn.lofy_ver108.business;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;

import lofy.fpt.edu.vn.lofy_ver108.adapter.InforNotiMaker;
import lofy.fpt.edu.vn.lofy_ver108.controller.NotificationDisplayService;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    //Link url hình ảnh bất kỳ
    private GoogleMap map;
    private Context context;
    private Notification mNoti;
    private Marker mMaker = null;


    public ImageLoadTask(Context context, GoogleMap map, Notification notification) {
        this.context = context;
        this.map = map;
        this.mNoti = notification;
    }

    public ImageLoadTask() {

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(mNoti.getNoti_icon());
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
        map.setInfoWindowAdapter(new InforNotiMaker(context, result, mNoti.getUserID()));
        //new BitmapLoadTask(context,mNoti.getNoti_icon()).execute();
        MarkerOptions mMarkerOptions = new MarkerOptions()
                .title(mNoti.getNotiName())
                .snippet(mNoti.getMess())
                .position(new LatLng(mNoti.getLatitude(), mNoti.getLongtitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(result));
        mMaker = map.addMarker(mMarkerOptions);

        //  mMaker.setTag(0);
    }

    // return marker
    public Marker retriveMarkerNoti() {
        return mMaker;
    }

}
