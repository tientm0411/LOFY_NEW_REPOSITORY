package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class StartActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String ciCode;
    private SharedPreferences mSharedPreferences;

    ActionBar actionBar;

    private int[] tabIcons = {
            R.drawable.ic_filter_1_white_24dp,
            R.drawable.ic_filter_2_white_24dp,
            R.drawable.ic_filter_3_white_24dp
    };

    private ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mSharedPreferences = getSharedPreferences(IntroApplicationActivity.FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Bundle bundle = getIntent().getExtras();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        String getFrom = bundle.getString("from");
        if (getFrom != null) {
            if (getFrom.equals("start")) {
                String getUserId = bundle.getString("USERID");
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment(getUserId);
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ln_start, profileFragment, ProfileFragment.class.getName())
                        //  .addToBackStack(HomeFragment.class.getName())
                        .commit();
            }
        }else{
            ciCode = bundle.getString("groupId");
            actionBar = getSupportActionBar();
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        String uID = mSharedPreferences.getString(IntroApplicationActivity.USER_ID, "NA");
        String isHost = mSharedPreferences.getString(IntroApplicationActivity.IS_HOST, "NA");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        MapGroupFragment mapGroupFragment = new MapGroupFragment();
//        mapGroupFragment.setCode(ciCode);

        adapter.addFragment(new MapGroupFragment(), "Bản đồ");
        if (isHost.equals("true")) {
            adapter.addFragment(new CreateGroupFragment("start"), "Nhóm");
        } else {
            adapter.addFragment(new JoinGroupFragment("start"), "Nhóm");
        }
        adapter.addFragment(new ProfileFragment(uID), "Trang cá nhân");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
