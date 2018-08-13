package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import lofy.fpt.edu.vn.lofy_ver108.R;

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

}
