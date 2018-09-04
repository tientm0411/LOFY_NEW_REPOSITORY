package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.HistoryGroupApdater;
import lofy.fpt.edu.vn.lofy_ver108.adapter.MemberAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.business.ResizeListview;
import lofy.fpt.edu.vn.lofy_ver108.dbo.QueryFirebase;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private View rootView;
    private Button btnLogout;
    private TextView tvUserName;
    private ImageView ivAva;
    private EditText edtPhoneNumber;
    private EditText edtVerifyCode;
    private Button btnUpdatePhoneNumber;
    private Button btnShowMenu;
    private Button btnConfimmCode;
    private Button btnCancelCode;
    private LinearLayout lnConfirmCode;
    private ListView lvHistoryGroups;
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<Group> alGroups;
    private ArrayList<User> alUsers;
    private ArrayList<String> alGroupIds;
    private String userId;
    private HistoryGroupApdater historyGroupApdater;
    private SharedPreferences mSharedPreferences;
    private String mUserId;

    FirebaseAuth mAuth;

    String codeSent;

    //    public ProfileFragment() {
//        // Required empty public constructor
//    }
    @SuppressLint("ValidFragment")
    public ProfileFragment(String userId) {
        this.userId = userId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        try {
            initView();
//        showHistoryGroup();
            showHistoryAsyn showHistoryAsyn = new showHistoryAsyn();
            showHistoryAsyn.execute();
        } catch (Exception e) {
        }

//        MapMethod mapMethod = new MapMethod(rootView.getContext());
//        String s = mapMethod.getAddress(21.0133712,105.5261999, rootView.getContext());
//        Log.d("onCreateView", s + " ");

        return rootView;
    }

    private void initView() {
        tvUserName = (TextView) rootView.findViewById(R.id.tv_profile_user_name);
        edtPhoneNumber = (EditText) rootView.findViewById(R.id.edt_profile_user_phone);
        edtVerifyCode = (EditText) rootView.findViewById(R.id.edt_profile_code_confirm);

        ivAva = (ImageView) rootView.findViewById(R.id.iv_profile_ava);
        lvHistoryGroups = (ListView) rootView.findViewById(R.id.lv_profile_list_old_group);
        btnLogout = (Button) rootView.findViewById(R.id.btn_profile_logout);
        btnConfimmCode = (Button) rootView.findViewById(R.id.btn_profile_code_confirm);
        btnLogout.setOnClickListener(this);

        btnCancelCode = (Button) rootView.findViewById(R.id.btn_profile_code_cancel);
        btnConfimmCode.setOnClickListener(this);
        btnCancelCode.setOnClickListener(this);
        btnShowMenu = (Button) rootView.findViewById(R.id.btn_profile_show_menu);
        btnShowMenu.setOnClickListener(this);
        lnConfirmCode = (LinearLayout) rootView.findViewById(R.id.ln_profile_confirm);
        lnConfirmCode.setVisibility(View.GONE);
        mSharedPreferences = getActivity().getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mUserId = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        Log.d("initView", mUserId + " ");
        Log.d("initView_2", userId + " ");
        if (!mUserId.equals(userId)) {
            btnLogout.setVisibility(View.GONE);
            btnShowMenu.setVisibility(View.GONE);
        }
        lvHistoryGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mUserId.equals(userId)) {
                    final Group g = (Group) adapterView.getItemAtPosition(i);
//                Toast.makeText(rootView.getContext(), "Name : " + g.getGroupName(), Toast.LENGTH_SHORT).show();
                    delAlertDialog(g.getGroupId());
                    switch (view.getId()) {
                        case R.id.iv_it_history_delete:
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        queryFirebase = new QueryFirebase();

        LoadUserAsyn loadUserAsyn = new LoadUserAsyn();
        loadUserAsyn.execute();
    }

    public void delAlertDialog(String gID) {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(rootView.getContext()).create();
        alertDialog.setTitle("Xóa lịch sử ");
        alertDialog.setMessage("Bạn có chắc muốn xóa nhóm nhóm này ?");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteGroup(gID);
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                return;
            }
        });
        alertDialog.show();

    }

    public void delPhoneNumber() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(rootView.getContext()).create();
        alertDialog.setTitle("Xóa số điện thoại ");
        alertDialog.setMessage("Bạn có chắc muốn xóa số điện thoại?");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mFirebaseDatabase.getReference("users").child(userId).child("userPhone").setValue("NA").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        edtPhoneNumber.setText("NA");
                        edtPhoneNumber.clearFocus();
                        Toast.makeText(rootView.getContext(), "Xóa thành công !", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                return;
            }
        });
        alertDialog.show();

    }


    private void deleteGroup(String gID) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        valueEventListenerDelGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GroupUser groupUser = ds.getValue(GroupUser.class);
                        if (userId.equals(groupUser.getUserId()) && gID.equals(groupUser.getGroupId())) {
                            mFirebaseDatabase.getReference("groups-users").child(groupUser.getGroupsUsersID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(rootView.getContext(), "Xóa thành công !", Toast.LENGTH_SHORT).show();
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
        mFirebaseDatabase.getReference("groups-users").addValueEventListener(valueEventListenerDelGroupUser);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    public class LoadUserAsyn extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
                User u = queryFirebase.getUserById(queryFirebase.getAlUser(), userId);
                Log.d(ProfileFragment.class.getName(), "doInBackground: " + u.getUserName());
                Log.d(ProfileFragment.class.getName(), "doInBackground_2: " + userId);
                return u;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            tvUserName.setText(user.getUserName());
            Picasso.with(rootView.getContext()).load(user.getUrlAvatar()).into(ivAva);
            edtPhoneNumber.setText(user.getUserPhone());
            super.onPostExecute(user);
        }
    }

    private QueryFirebase queryFirebase;

    private void verifySignInCode() {
        String code = edtVerifyCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String mPhone = task.getResult().getUser().getPhoneNumber();
                            //here you can open new activity
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            mFirebaseDatabase.getReference("users").child(userId).child("userPhone").setValue(mPhone).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    lnConfirmCode.setVisibility(View.GONE);
                                    edtPhoneNumber.clearFocus();
                                    Toast.makeText(getApplicationContext(),
                                            "Lưu số điện thoại thành công !", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Mã xác nhận không đúng ! ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {

        String phone = edtPhoneNumber.getText().toString();

        if (phone.isEmpty()) {
            edtPhoneNumber.setError("Mời nhập số điện thoại");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            edtPhoneNumber.setError("Số điện thoại không đúng !");
            edtPhoneNumber.requestFocus();

            return;
        }
        if (phone.length() > 11) {
            edtPhoneNumber.setError("Số điện thoại không đúng !");
            edtPhoneNumber.requestFocus();

            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);
        lnConfirmCode.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),
                "Mã xác nhận đã được gửi, vui lòng đợi ! ", Toast.LENGTH_LONG).show();// OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_logout:
                disconnectFromFacebook();
                break;
            case R.id.btn_profile_show_menu:
                showMenu(view);
                break;
            case R.id.btn_profile_code_confirm:
                verifySignInCode();
                break;
            case R.id.btn_profile_code_cancel:
                lnConfirmCode.setVisibility(View.GONE);
                edtPhoneNumber.clearFocus();
                break;
            default:
                break;
        }
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(rootView.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_history_group);
        popup.show();
    }

    public class showHistoryAsyn extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            showHistoryGroup();
        }
    }

    private void showHistoryGroup() {
        alGroupIds = new ArrayList<>();
        alGroups = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        valueEventListenerShowGroup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroups.clear();
                    for (DataSnapshot gr : dataSnapshot.getChildren()) {
                        Group g = gr.getValue(Group.class);
                        for (int i = 0; i < alGroupIds.size(); i++) {
                            if (alGroupIds.get(i).equals(g.getGroupId())) {
                                alGroups.add(g);
                            }
                        }
                    }
                   try {
                       historyGroupApdater = new HistoryGroupApdater(rootView.getContext(), alGroups);
                       lvHistoryGroups.setAdapter(historyGroupApdater);
                       lvHistoryGroups.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
                       ResizeListview.setListViewHeightBasedOnChildren(lvHistoryGroups);
                       historyGroupApdater.notifyDataSetChanged();
                   }catch (Exception e){

                   }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valueEventListenerShowGroupUser = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroupIds.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GroupUser groupUser = ds.getValue(GroupUser.class);
                        if (groupUser.getUserId().equals(userId)) {
                            alGroupIds.add(groupUser.getGroupId());
                        }
                    }
//                    if (!alGroupIds.isEmpty() && alGroupIds.size() >= 0) {
                    mFirebaseDatabase.getReference("groups").addValueEventListener(valueEventListenerShowGroup);
                }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mFirebaseDatabase.getReference("groups-users").addValueEventListener(valueEventListenerShowGroupUser);
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
                        } else {
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

    private ValueEventListener valueEventListenerDelGroupUser = null;
    private ValueEventListener valueEventListenerShowGroupUser = null;
    private ValueEventListener valueEventListenerShowGroup = null;

    @Override
    public void onStop() {
        super.onStop();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mFirebaseDatabase.getReference("groups").removeEventListener(valueEventListenerShowGroup);
//        mFirebaseDatabase.getReference("groups-users").removeEventListener(valueEventListenerShowGroupUser);
//        mFirebaseDatabase.getReference("groups-users").removeEventListener(valueEventListenerDelGroupUser);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mFirebaseDatabase.getReference("groups").removeEventListener(valueEventListenerShowGroup);
//        mFirebaseDatabase.getReference("groups-users").removeEventListener(valueEventListenerShowGroupUser);
//        mFirebaseDatabase.getReference("groups-users").removeEventListener(valueEventListenerDelGroupUser);
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
//            case R.id.menu_profile_confirm:
//
//                break;
            case R.id.menu_profile_delete:
                try {
                    lnConfirmCode.setVisibility(View.GONE);
                    delPhoneNumber();
                } catch (Exception e) {

                }
                break;
            case R.id.menu_profile_update:
                try {
                    sendVerificationCode();
                } catch (Exception e) {

                }
                break;
        }
        return false;
    }

}
