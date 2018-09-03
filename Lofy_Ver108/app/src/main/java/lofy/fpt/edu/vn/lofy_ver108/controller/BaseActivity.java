package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import lofy.fpt.edu.vn.lofy_ver108.R;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       try {
           navigationView.postDelayed(() -> {
               int itemId = item.getItemId();
               if (itemId == R.id.navigation_home) {
                   startActivity(new Intent(this, InformationAppActivity.class));
               } else if (itemId == R.id.navigation_dashboard) {
                   startActivity(new Intent(this, GuidesActivity.class));
               }
               finish();
           }, 300);
           return true;
       }catch (Exception e){
           return false;
       }
    }

    private void updateNavigationBarState() {
        try{
            int actionId = getNavigationMenuItemId();
            selectBottomNavigationBarItem(actionId);
        }catch (Exception e){

        }
    }

    void selectBottomNavigationBarItem(int itemId) {
       try{
           MenuItem item = navigationView.getMenu().findItem(itemId);
           item.setChecked(true);
       }catch (Exception e){

       }
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

}
