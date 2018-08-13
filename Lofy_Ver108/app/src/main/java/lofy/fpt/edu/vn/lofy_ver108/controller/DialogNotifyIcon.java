package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.AppFunctions;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogNotifyIcon extends Dialog implements View.OnClickListener {

    private Button btnNotiPolice;
    private Button btnaccident;
    private Button btnConstruct;
    private Button btnGasStation;
    private Button btnHealthy;
    private Button btnLost;
    private Button btnMountantRoad;
    private Button btnLandScape;
    private Button btnRepair;
    private Button btnStop;
    private Button btnStopPoint;
    private Button btnSumUp;
    private Button btnTrafficJam;
    private Button btnTrain;
    private Button btnWather;
    private Button btnAnimals;


    private Context mContext;
    private Location mLocation;


    public DialogNotifyIcon(@NonNull Context context, Location mLocation) {
        super(context);
        mContext = context;
        this.mLocation = mLocation;
        initView();
    }


    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notify_icon);
        btnNotiPolice = (Button) findViewById(R.id.btn_map_noti_police);
        btnaccident = (Button) findViewById(R.id.btn_map_noti_accident);
        btnConstruct = (Button) findViewById(R.id.btn_map_noti_contruction_arena);
        btnGasStation = (Button) findViewById(R.id.btn_map_noti_gas_station);
        btnHealthy = (Button) findViewById(R.id.btn_map_noti_healthy);
        btnLost = (Button) findViewById(R.id.btn_map_noti_lost);
        btnMountantRoad = (Button) findViewById(R.id.btn_map_noti_mountan_road);
        btnLandScape = (Button) findViewById(R.id.btn_map_noti_land_scape);
        btnRepair = (Button) findViewById(R.id.btn_map_noti_repair);
        btnStop = (Button) findViewById(R.id.btn_map_noti_stop);
        btnStopPoint = (Button) findViewById(R.id.btn_map_noti_stop_point);
        btnSumUp = (Button) findViewById(R.id.btn_map_noti_sum_up);
        btnTrafficJam = (Button) findViewById(R.id.btn_map_noti_traffic_jam);
        btnTrain = (Button) findViewById(R.id.btn_map_noti_traffic_jam);
        btnWather = (Button) findViewById(R.id.btn_map_noti_weather);
        btnAnimals = (Button) findViewById(R.id.btn_map_noti_animals);

        btnNotiPolice.setOnClickListener(this);
        btnaccident.setOnClickListener(this);
        btnConstruct.setOnClickListener(this);
        btnGasStation.setOnClickListener(this);
        btnHealthy.setOnClickListener(this);
        btnLost.setOnClickListener(this);
        btnMountantRoad.setOnClickListener(this);
        btnLandScape.setOnClickListener(this);
        btnRepair.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStopPoint.setOnClickListener(this);
        btnSumUp.setOnClickListener(this);
        btnTrafficJam.setOnClickListener(this);
        btnTrain.setOnClickListener(this);
        btnWather.setOnClickListener(this);
        btnAnimals.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        DialogConfirmSetMarkerNoti dlMarker;
        Window window;
        switch (view.getId()) {
            case R.id.btn_map_noti_police:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có cảnh sát", R.drawable.ic_police, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_accident:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có tai nạn", R.drawable.ic_accident, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_contruction_arena:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có công trường đang thi công", R.drawable.ic_construction_area, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_gas_station:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có trạm xăng", R.drawable.ic_gas_station, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_healthy:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có vấn đề về sức khoẻ", R.drawable.ic_health, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_lost:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Bị lạc đường", R.drawable.ic_lost, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_mountan_road:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Đường hiểm trở", R.drawable.ic_mountain_road, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_land_scape:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có cảnh đẹp", R.drawable.ic_natural_landscape, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_repair:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Cần sửa chữa", R.drawable.ic_repair, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_stop:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Dừng khẩn cấp", R.drawable.ic_stop, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_stop_point:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Xin dừng", R.drawable.ic_stop_point, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_sum_up:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Tập trung", R.drawable.ic_sumup, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_traffic_jam:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Tắc đường", R.drawable.ic_traffic_jam, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_train:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có tàu hoả", R.drawable.ic_train, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_weather:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Thời tiết xấu", R.drawable.ic_weather, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_animals:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có động vật hoang dã", R.drawable.ic_wild_animals, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            default:
                break;
        }
    }

}
