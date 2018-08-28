package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class GuidesActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("First App Into", "First App Intro Details",
                R.drawable.img_intro_background, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Second App Into", "Second App Intro Details",
                R.drawable.img_intro_background2, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance("Third App Into", "Third App Intro Details",
                R.drawable.img_intro_background2, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),InformationAppActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),InformationAppActivity.class);
        startActivity(intent);
        finish();
    }
}
