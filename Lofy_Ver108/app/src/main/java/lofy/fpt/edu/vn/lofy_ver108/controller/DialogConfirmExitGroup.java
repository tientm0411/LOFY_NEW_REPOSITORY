package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class DialogConfirmExitGroup extends Dialog implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Button btnGo;
    private Button btnQuit;
    private String mkeyGroupUser;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String userID = "";
    private String isHost = "";
    private String currentGroupId = "";
    private String currentGroupUserId = "";

    public DialogConfirmExitGroup(@NonNull Context context, String keyGroupuser) {
        super(context);
        mContext = context;
        mkeyGroupUser = keyGroupuser;
        initView();
    }



    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_confirm_exit_group);
        btnGo = (Button) findViewById(R.id.btn_dialog_confirm_exit_group_go_current);
        btnQuit = (Button) findViewById(R.id.btn_dialog_confirm_exit_group_quit_group);
        btnGo.setOnClickListener(this);
        btnQuit.setOnClickListener(this);

        mSharedPreferences = mContext.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        isHost = mSharedPreferences.getString(IntroApplicationActivity.IS_HOST, "NA");

    }

    private void quitGroup() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups-users");
        groupRef.child(mkeyGroupUser).child("statusUser").setValue(false);

        editor.putString(IntroApplicationActivity.GROUP_ID, "NA");
        editor.putString(IntroApplicationActivity.GROUP_NAME, "NA");
        editor.putString(IntroApplicationActivity.GROUP_USER_ID, "NA");
        editor.putString(IntroApplicationActivity.IS_HOST, "NA");
        editor.apply();
        Toast.makeText(mContext, "Đã rời khỏi nhóm !", Toast.LENGTH_SHORT).show();
    }

    // create a dialog
    public void createAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Thoát ");
        alertDialog.setMessage("Bạn có chắc muốn thoát nhóm hiện tại");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quitGroup();
                dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
                return;
            }
        });
        alertDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_confirm_exit_group_go_current:
                Toast.makeText(mContext, "ĐI", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_dialog_confirm_exit_group_quit_group:
                createAlertDialog();
                break;
            default:
                break;

        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
