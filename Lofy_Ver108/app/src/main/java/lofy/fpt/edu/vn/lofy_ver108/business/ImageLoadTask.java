package lofy.fpt.edu.vn.lofy_ver108.business;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import lofy.fpt.edu.vn.lofy_ver108.adapter.InforNotiMaker;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    //Link url hình ảnh bất kỳ
    private String url;
    private GoogleMap map;
    private Context context;
    private boolean isCompleted = false;
    private Marker currentMarker;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public ImageLoadTask(Context context, String url, GoogleMap map, Marker currentMarker) {
        this.context = context;
        this.url = url;
        this.map = map;
        this.currentMarker = currentMarker;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
//Tiến hành tạo đối tượng URL
            URL urlConnection = new URL(url);
//Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
//Đọc dữ liệu
            InputStream input = connection.getInputStream();
//Tiến hành convert qua hình ảnh
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
//thiết lập Info cho Map khi tải hình hoàn tất
        map.setInfoWindowAdapter(new InforNotiMaker(context, result));
//tiến hành hiển thị lên Custom marker option lên Map:
     //   currentMarker.showInfoWindow();
    }
}
