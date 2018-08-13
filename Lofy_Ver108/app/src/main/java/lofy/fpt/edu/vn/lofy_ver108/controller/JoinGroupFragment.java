package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.JoinMemberAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.ResizeListview;
import lofy.fpt.edu.vn.lofy_ver108.dbo.QueryFirebase;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;
import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinGroupFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MY_TAG";
    private View rootView;
    private EditText ciEnterCode;
    private Button btnOkCode;
    private ListView lvAllMember;
    private ListView lvHost;
    private ListView lvVice;
    private TextView tvGroupName;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private JoinMemberAdapter groupMemberAdapter;
    private ProfileFragment profileFragment;

    private TextView tvColorPicker;
    private Button btnColorPicker;

    private String currentGroupId = "";
    private String userID = "";
    private String userName = "";
    private String userPhone = "";
    private String userFbID = "";
    private String userAvatar = "";
    private String groupID = "";
    private String groupName = "";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;
    private DatabaseReference userRequestRef;
    private DatabaseReference groupUserRef;
    private DatabaseReference groupRef;

    public JoinGroupFragment() {

    }

    private String mCode = "";
    private int mCount;
    private String gName = "";
    private ArrayList<String> colorList;

    // set listview host
    private void registerHost() {
        mCount = 0;
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alHost = new ArrayList<>();
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(mCode.toUpperCase())
                                && groupUser.isHost() == true && groupUser.isVice() == false && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                        if (groupUser.getUserId().equals(userID) && groupUser.getGroupId().equals(mCode.toUpperCase())) {
                            mCount++;
                        }
                    }
                }
                if (mCount == 0) {
                    alGroupUser.clear();
                }
                // get list detail user just request
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            alHost.clear();
                            for (DataSnapshot us : dataSnapshot.getChildren()) {
                                User ur = us.getValue(User.class);
                                for (int i = 0; i < alGroupUser.size(); i++) {
                                    if (ur.getUserId().equals(alGroupUser.get(i).getUserId())) {
                                        alHost.add(ur);
                                    }
                                }
                            }
                        }
                        // set list request to listview
                        groupMemberAdapter = new JoinMemberAdapter(rootView.getContext(), alHost);
                        lvHost.setAdapter(groupMemberAdapter);
                        groupMemberAdapter.notifyDataSetChanged();
                        ResizeListview.setListViewHeightBasedOnChildren(lvHost);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // set listview vice
    private void registerVice() {
        mCount = 0;
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alVice = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(mCode.toUpperCase())
                                && groupUser.isHost() == false && groupUser.isVice() == true && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                        if (groupUser.getUserId().equals(userID) && groupUser.getGroupId().equals(mCode.toUpperCase())) {
                            mCount++;
                        }
                    }
                }
                if (mCount == 0) {
                    alGroupUser.clear();
                }
                // get list detail user just request
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            alVice.clear();
                            for (DataSnapshot us : dataSnapshot.getChildren()) {
                                User ur = us.getValue(User.class);
                                for (int i = 0; i < alGroupUser.size(); i++) {
                                    if (ur.getUserId().equals(alGroupUser.get(i).getUserId())) {
                                        alVice.add(ur);
                                    }
                                }
                            }
                        }
                        // set list request to listview
                        groupMemberAdapter = new JoinMemberAdapter(rootView.getContext(), alVice);
                        lvVice.setAdapter(groupMemberAdapter);
                        groupMemberAdapter.notifyDataSetChanged();
                        ResizeListview.setListViewHeightBasedOnChildren(lvVice);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // set listview member
    private void registerListMember() {
        mCount = 0;
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alMem = new ArrayList<>();
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(mCode.toUpperCase()) && groupUser.isHost() == false
                                && groupUser.isVice() == false && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                        if (groupUser.getUserId().equals(userID) && groupUser.getGroupId().equals(mCode.toUpperCase())) {
                            gName = groupRef.child(groupUser.getGroupId()).toString();
                            mCount++;
                        }
                    }
                }
                if (mCount == 0) {
                    alGroupUser.clear();
                } else {
                    colorList = new ArrayList<>();
                    groupRef.child(ciEnterCode.getText().toString().toUpperCase()).child("colorUser").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            colorList.clear();
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String g = ds.getValue(String.class);
                                    colorList.add(g);
                                }
                            }
                            if (tvColorPicker.getHint().toString().equals("NA")) {
                                tvColorPicker.setBackgroundColor(Color.parseColor(colorList.get(0)));
                                btnColorPicker.setTextColor(Color.parseColor(colorList.get(0)));
                                tvColorPicker.setHint(colorList.get(0));
                                tvColorPicker.setHintTextColor(Color.parseColor(colorList.get(0)));

                                groupUserRef.child(ciEnterCode.getText().toString().toUpperCase() + mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA")).
                                        child("userColor").setValue(colorList.get(0));
                                colorList.remove(colorList.get(0));
                                groupRef.child(ciEnterCode.getText().toString().toUpperCase()).child("colorUser").setValue(colorList);
                            }

                            Log.d(TAG, "colortHint: " + tvColorPicker.getHint().toString());
                            // set groupId for sharepreference
                            editor.putString(IntroApplicationActivity.GROUP_ID, mCode.toUpperCase());
                            editor.putString(IntroApplicationActivity.GROUP_NAME, gName);
                            editor.putString(IntroApplicationActivity.GROUP_USER_ID, mCode.toUpperCase()
                                    + mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA"));
                            editor.putString(IntroApplicationActivity.IS_HOST, "false");
                            editor.putString(IntroApplicationActivity.GROUP_USER_COLOR, tvColorPicker.getHint().toString());
                            editor.apply();
                            tvGroupName.setText(gName);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                //  list member
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            alMem.clear();
                            for (DataSnapshot us : dataSnapshot.getChildren()) {
                                User ur = us.getValue(User.class);
                                for (int i = 0; i < alGroupUser.size(); i++) {
                                    if (ur.getUserId().equals(alGroupUser.get(i).getUserId())) {
                                        alMem.add(ur);
                                    }
                                }
                            }
                        }
                        // set list request to listview
                        groupMemberAdapter = new JoinMemberAdapter(rootView.getContext(), alMem);
                        lvAllMember.setAdapter(groupMemberAdapter);
                        groupMemberAdapter.notifyDataSetChanged();
                        ResizeListview.setListViewHeightBasedOnChildren(lvAllMember);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // check if this is old group or not
    private void checkOldGroup() {
        if (!groupID.equals("NA")) {
            ciEnterCode.setText(groupID);
            tvGroupName.setText(groupName);
            btnOkCode.setEnabled(false);
            btnOkCode.setFocusable(false);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_join_group, container, false);
        initView();
        registerListMember();
        registerHost();
        registerVice();
        // checkOldGroup();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initView() {
        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        userName = mSharedPreferences.getString(IntroApplicationActivity.USER_NAME, "NA");
        userPhone = mSharedPreferences.getString(IntroApplicationActivity.USER_PHONE, "NA");
        userAvatar = mSharedPreferences.getString(IntroApplicationActivity.USER_URL_AVATAR, "NA");

        tvColorPicker = (TextView) rootView.findViewById(R.id.join_tv_color_picker);
        btnColorPicker = (Button) rootView.findViewById(R.id.join_btn_color_picker_member);
        btnColorPicker.setOnClickListener(this);
        tvGroupName = (TextView) rootView.findViewById(R.id.tv_join_group_name);
        ciEnterCode = (EditText) rootView.findViewById(R.id.join_ci_enter_code);
        btnOkCode = (Button) rootView.findViewById(R.id.join_btn_join_send_request);
        btnOkCode.setOnClickListener(this);
        lvAllMember = (ListView) rootView.findViewById(R.id.lv_join_member);
        lvHost = (ListView) rootView.findViewById(R.id.lv_join_host);
        lvVice = (ListView) rootView.findViewById(R.id.lv_join_vice);

        lvVice.setOnItemClickListener(this);
        lvHost.setOnItemClickListener(this);
        lvAllMember.setOnItemClickListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference("users");
        userRequestRef = mFirebaseDatabase.getReference("user-requests");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        groupRef = mFirebaseDatabase.getReference("groups");


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_btn_join_send_request:
                joinGroup();
                break;
            case R.id.join_btn_color_picker_member:
                chooseColor();
                break;
            default:
                break;
        }
    }


    // send request to join group
    private void joinGroup() {
        mCode = ciEnterCode.getText().toString().toUpperCase();
        final String urKey = userID + mCode;
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mCount = 0;
                    for (DataSnapshot gr : dataSnapshot.getChildren()) {
                        Group group = gr.getValue(Group.class);
                        if (group.getGroupId().toString().toUpperCase().equals(mCode.toUpperCase())) {
                            UserRequest userRequest = new UserRequest(userID, mCode.toUpperCase());
                            userRequestRef.child(urKey).setValue(userRequest);
                            Toast.makeText(rootView.getContext(), "Vui lòng đợi chấp nhận !", Toast.LENGTH_SHORT).show();
                            mCount++;
                            break;
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(rootView.getContext(), "Vui lòng đợi chấp nhận !", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(rootView.getContext(), "Nhóm không tồn tại !", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            });
                        }
                    }
                    if (mCount > 0) {
                        Toast.makeText(rootView.getContext(), "Nhóm không tồn tại !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userRequestRef.removeEventListener(this);
                }
                groupRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    // choose color of marker
    private void chooseColor() {
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                colorList = new ArrayList<>();
                mCount = 0;
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getUserId().equals(userID) && groupUser.getGroupId().equals(ciEnterCode.getText().toString().toUpperCase())) {
                            mCount++;
                        }
                    }
                    if (mCount == 0) {
                        Toast.makeText(rootView.getContext(), "Bạn cần tham gia vào một nhóm trước !", Toast.LENGTH_SHORT).show();
                        groupUserRef.removeEventListener(this);
                        return;
                    } else {
                        final DatabaseReference newRef = groupRef.child(ciEnterCode.getText().toString().toUpperCase()).child("colorUser");
                        newRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                colorList.clear();
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        String g = ds.getValue(String.class);
                                        colorList.add(g);
                                    }
                                    final ColorPicker colorPicker = new ColorPicker(getActivity());
                                    colorPicker.addListenerButton("Chọn", new ColorPicker.OnButtonListener() {
                                        @Override
                                        public void onClick(View v, int position, int color) {
                                            if (position == 0) {
                                                String hexColor = String.format("#%06X", (0xFFFFFF & Color.parseColor(colorList.get(0))));
                                                tvColorPicker.setBackgroundColor(Color.parseColor(colorList.get(0)));
                                                btnColorPicker.setTextColor(Color.parseColor(colorList.get(0)));
                                                if (!mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA").equals("NA")) {
                                                    groupUserRef.child(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA")).child("userColor").setValue(hexColor);
                                                    groupRef.child(ciEnterCode.getText().toString().toUpperCase()).child("colorUser").child(String.valueOf(position)).
                                                            setValue(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_COLOR, "NA"));
                                                }
                                                editor.putString(IntroApplicationActivity.GROUP_USER_COLOR, hexColor);
                                                editor.apply();
                                                tvColorPicker.setHint(colorList.get(0));
                                                tvColorPicker.setHintTextColor(Color.parseColor(colorList.get(0)));
                                            } else {
                                                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                                                tvColorPicker.setBackgroundColor(color);
                                                btnColorPicker.setTextColor(color);
                                                tvColorPicker.setHint(hexColor);
                                                tvColorPicker.setHintTextColor(color);
                                                if (!mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA").equals("NA")) {
                                                    groupUserRef.child(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA")).child("userColor").setValue(hexColor);
                                                    groupRef.child(ciEnterCode.getText().toString().toUpperCase()).child("colorUser").child(String.valueOf(position)).
                                                            setValue(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_COLOR, "NA"));
                                                }
                                                editor.putString(IntroApplicationActivity.GROUP_USER_COLOR, hexColor);
                                                editor.apply();
                                                tvColorPicker.setHint(hexColor);
                                                tvColorPicker.setHintTextColor(color);
                                            }
                                            colorPicker.dismissDialog();
                                        }
                                    });
                                    colorPicker.addListenerButton("Hủy", new ColorPicker.OnButtonListener() {
                                        @Override
                                        public void onClick(View v, int position, int color) {
                                            colorPicker.dismissDialog();
                                        }
                                    });
                                    colorPicker.disableDefaultButtons(true);
                                    colorPicker.setDefaultColorButton(Color.parseColor(colorList.get(0)));
                                    colorPicker.setColumns(5);
                                    colorPicker.setColors(colorList);
                                    colorPicker.show();
                                }
                                newRef.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                        groupUserRef.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final User u = (User) adapterView.getItemAtPosition(i);
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.ln_main, profileFragment, ProfileFragment.class.getName())
                .addToBackStack(null)
                .commit();

//        Toast.makeText(rootView.getContext(), "Xem trang cá nhân: " + u.getUserName(), Toast.LENGTH_SHORT).show();
    }
}