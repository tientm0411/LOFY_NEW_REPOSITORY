package lofy.fpt.edu.vn.lofy_ver108.dbo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
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
import lofy.fpt.edu.vn.lofy_ver108.entity.User;
import lofy.fpt.edu.vn.lofy_ver108.entity.UserRequest;

public class QueryFirebase {

    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<User> alUser;

    public QueryFirebase() {
        alUser = new ArrayList<>();
        registerUser();
    }

    public void registerUser() {
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
                    Log.d("alUser", alUser.size() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public ArrayList<User> getAlUser() {
        Log.d("alUser2", alUser.size() + "");
        // no return
        return alUser;
    }

}
