package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.Notification;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initview();
        showFragmentEnterName();
    }


    private void initview() {
    }

    private void showFragmentEnterName() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ln_main, homeFragment, HomeFragment.class.getName())
                //  .addToBackStack(HomeFragment.class.getName())
                .commit();
        //getFragmentManager().popBackStackImmediate();
    }

    private boolean twice = false;

//    @Override
//    public void onBackPressed() {
////        if (twice == true) {
////            Intent startMain = new Intent(Intent.ACTION_MAIN);
////            startMain.addCategory(Intent.CATEGORY_HOME);
////            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(startMain);
//////            finish();
//////            System.exit(0);
////        }
////        twice = true;
////        Toast.makeText(this, "Nhấn trở lại một lần nữa để thoát !", Toast.LENGTH_SHORT).show();
////        new Handler().postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                twice = false;
////            }
////        }, 3000);
////
//
//    }

}
