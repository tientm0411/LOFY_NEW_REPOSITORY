package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class DialogSetMemberInCreateFragment extends Dialog implements View.OnClickListener {

    private Button btnViewProfile;
    private Button btnRemove;
    private Button btnSetVice;
    private Button btnSetHost;

    private DialogSetMemberConfirmRemove dialogSetMemberConfirmRemove;
    private DialogSetMemberConfirmSetVice dialogSetMemberConfirmSetVice;
    private Context mContext;
    private String mKeyUserRequest2 ;

    private ProfileFragment profileFragment;


    public DialogSetMemberInCreateFragment(@NonNull Context context, String keyUserRequest2) {
        super(context);
        mContext = context;
        mKeyUserRequest2 = keyUserRequest2 ;
        initView();
    }


    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_member_list);
//        setCanceledOnTouchOutside(false);
//        setCancelable(false);

        btnViewProfile = (Button) findViewById(R.id.btn_dialog_create_view_profile);
        btnRemove = (Button) findViewById(R.id.btn_dialog_create_remove);
        btnSetVice = (Button) findViewById(R.id.btn_dialog_create_set_vice);
        btnSetHost = (Button) findViewById(R.id.btn_dialog_create_set_host);
        btnViewProfile.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnSetVice.setOnClickListener(this);
        btnSetHost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_create_view_profile:
                dismiss();
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment();
                }
                ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_main, profileFragment, ProfileFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_dialog_create_remove:
                dismiss();
                dialogSetMemberConfirmRemove = new DialogSetMemberConfirmRemove(mContext,mKeyUserRequest2);
                dialogSetMemberConfirmRemove.show();
                break;
            case R.id.btn_dialog_create_set_vice:
                dismiss();
                dialogSetMemberConfirmSetVice = new DialogSetMemberConfirmSetVice(mContext,mKeyUserRequest2);
                dialogSetMemberConfirmSetVice.show();
                break;
            case R.id.btn_dialog_create_set_host:
                Toast.makeText(mContext, "Set host", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


}
