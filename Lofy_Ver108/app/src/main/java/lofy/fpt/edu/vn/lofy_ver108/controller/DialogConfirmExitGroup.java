package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;

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
        try {
            Log.d("mkeyGroupUser", mkeyGroupUser + ": ");
            String substrGId = mkeyGroupUser.substring(0, 6);
            String substrUId = mkeyGroupUser.substring(6, 21);
            GroupUser gu1 = new GroupUser(mkeyGroupUser, substrUId, substrGId, false, false, "NA", 0.0, false);
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups-users");
            groupRef.child(mkeyGroupUser).setValue(gu1);

            editor.putString(IntroApplicationActivity.GROUP_ID, "NA");
            editor.putString(IntroApplicationActivity.GROUP_NAME, "NA");
            editor.putString(IntroApplicationActivity.GROUP_USER_ID, "NA");
            editor.putString(IntroApplicationActivity.IS_HOST, "NA");
            editor.apply();
            Toast.makeText(mContext, "Đã rời khỏi nhóm !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
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

    private CreateGroupFragment createGroupFragment;
    private JoinGroupFragment joinGroupFragment;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_confirm_exit_group_go_current:
                String groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
//                String uID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
//                String isHost = mSharedPreferences.getString(IntroApplicationActivity.IS_HOST, "NA");
//                if (isHost.equals("true")) {
//                    if (createGroupFragment == null) {
//                        createGroupFragment = new CreateGroupFragment("home");
//                    }
//                    ((AppCompatActivity)getOwnerActivity()).getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.ln_main, createGroupFragment, CreateGroupFragment.class.getName())
//                            .addToBackStack(null)
//                            .commit();
//                } else {
//                    if (joinGroupFragment == null) {
//                        joinGroupFragment = new JoinGroupFragment("home");
//                    }
//                    ((AppCompatActivity)getOwnerActivity()).getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.ln_main, joinGroupFragment, JoinGroupFragment.class.getName())
//                            .addToBackStack(null)
//                            .commit();
//                }
//                Toast.makeText(mContext, "ĐI", Toast.LENGTH_SHORT).show();

                try {
                    Activity activity = getOwnerActivity();
                    if (activity != null) {
                        Intent intent;
                        intent = new Intent(activity, StartActivity.class);
                        intent.putExtra("groupId", groupID);
                        activity.startActivity(intent);
                    } else {
                        Toast.makeText(activity, "null", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Không tải được nhóm hiện tại !", Toast.LENGTH_SHORT).show();
                }
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
