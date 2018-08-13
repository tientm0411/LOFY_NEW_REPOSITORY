package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class DialogSetMemberConfirmRemove extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String mKeyUserRequest2;
    private Button btnRemove;
    private Button btnCacel;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;

    public DialogSetMemberConfirmRemove(@NonNull Context context, String keyRequest2) {
        super(context);
        mContext = context;
        mKeyUserRequest2 = keyRequest2;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_remove_member);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        btnRemove = (Button) findViewById(R.id.btn_create_confirm_delete);
        btnCacel = (Button) findViewById(R.id.btn_create_confirm_cancel);
        btnRemove.setOnClickListener(this);
        btnCacel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_confirm_delete:
                doRemove();
                dismiss();
                break;
            case R.id.btn_create_confirm_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void doRemove() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference("groups-users");
        mReference.child(mKeyUserRequest2).child("statusUser").setValue(false);;
    }
}
