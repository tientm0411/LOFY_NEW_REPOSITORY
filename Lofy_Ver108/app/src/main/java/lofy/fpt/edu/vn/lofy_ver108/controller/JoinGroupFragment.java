package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
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

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
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

    private FloatingActionButton btnStart;
    private FloatingActionButton btnQuit;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;
    private DatabaseReference userRequestRef;
    private DatabaseReference groupUserRef;
    private DatabaseReference groupRef;

    private String from;

    @SuppressLint("ValidFragment")
    public JoinGroupFragment(String from) {
        this.from = from;

    }

    private String mCode = "";
    private int mCount = 0;


    // set listview host
    private void registerHost() {
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alHost = new ArrayList<>();
        valueEventListenerHostUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alHost.clear();
                    for (DataSnapshot us : dataSnapshot.getChildren()) {
                        User ur = us.getValue(User.class);
                        for (int i = 0; i < alGroupUser.size(); i++) {
                            if (alGroupUser.get(i).getUserId().equals(ur.getUserId())) {
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
        };
        valueEventListenerHostGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
                    String userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
                    alGroupUser.clear();
                    mCount = 0;
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupID.equals(groupUser.getGroupId())
                                && groupUser.isHost() == true && groupUser.isVice() == false && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                        if (groupUser.getUserId().equals(userID) && groupUser.getGroupId().equals(groupID)) {
                            mCount++;
                        }
                    }
                }
                if (mCount == 0) {
                    alGroupUser.clear();
                }
                // get list detail user just request
                userRef.addValueEventListener(valueEventListenerHostUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        groupUserRef.addValueEventListener(valueEventListenerHostGroupUser);
    }


    // set listview vice
    private void registerVice() {
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alVice = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        valueEventListenerViceUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alVice.clear();
                    for (DataSnapshot us : dataSnapshot.getChildren()) {
                        User ur = us.getValue(User.class);
                        for (int i = 0; i < alGroupUser.size(); i++) {
                            if (alGroupUser.get(i).getUserId().equals(ur.getUserId())) {
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
        };
        valueEventListenerViceGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
                String userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    mCount = 0;
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupID.equals(groupUser.getGroupId())
                                && groupUser.isHost() == false && groupUser.isVice() == true && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                        if (userID.equals(groupUser.getUserId()) && groupID.equals(groupUser.getGroupId())) {
                            mCount++;
                        }
                    }
                }
                if (mCount == 0) {
                    alGroupUser.clear();
                }
                // get list detail user just request
                userRef.addValueEventListener(valueEventListenerViceUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        groupUserRef.addValueEventListener(valueEventListenerViceGroupUser);

    }


    // set listview member
    private void registerListMember() {
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alMem = new ArrayList<>();

        valueEventListenerMemberUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alMem.clear();
                    for (DataSnapshot us : dataSnapshot.getChildren()) {
                        User ur = us.getValue(User.class);
                        for (int i = 0; i < alGroupUser.size(); i++) {
                            if (alGroupUser.get(i).getUserId().equals(ur.getUserId())) {
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
        };
        valueEventListenerMemberGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String request = mSharedPreferences.getString(IntroApplicationActivity.GROUP_REQUEST, "NA");
                    String userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
                    Log.d(TAG, "request: " + request);
                    alGroupUser.clear();
                    mCount = 0;
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (request.equals(groupUser.getGroupId()) && groupUser.isHost() == false
                                && groupUser.isVice() == false && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }

                        if (userID.equals(groupUser.getUserId()) && request.equals(groupUser.getGroupId()) && groupUser.isStatusUser() == true) {
                            mCount++;
                        }
                    }
                }
                Log.d(TAG, "mCount: " + mCount);
                if (mCount == 0) {
                    alGroupUser.clear();
                } else {
                    // set groupId for sharepreference
                    String getCode = ciEnterCode.getText().toString().toUpperCase();
                    String abc = queryFirebase.getGroupNameById(queryFirebase.getAlGroup(), getCode);
                    editor.putString(IntroApplicationActivity.GROUP_ID, getCode);
                    editor.putString(IntroApplicationActivity.GROUP_NAME, abc);
                    editor.putString(IntroApplicationActivity.GROUP_USER_ID, getCode
                            + mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA"));
                    editor.putString(IntroApplicationActivity.IS_HOST, "false");
                    editor.apply();
                    tvGroupName.setText(abc);
                    btnOkCode.setVisibility(View.GONE);
                    ciEnterCode.clearFocus();
                    ciEnterCode.setFocusable(false);
//                    ciEnterCode.setEnabled(false);
                    ciEnterCode.setClickable(false);
                    Log.d(TAG, "StringABC: " + abc);
                }
                //  list member
                userRef.addValueEventListener(valueEventListenerMemberUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        groupUserRef.addValueEventListener(valueEventListenerMemberGroupUser);
    }

    // check if this is old group or not
    private void checkOldGroup() {
        String mID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        String mName = mSharedPreferences.getString(IntroApplicationActivity.GROUP_NAME, "NA");
        Log.d(TAG, "checkOldGroup: " + mID);
        if (mID.equals("NA") || mID.equals("") || mID.isEmpty()) {
            ciEnterCode.setText("");
            Log.d(TAG, "ping 1!: ");
        } else {
            Log.d(TAG, "ping 2!: ");
            ciEnterCode.setText(mID);
            tvGroupName.setText(mName);
            btnOkCode.setEnabled(false);
            btnOkCode.setFocusable(false);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_join_group, container, false);
        initView();
        checkOldGroup();
        LoadListAsync loadListAsync = new LoadListAsync();
        loadListAsync.execute();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(rootView.getContext(), "Clicked !", Toast.LENGTH_SHORT).show();
                        callParentMethod();
                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }

    public void callParentMethod() {
        getActivity().onBackPressed();
    }

    public class LoadListAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            registerListMember();
            registerHost();
            registerVice();
        }
    }

    private ValueEventListener valueEventListenerHostGroupUser = null;
    private ValueEventListener valueEventListenerHostUser = null;
    private ValueEventListener valueEventListenerViceGroupUser = null;
    private ValueEventListener valueEventListenerViceUser = null;
    private ValueEventListener valueEventListenerMemberGroupUser = null;
    private ValueEventListener valueEventListenerMemberUser = null;

    @Override
    public void onStop() {
        super.onStop();
        groupUserRef.removeEventListener(valueEventListenerHostGroupUser);
        groupUserRef.removeEventListener(valueEventListenerViceGroupUser);
        groupUserRef.removeEventListener(valueEventListenerMemberGroupUser);
        userRef.removeEventListener(valueEventListenerHostUser);
        userRef.removeEventListener(valueEventListenerViceUser);
        userRef.removeEventListener(valueEventListenerMemberUser);
        editor.putString(IntroApplicationActivity.GROUP_REQUEST, "NA");
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        groupUserRef.removeEventListener(valueEventListenerHostGroupUser);
        groupUserRef.removeEventListener(valueEventListenerViceGroupUser);
        groupUserRef.removeEventListener(valueEventListenerMemberGroupUser);
        userRef.removeEventListener(valueEventListenerHostUser);
        userRef.removeEventListener(valueEventListenerViceUser);
        userRef.removeEventListener(valueEventListenerMemberUser);
        editor.putString(IntroApplicationActivity.GROUP_REQUEST, "NA");
        editor.apply();
    }

    private QueryFirebase queryFirebase = new QueryFirebase();

    private void initView() {
        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
//        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
//        userName = mSharedPreferences.getString(IntroApplicationActivity.USER_NAME, "NA");
//        userPhone = mSharedPreferences.getString(IntroApplicationActivity.USER_PHONE, "NA");
//        userAvatar = mSharedPreferences.getString(IntroApplicationActivity.USER_URL_AVATAR, "NA");
//        groupID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
//        groupName = mSharedPreferences.getString(IntroApplicationActivity.GROUP_NAME, "NA");

        tvGroupName = (TextView) rootView.findViewById(R.id.tv_join_group_name);
        ciEnterCode = (EditText) rootView.findViewById(R.id.join_ci_enter_code);
        btnOkCode = (Button) rootView.findViewById(R.id.join_btn_join_send_request);
        btnOkCode.setOnClickListener(this);
        lvAllMember = (ListView) rootView.findViewById(R.id.lv_join_member);
        lvHost = (ListView) rootView.findViewById(R.id.lv_join_host);
        lvVice = (ListView) rootView.findViewById(R.id.lv_join_vice);
        btnStart = (FloatingActionButton) rootView.findViewById(R.id.fab_join_start);
        btnQuit = (FloatingActionButton) rootView.findViewById(R.id.fab_join_quit_group);
        btnStart.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        lvVice.setOnItemClickListener(this);
        lvHost.setOnItemClickListener(this);
        lvAllMember.setOnItemClickListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference("users");
        userRequestRef = mFirebaseDatabase.getReference("user-requests");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        groupRef = mFirebaseDatabase.getReference("groups");
//        queryFirebase = new QueryFirebase();

        editor.putString(IntroApplicationActivity.GROUP_REQUEST, "NA");
        editor.apply();

        String gCheck = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        if (!gCheck.equals("NA")) {
            ciEnterCode.setFocusable(false);
            ciEnterCode.setClickable(false);
        }

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.join_btn_join_send_request:
                joinGroup();
                break;
            case R.id.fab_join_start:
                intent = new Intent(rootView.getContext(), StartActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_join_quit_group:
                joinAlertDialog();
                break;
            default:
                break;
        }
    }

    // create a dialog
    public void joinAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(rootView.getContext()).create();
        alertDialog.setTitle("Thoát ");
        alertDialog.setMessage("Bạn có chắc muốn thoát nhóm hiện tại");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quitGroup();
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                return;
            }
        });
        alertDialog.show();

    }

    private HomeFragment homeFragment;

    private void quitGroup() {
        String guID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA");
        String gID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
        String uID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        if (gID.equals("NA") || gID.equals("") || gID.isEmpty()) {
            try {
                GroupUser gu1 = new GroupUser(guID, uID, gID, false, false, "NA", 0.0, false);
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups-users");
                groupRef.child(guID).setValue(gu1);

                editor.putString(IntroApplicationActivity.GROUP_ID, "NA");
                editor.putString(IntroApplicationActivity.GROUP_NAME, "NA");
                editor.putString(IntroApplicationActivity.GROUP_USER_ID, "NA");
                editor.putString(IntroApplicationActivity.IS_HOST, "NA");
                editor.apply();

                Toast.makeText(rootView.getContext(), "Đã rời khỏi nhóm !", Toast.LENGTH_SHORT).show();
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_main, homeFragment, HomeFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(rootView.getContext(), "Đã rời khỏi nhóm !", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(rootView.getContext(), "Bạn chưa tham gia nhóm nào !", Toast.LENGTH_SHORT).show();
        }

    }

    private ValueEventListener valueEventListenerJoinGroupUser = null;
    private ValueEventListener valueEventListenerJoinGroup = null;
    private ValueEventListener valueEventListenerJoinRequest = null;

    // send request to join group
    private void joinGroup() {
        mCode = ciEnterCode.getText().toString().toUpperCase();
        String userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        final String urKey = userID + mCode;
        mCount = 0;
        valueEventListenerJoinGroup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mCount = 0;
                    for (DataSnapshot gr : dataSnapshot.getChildren()) {
                        Group group = gr.getValue(Group.class);
                        if (mCode.toUpperCase().equals(group.getGroupId().toString().toUpperCase())) {
                            UserRequest userRequest = new UserRequest(userID, mCode.toUpperCase());
                            userRequestRef.child(urKey).setValue(userRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    editor.putString(IntroApplicationActivity.GROUP_REQUEST, mCode);
                                    GroupUser groupUser = new GroupUser("NA", "NA", "NA",
                                            false, false, "NA", 0.0, false);
                                    groupUserRef.child(mCode + userID).setValue(groupUser);
                                    editor.apply();
                                    mCount++;
//                                    Toast.makeText(rootView.getContext(), "Vui lòng đợi chấp nhận !", Toast.LENGTH_SHORT).show();
                                    groupUserRef.removeEventListener(valueEventListenerJoinGroupUser);
                                    groupRef.removeEventListener(valueEventListenerJoinGroup);
                                    return;
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        valueEventListenerJoinGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        GroupUser groupUser = dataSnapshot1.getValue(GroupUser.class);
                        if (mCode.equals(groupUser.getGroupId()) && userID.equals(groupUser.getUserId()) && groupUser.isStatusUser() == true) {
                            return;
                        } else {
                            groupRef.addValueEventListener(valueEventListenerJoinGroup);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        groupUserRef.addValueEventListener(valueEventListenerJoinGroupUser);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final User u = (User) adapterView.getItemAtPosition(i);
        if (profileFragment == null) {
            profileFragment = new ProfileFragment(u.getUserId());
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.ln_main, profileFragment, ProfileFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }
}
