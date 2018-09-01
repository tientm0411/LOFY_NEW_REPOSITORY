package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentGroupFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private View rootView;
    private Button btnCQuitGroup;
    private Button btnGoOldGroup;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String userID = "";
    private String isHost = "";
    private String currentGroupId = "";

    private CreateGroupFragment createGroupFragment;
    private JoinGroupFragment joinGroupFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_current_group, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initView() {
        btnCQuitGroup = (Button) rootView.findViewById(R.id.currentGroup_btn_quit_group);
        btnCQuitGroup.setOnClickListener(this);
        btnGoOldGroup = (Button) rootView.findViewById(R.id.currentGroup_btn_go_old_group);
        btnGoOldGroup.setOnClickListener(this);
        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        isHost = mSharedPreferences.getString(IntroApplicationActivity.IS_HOST, "NA");
        currentGroupId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.currentGroup_btn_quit_group:
                quitGroup();
                break;
            case R.id.currentGroup_btn_go_old_group:
                goOldGroup();
            default:
                break;
        }
    }

    private void goOldGroup() {
        switch (isHost) {
            case "NA":
                Toast.makeText(rootView.getContext(), "Bạn chưa tham gia vào bất cứ nhóm nào !", Toast.LENGTH_LONG).show();
                break;
            case "true":
                if (createGroupFragment == null) {
                    createGroupFragment = new CreateGroupFragment("home");
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_main, createGroupFragment, CreateGroupFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
                break;
            case "false":
                if (joinGroupFragment == null) {
                    joinGroupFragment = new JoinGroupFragment("home");
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_main, joinGroupFragment, JoinGroupFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                break;
        }
    }

    private void quitGroup() {
        String guID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID,"NA");
        if("NA".equals(guID)) {
            String substrGId = guID.substring(0, 6);
            String substrUId = guID.substring(6, 21);
            GroupUser gu1 = new GroupUser(guID, substrUId, substrGId, false, false, "NA", 0.0, false);
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups-users");
            groupRef.child(guID).setValue(gu1);

            editor.putString(IntroApplicationActivity.GROUP_ID, "NA");
            editor.putString(IntroApplicationActivity.GROUP_NAME, "NA");
            editor.putString(IntroApplicationActivity.GROUP_USER_ID, "NA");
            editor.putString(IntroApplicationActivity.IS_HOST, "NA");
            editor.apply();
            Toast.makeText(rootView.getContext(), "Done !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

}
