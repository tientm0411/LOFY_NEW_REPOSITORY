package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.MemberAdapter;
import lofy.fpt.edu.vn.lofy_ver108.adapter.UserRequestAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.AppFunctions;
import lofy.fpt.edu.vn.lofy_ver108.business.ResizeListview;
import lofy.fpt.edu.vn.lofy_ver108.dbo.QueryFirebase;
import lofy.fpt.edu.vn.lofy_ver108.entity.ColorUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;
import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MY_TAG";
    private View rootView;
    private Pinview ciCode;
    private Button btnCreate;
    private EditText edtGroupName;
    private ListView lvUserRequest;
    private ListView lvMember;
    private ListView lvVice;
    private TextView tvColorPicker;
    private Button btnColorPicker;
    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton btnSetTrack;
    private FloatingActionButton btnDeleteGroup;
    private FloatingActionButton btnStart;
    private FloatingActionButton btnQuit;

    private MemberAdapter memberAdapter;
    private UserRequestAdapter userRequestAdapter;
    private AppFunctions appFunctions;
    private DialogSetMemberInCreateFragment dialogSetMemberInCreateFragment;
    private MapGroupFragment mapGroupFragment;
    private StartActivity startActivity;
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;
    private DatabaseReference userRequestRef;
    private DatabaseReference groupUserRef;
    private DatabaseReference groupRef;

    private String currentGroupId = "";
    private String currentGroupUSerId = "";
    private String userID = "";
    private String userName = "";
    private String userPhone = "";
    private String userFbID = "";
    private String userAvatar = "";
    private String groupID = "";
    private String groupName = "";
    private ArrayList<String> colorList;

    public CreateGroupFragment() {
    }

    private void registerVice() {
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alVice = new ArrayList<>();
        final ArrayList<GroupUser> alGroupUserMem = new ArrayList<>();
        lvVice.setAdapter(null);
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    alGroupUserMem.clear();
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        // th1 : group has 1 vice before
                        if (groupUser.getGroupId().equals(ciCode.getValue().toString().toUpperCase()) && groupUser.isVice() == true
                                && groupUser.isStatusUser() == true && groupUser.isHost() == false) {
                            alGroupUser.add(groupUser);
                            break;
                        }
                        // th2 : group has't vice before
//                         else if (groupUser.getGroupId().equals(ciCode.getValue().toString().toUpperCase()) && groupUser.isVice() == false
//                                && groupUser.isStatusUser() == true && groupUser.isHost() == false) {
//                            alGroupUserMem.add(groupUser);
//                        }
                    }
                }
//                if (alGroupUser.size() == 0 && alGroupUserMem.size() >= 0 && !alGroupUserMem.isEmpty()) {
//                    groupUserRef.child(alGroupUserMem.get(0).getGroupsUsersID()).child("vice").setValue(true);
//                    alGroupUser.clear();
//                    alGroupUser.add(alGroupUserMem.get(0));
//                    alGroupUserMem.clear();
//                }

                // get list detail user just request
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren() && !alGroupUser.isEmpty()) {
                            alVice.clear();
                            for (DataSnapshot us : dataSnapshot.getChildren()) {
                                User ur = us.getValue(User.class);
                                if (ur.getUserId().equals(alGroupUser.get(0).getUserId()) && !ur.getUserId().equals(userID)) {
                                    alVice.add(ur);
                                }
                            }
                        }
                        // set list request to listview
                        memberAdapter = new MemberAdapter(rootView.getContext(), alVice);
                        lvVice.setAdapter(memberAdapter);
                        lvVice.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
                        ResizeListview.setListViewHeightBasedOnChildren(lvVice);
                        memberAdapter.notifyDataSetChanged();
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

    private void registerListMember() {
        final ArrayList<GroupUser> alGroupUser = new ArrayList<>();
        final ArrayList<User> alMem = new ArrayList<>();
        lvMember.setAdapter(null);
        groupUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupUser.clear();
                    for (DataSnapshot gu : dataSnapshot.getChildren()) {
                        GroupUser groupUser = gu.getValue(GroupUser.class);
                        if (groupUser.getGroupId().equals(ciCode.getValue().toString().toUpperCase()) && groupUser.isVice() == false
                                && groupUser.isHost() == false && groupUser.isStatusUser() == true) {
                            alGroupUser.add(groupUser);
                        }
                    }
                }
                // get list detail user just request
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
                        memberAdapter = new MemberAdapter(rootView.getContext(), alMem);
                        lvMember.setAdapter(memberAdapter);
                        lvMember.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
                        ResizeListview.setListViewHeightBasedOnChildren(lvMember);
                        memberAdapter.notifyDataSetChanged();
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

    private void registerUserRequest() {
        final ArrayList<UserRequest> alRequest = new ArrayList<>(); // list user request this group
        final ArrayList<User> alMem = new ArrayList<>(); // list user just request
        lvUserRequest.setAdapter(null);
        userRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alRequest.clear();
                    for (DataSnapshot us : dataSnapshot.getChildren()) {
                        UserRequest userRequest = us.getValue(UserRequest.class);
                        if (userRequest.getGroupId().toUpperCase().equals(ciCode.getValue().toString().toUpperCase())) {
                            alRequest.add(userRequest);
                        }
                    }
                }
                // get list detail user just request
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            alMem.clear();
                            for (DataSnapshot us : dataSnapshot.getChildren()) {
                                User ur = us.getValue(User.class);
                                for (int i = 0; i < alRequest.size(); i++) {
                                    if (ur.getUserId().equals(alRequest.get(i).getUserId())) {
                                        alMem.add(ur);
                                    }
                                }
                            }
                        }
                        // set list request to listview
                        userRequestAdapter = new UserRequestAdapter(rootView.getContext(), alMem);
                        lvUserRequest.setAdapter(userRequestAdapter);
                        lvUserRequest.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
                        ResizeListview.setListViewHeightBasedOnChildren(lvUserRequest);
                        userRequestAdapter.notifyDataSetChanged();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        initView();
        registerUserRequest();
        registerListMember();
        registerVice();
        //scheckOldGroup();
        return rootView;
    }

    private void initView() {
        edtGroupName = (EditText) rootView.findViewById(R.id.createGroup_edt_group_name);
        ciCode = (Pinview) rootView.findViewById(R.id.createGroup_ci_code);
        btnCreate = (Button) rootView.findViewById(R.id.createGroup_btn_create_group);
        btnCreate.setOnClickListener(this);
        lvUserRequest = (ListView) rootView.findViewById(R.id.lv_create_user_request);
        lvMember = (ListView) rootView.findViewById(R.id.lv_create_member);
        lvVice = (ListView) rootView.findViewById(R.id.lv_create_vice);
        tvColorPicker = (TextView) rootView.findViewById(R.id.tv_create_color_picker);
        btnColorPicker = (Button) rootView.findViewById(R.id.btn_create_choose_color_picker);
        btnColorPicker.setOnClickListener(this);
        materialDesignFAM = (FloatingActionMenu) rootView.findViewById(R.id.floating_menu_create_group);
        btnSetTrack = (FloatingActionButton) rootView.findViewById(R.id.fab_create_set_track);
        btnStart = (FloatingActionButton) rootView.findViewById(R.id.fab_create_start);
        btnDeleteGroup = (FloatingActionButton) rootView.findViewById(R.id.fab_create_start);
        btnQuit = (FloatingActionButton) rootView.findViewById(R.id.fab_create_quit_group);
        btnSetTrack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnDeleteGroup.setOnClickListener(this);
        btnQuit.setOnClickListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference("users");
        userRequestRef = mFirebaseDatabase.getReference("user-requests");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        groupRef = mFirebaseDatabase.getReference("groups");

        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        userName = mSharedPreferences.getString(IntroApplicationActivity.USER_NAME, "NA");
        userPhone = mSharedPreferences.getString(IntroApplicationActivity.USER_PHONE, "NA");
        userAvatar = mSharedPreferences.getString(IntroApplicationActivity.USER_URL_AVATAR, "NA");

        appFunctions = new AppFunctions();

        ciCode.setValue(appFunctions.randomString(6));

        lvVice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User u = (User) parent.getItemAtPosition(position);
                final String keyGroupUser = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA") + u.getUserId();
                dialogSetMemberInCreateFragment = new DialogSetMemberInCreateFragment(rootView.getContext(), keyGroupUser);
                dialogSetMemberInCreateFragment.show();
            }
        });
        lvUserRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User u = (User) parent.getItemAtPosition(position);
                final String keyUserRequest = u.getUserId() + mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
                switch (view.getId()) {
                    case R.id.btn_item_request_user_accecpt:
                        userRequestRef.child(keyUserRequest).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // push group-user to firebase;
                                String mGroupUserID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA") + u.getUserId();
                                GroupUser groupUser = new GroupUser(mGroupUserID, u.getUserId(), mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA"),
                                        false, false, "NA", "NA", 0.0, true);
                                groupUserRef.child(mGroupUserID).setValue(groupUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(rootView.getContext(), "Lỗi !", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.btn_item_request_user_deny:
                        userRequestRef.child(keyUserRequest).removeValue();
                        break;
                    default:
                        break;
                }

            }
        });
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User u = (User) parent.getItemAtPosition(position);
                final String keyGroupUser = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA") + u.getUserId();
                dialogSetMemberInCreateFragment = new DialogSetMemberInCreateFragment(rootView.getContext(), keyGroupUser);
                dialogSetMemberInCreateFragment.show();
            }
        });

        colorList = new ArrayList<>();
        colorList.add("#FF0000");
        colorList.add("#FFFF00");
        colorList.add("#78FF00");
        colorList.add("#00FFFF");
        colorList.add("#007CFF");
        colorList.add("#F000FF");
        colorList.add("#698992");
        colorList.add("#5C763C");
        colorList.add("#FFF6BC");
        colorList.add("#FFD3BC");

    }

    // choose color of marker
    private void chooseColor() {
        // the first choose color bofore create group
        if (mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA").equals("NA")) {
            final ColorPicker colorPicker = new ColorPicker(getActivity());
            colorPicker
                    .addListenerButton("Chọn", new ColorPicker.OnButtonListener() {
                        @Override
                        public void onClick(View v, int position, int color) {
                            if (position == 0) {
                                String hexColor = String.format("#%06X", (0xFFFFFF & Color.parseColor(colorList.get(0))));
                                tvColorPicker.setBackgroundColor(Color.parseColor(colorList.get(0)));
                                btnColorPicker.setTextColor(Color.parseColor(colorList.get(0)));
                                tvColorPicker.setHint(colorList.get(0));
                                tvColorPicker.setHintTextColor(Color.parseColor(colorList.get(0)));
                            } else {
                                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                                tvColorPicker.setBackgroundColor(color);
                                btnColorPicker.setTextColor(color);
                                tvColorPicker.setHint(hexColor);
                                tvColorPicker.setHintTextColor(color);
                            }
                            colorPicker.dismissDialog();
                        }
                    })
                    .addListenerButton("Hủy", new ColorPicker.OnButtonListener() {
                        @Override
                        public void onClick(View v, int position, int color) {
                            colorPicker.dismissDialog();
                        }
                    })
                    .disableDefaultButtons(true)
                    .setDefaultColorButton(Color.parseColor(colorList.get(0)))
                    .setColumns(5)
                    .setColors(colorList)
                    .show();
        } else {
            // change color
            final DatabaseReference newRef = groupRef.child(ciCode.getValue().toString().toUpperCase()).child("colorUser");
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
                                    groupUserRef.child(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA")).child("userColor").setValue(hexColor);
                                    groupRef.child(ciCode.getValue().toString().toUpperCase()).child("colorUser").child(String.valueOf(position)).
                                            setValue(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_COLOR, "NA"));
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
                                    groupUserRef.child(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA")).child("userColor").setValue(hexColor);
                                    groupRef.child(ciCode.getValue().toString().toUpperCase()).child("colorUser").child(String.valueOf(position)).
                                            setValue(mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_COLOR, "NA"));
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
        }


    }

    private void createGroup() {
        groupID = ciCode.getValue().toString();
        groupName = edtGroupName.getText().toString();
        String colorHost = tvColorPicker.getHint().toString();
        if (groupName == null || groupName.equals("") || groupID == null || groupID.equals("")) {
            Toast.makeText(rootView.getContext(), "Nhập tên nhóm !", Toast.LENGTH_SHORT).show();
        } else {
            // push group to firebase
            // Date currentTime = Calendar.getInstance().getTime();
            colorList.remove(colorHost);
            Group group = new Group(groupID, groupName, "NA",
                    "NA", 0.0, 0.0, 0.0, 0.0, "open", colorList);
            groupRef.child(groupID).setValue(group);

            // set groupId for sharefreference
            editor.putString(IntroApplicationActivity.GROUP_ID, groupID);
            editor.putString(IntroApplicationActivity.GROUP_NAME, groupName);
            editor.putString(IntroApplicationActivity.GROUP_USER_COLOR, colorHost);
            editor.putString(IntroApplicationActivity.IS_HOST, "true");
            editor.putString(IntroApplicationActivity.GROUP_USER_ID, groupID + userID);
            editor.apply();

            // push group-user to firebase
            currentGroupUSerId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA");
            currentGroupId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
            GroupUser groupUser = new GroupUser(currentGroupUSerId, userID, currentGroupId, true, false, colorHost, "NA", 0.0, true);
            groupUserRef.child(currentGroupUSerId).setValue(groupUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(rootView.getContext(), "Tạo thành công !", Toast.LENGTH_SHORT).show();
                    btnCreate.setClickable(false);
                    btnCreate.setFocusable(false);
                    edtGroupName.clearFocus();
                }
            });

        }

    }

    // check if this is old group or not
    private void checkOldGroup() {
        if (groupID.equals("NA")) {
            ciCode.setValue(groupID);
            edtGroupName.setText(groupName);
            btnCreate.setEnabled(false);
            btnCreate.setFocusable(false);
        } else {
            ciCode.setValue(appFunctions.randomString(6));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.createGroup_btn_create_group:
                createGroup();
                break;
            case R.id.btn_create_choose_color_picker:
                chooseColor();
                break;
            case R.id.fab_create_set_track:
                intent = new Intent(rootView.getContext(), MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_create_start:
//                if (mapGroupFragment == null) {
//                    mapGroupFragment = new MapGroupFragment();
//                }
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.ln_main, mapGroupFragment, MapGroupFragment.class.getName())
//                        .addToBackStack(null)
//                        .commit();
                intent = new Intent(rootView.getContext(), StartActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_create_delete_group:

                break;
            case R.id.fab_create_quit_group:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
