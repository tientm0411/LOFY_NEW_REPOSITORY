package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private View rootView;
    private CreateGroupFragment createGroupFragment;
    private JoinGroupFragment joinGroupFragment;
    private CurrentGroupFragment currentGroupFragment;
    private ProfileFragment profileFragment;
    private Button btnProfile;
    private Button btnCreate;
    private Button btnCurrent;
    private Button btnJoin;
    private Button btnAbout;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String currentGroupId = ""; // current group in share preference
    private String currentGroupUserId = ""; // current group in share preference
    private FirebaseDatabase mFirebaseDatabase;


    public HomeFragment() {
        // add node user-request
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        UserRequest userRequest = new UserRequest("default-data", "default-data");
        mFirebaseDatabase.getReference("user-requests").child("default-data").setValue(userRequest);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        btnProfile = (Button) rootView.findViewById(R.id.btn_home_profile);
//        btnCurrent = (Button) rootView.findViewById(R.id.btn_home_current);
        btnCreate = (Button) rootView.findViewById(R.id.btn_home_create);
        btnJoin = (Button) rootView.findViewById(R.id.btn_home_join);
        btnAbout = (Button) rootView.findViewById(R.id.btn_home_about);

        btnProfile.setOnClickListener(this);
//        btnCurrent.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
        btnAbout.setOnClickListener(this);

        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();Log.d("onClick", currentGroupId+" ");

    }

    @Override
    public void onClick(View v) {
        currentGroupId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        currentGroupUserId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA");
        Log.d("onClick", currentGroupId + " ");
        Log.d("onClick2", currentGroupUserId + " ");
        String userId = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        Log.d("USERRR_ID", userId+"");
        switch (v.getId()) {
            case R.id.btn_home_profile:
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment(userId);
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_main, profileFragment, ProfileFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
                break;
//            case R.id.btn_home_current:
//                if (currentGroupFragment == null) {
//                    currentGroupFragment = new CurrentGroupFragment();
//                }
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.ln_main, currentGroupFragment, CurrentGroupFragment.class.getName())
//                        .addToBackStack(null)
//                        .commit();
//                break;
            case R.id.btn_home_create:
                if (currentGroupId.equals("NA")) {
                    if (createGroupFragment == null) {
                        createGroupFragment = new CreateGroupFragment();
                    }
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ln_main, createGroupFragment, CreateGroupFragment.class.getName())
                            .addToBackStack(null)
                            .commit();
                } else {
                    new DialogConfirmExitGroup(rootView.getContext(), currentGroupUserId).show();
                }
                break;
            case R.id.btn_home_join:
                if (currentGroupId.equals("NA")) {
                    if (joinGroupFragment == null) {
                        joinGroupFragment = new JoinGroupFragment();
                    }
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ln_main, joinGroupFragment, JoinGroupFragment.class.getName())
                            .addToBackStack(null)
                            .commit();
                } else {

                    new DialogConfirmExitGroup(rootView.getContext(), currentGroupUserId).show();
                }
                break;
            case R.id.btn_home_about:
                Intent intent=new Intent(rootView.getContext(),InformationAppActivity.class);
                rootView.getContext().startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
