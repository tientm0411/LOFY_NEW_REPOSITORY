package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class IntroApplicationActivity extends AppIntro implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String FILE_NAME = "Inital-Data-App-lofy-ver108.1";
    public static final String IS_FIST = "is fist";
    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String USER_PHONE = "userPhone";
    public static final String USER_URL_AVATAR = "userAvatar";
    public static final String GROUP_ID = "groupID";
    public static final String GROUP_NAME = "groupName";
    public static final String IS_HOST = "isHost";
    public static final String GROUP_USER_ID = "groupUser_ID";
    public static final String GROUP_USER_COLOR = "groupUser_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        chekFirstTime();
    }

    private void chekFirstTime() {
        if(AccessToken.getCurrentAccessToken()!=null){
            Intent intent = new Intent(IntroApplicationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else if (mSharedPreferences.getBoolean(IS_FIST, true)) {
            editor.putBoolean(IS_FIST, false);
            editor.apply();
            showIntro();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        mSharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
    }
    private void showIntro() {
        addSlide(AppIntroFragment.newInstance("First App Into", "First App Intro Details",
                R.drawable.img_intro_background, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Second App Into", "Second App Intro Details",
                R.drawable.img_intro_background2, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance("Third App Into", "Third App Intro Details",
                R.drawable.img_intro_background3, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
    @Override
    protected void onDestroy() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
