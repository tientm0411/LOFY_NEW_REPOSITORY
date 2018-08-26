package lofy.fpt.edu.vn.lofy_ver108.dbo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.controller.IntroApplicationActivity;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;
import lofy.fpt.edu.vn.lofy_ver108.entity.GroupUser;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

public class QueryFirebase {

    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<User> alUser;
    private ArrayList<Group> alGroup;
    private String groupName;

    public QueryFirebase() {
        alUser = new ArrayList<>();
        alGroup = new ArrayList<>();
        registerUser();
        registerGroup();
    }

    public void registerGroup() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alGroup.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Group u = ds.getValue(Group.class);
                        alGroup.add(u);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String getGroupNameById(ArrayList<Group> al,String gID) {
        String name = "";
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).getGroupId().equals(gID)) {
                name = al.get(i).getGroupName();
                return name;
            }
        }
        return "Group sapa";
    }

    public void registerUser() {
        Log.d("alUser2", "ping ! ");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    alUser.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User u = ds.getValue(User.class);
                        alUser.add(u);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public  ArrayList<User> getAlUser() {
        return alUser;
    }
    public  ArrayList<Group> getAlGroup() {
        return alGroup;
    }

}

