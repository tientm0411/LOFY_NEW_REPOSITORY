package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import lofy.fpt.edu.vn.lofy_ver108.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        btnLogout = (Button) rootView.findViewById(R.id.btn_profile_logout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_logout:
                disconnectFromFacebook();
                break;
            default:
                break;
        }
    }



    public void disconnectFromFacebook() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        FacebookSdk.sdkInitialize(rootView.getContext());
                        if (AccessToken.getCurrentAccessToken() == null) {
                            return; // already logged out
                        }else{
                            LoginManager.getInstance().logOut();
                            Intent intent = new Intent(rootView.getContext(), LoginActivity.class);
                            startActivity(intent);
                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setMessage("Đăng xuất?").setPositiveButton("Xác nhận", dialogClickListener)
                .setNegativeButton("Hủy", dialogClickListener).show();

    }
}
