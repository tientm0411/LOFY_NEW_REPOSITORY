package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;

public class DialogConfirmSetHost extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String mKeyUserRequest2;

    private Button btnSetVice;
    private Button btnCacel;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private SharedPreferences mSharedPreferences;
    private String currentGroupId = "";


    public DialogConfirmSetHost(@NonNull Context context, String keyRequest2) {
        super(context);
        mContext = context;
        mKeyUserRequest2 = keyRequest2;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_member_set_host);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        mSharedPreferences = mContext.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        currentGroupId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");

        btnSetVice = (Button) findViewById(R.id.btn_create_confirm_set_host);
        btnCacel = (Button) findViewById(R.id.btn_create_confirm_cancel_set_host);
        btnSetVice.setOnClickListener(this);
        btnCacel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_confirm_set_host:
                try {
                    doSetHost();
                } catch (Exception e) {

                }
                dismiss();
                break;
            case R.id.btn_create_confirm_cancel_set_host:
                dismiss();
                break;
            default:
                break;
        }
    }

    private int mCount;

    private void doSetHost() {
        mCount = 0;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("groups-users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(currentGroupId) && groupUser.isHost() == true && !groupUser.getGroupsUsersID().equals(mKeyUserRequest2)) {
                            mFirebaseDatabase.getReference("groups-users").child(groupUser.getGroupsUsersID()).child("host").setValue(false);
                            mFirebaseDatabase.getReference("groups-users").child(mKeyUserRequest2).child("host").setValue(true);
                            mCount++;
                        }
                    }
                    if (mCount == 0) {
                        mFirebaseDatabase.getReference("groups-users").child(mKeyUserRequest2).child("host").setValue(true);
                    }
                }
                mFirebaseDatabase.getReference("groups-users").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
