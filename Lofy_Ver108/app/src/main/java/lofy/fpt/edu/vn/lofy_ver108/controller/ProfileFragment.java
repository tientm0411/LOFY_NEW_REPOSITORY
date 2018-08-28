package lofy.fpt.edu.vn.lofy_ver108.controller;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
import java.util.ArrayList;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.adapter.HistoryGroupApdater;
import lofy.fpt.edu.vn.lofy_ver108.adapter.MemberAdapter;
import lofy.fpt.edu.vn.lofy_ver108.business.ResizeListview;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

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
        initView();
        showHistoryGroup();
        return rootView;
    }

    private void initView() {
        tvUserName = (TextView) rootView.findViewById(R.id.tv_profile_user_name);
        edtPhoneNumber = (EditText) rootView.findViewById(R.id.edt_profile_user_phone);
        edtVerifyCode=(EditText)rootView.findViewById(R.id.edt_profile_code_confirm);

        ivAva = (ImageView) rootView.findViewById(R.id.iv_profile_ava);
        lvHistoryGroups = (ListView) rootView.findViewById(R.id.lv_profile_list_old_group);
        btnLogout = (Button) rootView.findViewById(R.id.btn_profile_logout);
        btnConfimmCode=(Button)rootView.findViewById(R.id.btn_profile_code_confirm);
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
        if (!mUserId.equals(userId)) {
            btnLogout.setVisibility(View.GONE);
        }

//        tvUserName.setText(userId+"");
    }
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
                            //here you can open new activity
                            Toast.makeText(getApplicationContext(),
                                    "Login Successfull", Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
    private void sendVerificationCode() {

        String phone = edtPhoneNumber.getText().toString();

        if (phone.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            edtPhoneNumber.setError("Please enter a valid phone");
            edtPhoneNumber.requestFocus();

            return;
        }
        if (phone.length() > 11) {
            edtPhoneNumber.setError("Please enter a valid phone");
            edtPhoneNumber.requestFocus();

            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                 getActivity(),               // Activity (for callback binding)
                mCallbacks);
        Toast.makeText(getApplicationContext(),
                "Please wait!!! ", Toast.LENGTH_LONG).show();// OnVerificationStateChangedCallbacks
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


    private void showHistoryGroup() {
        Log.d("showHistoryGroup", userId + " ");
        alGroupIds = new ArrayList<>();
        alGroups = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("groups-users").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Log.d("alGroupIds ", alGroupIds.size() + "");
                    if (!alGroupIds.isEmpty() && alGroupIds.size() >= 0) {
                        mFirebaseDatabase.getReference("groups").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    alGroups.clear();
                                    for (DataSnapshot gr : dataSnapshot.getChildren()) {
                                        Group g = gr.getValue(Group.class);
                                        for (int i = 0; i < alGroupIds.size(); i++) {
                                            if (g.getGroupId().equals(alGroupIds.get(i))) {
                                                alGroups.add(g);
                                            }
                                        }
                                    }
                                    Log.d("alGroups ", alGroups.size() + "");
                                    historyGroupApdater = new HistoryGroupApdater(rootView.getContext(), alGroups);
                                    lvHistoryGroups.setAdapter(historyGroupApdater);
                                    lvHistoryGroups.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
                                    ResizeListview.setListViewHeightBasedOnChildren(lvHistoryGroups);
                                    historyGroupApdater.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
//            case R.id.menu_profile_confirm:
//
//                break;
            case R.id.menu_profile_delete:

                lnConfirmCode.setVisibility(View.GONE);
                break;
            case R.id.menu_profile_update:
                sendVerificationCode();
                lnConfirmCode.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }
}
