package lofy.fpt.edu.vn.lofy_ver108.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class BitmapLoadTask extends AsyncTask<Void, Void, Bitmap> {

    //Link url hình ảnh bất kỳ
    private String url;
    private ImageView mIv ;
    private Context context;

    public BitmapLoadTask(Context context, String url, ImageView iv) {
        this.context = context;
        this.url = url;
        this.mIv = iv;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            // Tiến hành tạo đối tượng URL
            URL urlConnection = new URL(url);
            //Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            // Đọc dữ liệu
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
        mIv.setImageBitmap(result);
    }
}
