package lofy.fpt.edu.vn.lofy_ver108.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class InforNotiMaker implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private Bitmap btmp;
    private LayoutInflater inflater;

    public InforNotiMaker(Context context,Bitmap result)
    {
        this.mContext=context;
        this.btmp=result;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
       View v= inflater.inflate(R.layout.dialog_marker_nioti_infor,null);
        // Getting the position from the marker
        LatLng latLng = marker.getPosition();

        ImageView ivNoti = (ImageView) v.findViewById(R.id.iv_dg_noti_infor_icon);
        TextView tvName = (TextView) v.findViewById(R.id.tv_dg_noti_infor_noti_name);
        TextView tvMess = (TextView) v.findViewById(R.id.tv_dg_noti_infor_noti_mess);

        tvName.setText(marker.getTitle());
        tvMess.setText(marker.getSnippet());
        ivNoti.setImageBitmap(btmp);
        return v;
    }
}
