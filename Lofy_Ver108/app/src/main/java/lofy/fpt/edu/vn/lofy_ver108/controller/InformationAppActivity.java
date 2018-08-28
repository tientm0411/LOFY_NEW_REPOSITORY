package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class InformationAppActivity extends BaseActivity {
    @Override
    int getContentViewId() {
        return R.layout.activity_information_app;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
}
