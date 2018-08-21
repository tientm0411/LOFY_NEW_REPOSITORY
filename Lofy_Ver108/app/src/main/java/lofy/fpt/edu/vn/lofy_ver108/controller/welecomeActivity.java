package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class welecomeActivity extends AppCompatActivity {
    LinearLayout l1,l2;
    Animation uptodown,downtoup;
    ProgressBar progressBar;
    private static int SPLASH_TIME = 3000; //This is 4 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welecome);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        progressBar=(ProgressBar)findViewById(R.id.loadingPanel);
        progressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l1.setAnimation
                (uptodown);
        l2.setAnimation(downtoup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                Intent mySuperIntent = new Intent(welecomeActivity.this, IntroApplicationActivity.class);
                startActivity(mySuperIntent);
                /* This 'finish()' is for exiting the app when back button pressed
                 *  from Home page which is ActivityHome
                 */
                finish();
            }
        }, SPLASH_TIME);
    }
}
