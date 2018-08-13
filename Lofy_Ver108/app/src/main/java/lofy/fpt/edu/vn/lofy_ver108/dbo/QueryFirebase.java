package lofy.fpt.edu.vn.lofy_ver108.dbo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.controller.IntroApplicationActivity;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

public class QueryFirebase implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userRef;
    private DatabaseReference userRequestRef;
    private DatabaseReference groupUserRef;
    private DatabaseReference groupRef;
    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;

    private String currentGroupId;
    private String currentGroupUSerId;
    private String userID = "";
    private String userName = "";
    private String userPhone = "";
    private String userFbID = "";
    private String userAvatar = "";
    private String groupID = "";
    private String groupName = "";

    public QueryFirebase(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userRef = mFirebaseDatabase.getReference("users");
        userRequestRef = mFirebaseDatabase.getReference("user-requests");
        groupUserRef = mFirebaseDatabase.getReference("groups-users");
        groupRef = mFirebaseDatabase.getReference("groups");

        mSharedPreferences = mContext.getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
        userID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        userName = mSharedPreferences.getString(IntroApplicationActivity.USER_NAME, "NA");
        userPhone = mSharedPreferences.getString(IntroApplicationActivity.USER_PHONE, "NA");
        userAvatar = mSharedPreferences.getString(IntroApplicationActivity.USER_URL_AVATAR, "NA");


    }

    public void createGroup(String groupID, String groupName, String color) {
        if (groupName == null || groupName.equals("") || groupID == null || groupID.equals("")) {
            Toast.makeText(mContext, "Nhập tên nhóm !", Toast.LENGTH_SHORT).show();
        } else {
            // push group to firebase
            // Date currentTime = Calendar.getInstance().getTime();
            ArrayList<String> colorList = new ArrayList<>();
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
            Group group = new Group(groupID, groupName, "NA",
                    "NA", 0.0, 0.0, 0.0, 0.0, "open", colorList);
            groupRef.child(groupID).setValue(group);

            // set groupId for sharefreference
            editor.putString(IntroApplicationActivity.GROUP_ID, groupID);
            editor.putString(IntroApplicationActivity.GROUP_NAME, groupName);
            editor.putString(IntroApplicationActivity.IS_HOST, "true");
            editor.putString(IntroApplicationActivity.GROUP_USER_ID, groupID + userID);
            editor.apply();

            // push group-user to firebase
            currentGroupUSerId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_USER_ID, "NA");
            currentGroupId = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID, "NA");
            GroupUser groupUser = new GroupUser(currentGroupUSerId, userID, currentGroupId, true, false, color, "NA", 0.0, true);
            groupUserRef.child(currentGroupUSerId).setValue(groupUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(mContext, "Tạo thành công !", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void joinGroup(final String mCode, final String urKey) {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot gr : dataSnapshot.getChildren()) {
                        Group group = gr.getValue(Group.class);
                        if (group.getGroupId().toString().toUpperCase().equals(mCode.toUpperCase())) {
                            UserRequest userRequest = new UserRequest(userID, mCode.toUpperCase());
                            userRequestRef.child(urKey).setValue(userRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext, "Vui lòng đợi chấp nhận !", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "Nhóm không tồn tại !", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                        }
                    }
                    userRequestRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


}
