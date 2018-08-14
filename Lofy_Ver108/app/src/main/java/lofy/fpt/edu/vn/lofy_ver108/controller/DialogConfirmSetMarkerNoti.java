package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.AppFunctions;
import lofy.fpt.edu.vn.lofy_ver108.business.BitmapLoadTask;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

public class DialogConfirmSetMarkerNoti extends Dialog implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private Context mContext;
    private String mUrlIcon;
    private String mNotiName;
    private Location mLocation;


    private SharedPreferences mSharedPreferences;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference notiRef;

    private String userID = "";
    private String userName = "";
    private String userPhone = "";
    private String userFbID = "";
    private String userAvatar = "";
    private String groupID = "";
    private String groupName = "";

    AppFunctions appFunctions;

    private Button btnCancel;
    private Button btnSend;
    private ImageView ivIcon;
    private TextView tvNameNoti;
    private EditText edtMess;


    public DialogConfirmSetMarkerNoti(@NonNull Context context,String notiName,String urlIcon, Location location) {
        super(context);
        mContext = context;
        mNotiName= notiName;
        mUrlIcon=urlIcon;
        mLocation = location;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_set_marker_noti);
        ivIcon = (ImageView) findViewById(R.id.iv_dg_noti_confirm_icon);
        tvNameNoti = (TextView) findViewById(R.id.tv_dg_noti_confirm_noti_name);
        edtMess = (EditText) findViewById(R.id.edt_dg_noti_confirm_mess);
        btnCancel = (Button) findViewById(R.id.btn_dg_noti_confirm_cancel);
        btnSend = (Button) findViewById(R.id.btn_dg_noti_confirm_send);
        btnSend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mSharedPreferences = mContext.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_MULTI_PROCESS);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        userName = mSharedPreferences.getString(IntroApplicationActivity.USER_NAME, "NA");
        userPhone = mSharedPreferences.getString(IntroApplicationActivity.USER_PHONE, "NA");
        userAvatar = mSharedPreferences.getString(IntroApplicationActivity.USER_URL_AVATAR, "NA");
        groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");

        appFunctions = new AppFunctions();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        notiRef = mFirebaseDatabase.getReference("notifications");

        BitmapLoadTask bitmapLoadTask = new BitmapLoadTask(mContext,mUrlIcon,ivIcon);
        bitmapLoadTask.execute();
        tvNameNoti.setText(mNotiName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dg_noti_confirm_cancel:
                dismiss();
                break;
            case R.id.btn_dg_noti_confirm_send:
                sendNoti();
                dismiss();
                break;
            default:
                break;
        }
    }

    private void sendNoti() {
        if(!groupID.equals("NA")){
                    String notiID = userID + appFunctions.randomString(6);
                    Date currentTime = Calendar.getInstance().getTime();
                    Notification notification = new Notification(notiID,mNotiName,groupID,userID,"icon",currentTime.toString(),
                           mUrlIcon,mNotiName, mLocation.getLatitude(),mLocation.getLongitude(),edtMess.getText().toString());
                    notiRef.child(notiID).setValue(notification);
                }else{
            Toast.makeText(mContext, "Bạn cần tham gia nhóm trước !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
