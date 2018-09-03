package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class DialogSetRadius extends Dialog implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context mContext;
    private EditText edtSize;
    private Button btnOk;
    private FirebaseDatabase mFirebaseDatabase;
    private SharedPreferences mSharedPreferences;

    public DialogSetRadius(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private String groupUserId;

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_radius);
        btnOk = (Button) findViewById(R.id.btn_dl_set_radius_ok);
        edtSize = (EditText) findViewById(R.id.edt_dl_set_radius_text);
        mSharedPreferences = mContext.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        groupUserId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double size = Double.parseDouble(edtSize.getText().toString());
                    if (!groupUserId.equals("NA")) {
                        mFirebaseDatabase = FirebaseDatabase.getInstance();
                        mFirebaseDatabase.getReference("groups-users").child(groupUserId).child("sizeRadius").setValue(size);
                        Toast.makeText(mContext, "Đã cập nhật bán kính vòng an toàn !", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Lỗi nhập !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
