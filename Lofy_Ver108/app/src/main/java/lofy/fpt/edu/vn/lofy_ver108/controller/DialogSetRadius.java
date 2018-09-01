package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class DialogSetRadius extends Dialog {
    private Context mContext;
    private EditText edtSize;
    private Button btnOk;

    public DialogSetRadius(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_radius);
        btnOk = (Button) findViewById(R.id.btn_dl_set_radius_ok);
        edtSize = (EditText) findViewById(R.id.edt_dl_set_radius_text);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
