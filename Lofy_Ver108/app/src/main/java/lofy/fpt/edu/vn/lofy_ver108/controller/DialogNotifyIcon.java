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

//    public static final String IC_POLICE = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_police.png?alt=media&token=586d712e-505d-422c-af8e-7120675e3035";
//    public static final String IC_ACCIDENT = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_accident.png?alt=media&token=8a0f2605-7009-461b-b311-97045b19628e";
//    public static final String IC_CONSTRUCTION_AREA = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_construction_area.png?alt=media&token=e65a6843-ae7c-4c46-8c44-7248d528539a";
//    public static final String IC_GAS_STATION = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_gas_station.png?alt=media&token=0b8191b1-a2c9-4a7e-b006-2c4e1c81e1c0";
//    public static final String IC_HEALTHY = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_health.png?alt=media&token=ef7a163c-33d5-47f8-9eda-87e7862b7626";
//    public static final String IC_LOST = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_lost.png?alt=media&token=f0bbde80-2b1f-4f9f-bb38-6279d04ba017";
//    public static final String IC_MOUNTANT_ROAD = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_mountain_road.png?alt=media&token=faade8d8-e68a-43a6-b3d6-d6417f94cf9a";
//    public static final String IC_LAND_SCAPE = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_natural_landscape.png?alt=media&token=0ffaddca-b738-488e-b251-33ffe37b76c5";
//    public static final String IC_REPAIR = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_repair.png?alt=media&token=53bd2ed8-174c-4467-a563-cb0b4d3e6567";
//    public static final String IC_STOP = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_stop.png?alt=media&token=87d980c0-1697-4b08-8533-d5cf44ad063c";
//    public static final String IC_STOP_POINT = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_stop_point.png?alt=media&token=56d0ef24-4b86-462b-8182-67527e767559";
//    public static final String IC_SUM_UP = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_sumup.png?alt=media&token=ea319a1a-d14a-4118-b131-664f81ddb433";
//    public static final String IC_TRAFFIC_JAM = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_traffic_jam.png?alt=media&token=baf5563d-b353-4802-82fb-484eac1df693";
//    public static final String IC_TRAIN = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_train.png?alt=media&token=61ede068-24f6-45e5-bc8e-aee18ea8347b";
//    public static final String IC_WEATHER = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_weather.png?alt=media&token=92b6ee15-4b2d-4fd5-b119-3cfe2a92b421";
//    public static final String IC_ANIMAL = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_noti%2Fic_wild_animals.png?alt=media&token=7cd3ed54-e9b4-4267-9590-6a64cdbc4cbf";

    public static final String IC_POLICE = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_cong_an.png?alt=media&token=64660849-f49d-4893-8b93-5355e2c63c35";
    public static final String IC_ACCIDENT = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_tai_nan.png?alt=media&token=d038d2e3-90ae-4599-be1e-7a46faabe2f1";
    public static final String IC_CONSTRUCTION_AREA = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_cong_truong.png?alt=media&token=e06640b5-d907-4c15-978c-d647164e6079";
    public static final String IC_GAS_STATION = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_tram_xang.png?alt=media&token=5500d4b3-a65d-4bd4-a90c-06b1fe76e55b";
    public static final String IC_HEALTHY = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_suc_khoe.png?alt=media&token=57b1c84a-0f94-4d27-9529-bb644027e43a";
    public static final String IC_LOST = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_bi_lac.png?alt=media&token=a9912f79-c49c-4d26-9762-963937957515";
    public static final String IC_MOUNTANT_ROAD = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_duong_nui.png?alt=media&token=2de38a26-e1d8-42b8-b9c8-2d21fd130127";
    public static final String IC_LAND_SCAPE = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_canh_dep.png?alt=media&token=35f497b6-f56f-4294-932f-74d459c9600d";
    public static final String IC_REPAIR = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_sua_chua.png?alt=media&token=c31af0ac-2c0c-4d9b-bc90-3eeb5c9c1d68";
    public static final String IC_STOP = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_dung.png?alt=media&token=b669cbf7-4231-43b1-865b-64a22469ff21";
    public static final String IC_STOP_POINT = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_xin_dung.png?alt=media&token=bb847547-510f-4330-8dd0-e954797cef9b";
    public static final String IC_SUM_UP = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_tap_trung.png?alt=media&token=946178ff-6fb8-4c1d-8cf3-c35e385a9c93";
    public static final String IC_TRAFFIC_JAM = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_tac_duong.png?alt=media&token=035860a9-430b-4273-9312-b0acd0978696";
    public static final String IC_TRAIN = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_tau_hoa.png?alt=media&token=952e204d-7f4f-42bb-891e-0ca07cfeea8f";
    public static final String IC_WEATHER = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_thoi_tiet.png?alt=media&token=b030e0a1-5aa8-4ed2-8c2a-d5343d89fd71";
//    public static final String IC_ANIMAL = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_dong_vat_hoang_da.png?alt=media&token=94b2abe9-6e6b-411b-b0d6-24896b5dafc3";
    public static final String IC_ANIMAL = "https://firebasestorage.googleapis.com/v0/b/lofyversion106.appspot.com/o/ic_marker_noti%2Fic_dong_vat.png?alt=media&token=18615b6b-1fb1-4f1b-9f9d-c66fe90914c7";





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
        btnTrain = (Button) findViewById(R.id.btn_map_noti_trains);
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
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có cảnh sát", IC_POLICE, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_accident:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có tai nạn",IC_ACCIDENT, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_contruction_arena:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có công trường đang thi công",IC_CONSTRUCTION_AREA, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_gas_station:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có trạm xăng",IC_GAS_STATION, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_healthy:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có vấn đề về sức khoẻ",IC_HEALTHY, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_lost:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Bị lạc đường", IC_LOST, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_mountan_road:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Đường hiểm trở",IC_MOUNTANT_ROAD, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_land_scape:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có cảnh đẹp",IC_LAND_SCAPE, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_repair:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Cần sửa chữa", IC_REPAIR, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_stop:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Dừng khẩn cấp",IC_STOP, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_stop_point:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Xin dừng", IC_STOP_POINT, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_sum_up:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Tập trung",IC_SUM_UP, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_traffic_jam:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Tắc đường",IC_TRAFFIC_JAM, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_trains:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có tàu hoả",IC_TRAIN, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_weather:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Thời tiết xấu",IC_WEATHER, mLocation);
                DialogNotifyIcon.this.dismiss();
                window = dlMarker.getWindow();
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dlMarker.show();
                break;
            case R.id.btn_map_noti_animals:
                dlMarker = new DialogConfirmSetMarkerNoti(mContext, "Có động vật hoang dã", IC_ANIMAL, mLocation);
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
