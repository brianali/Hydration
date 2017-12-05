package itp341.liang.briana.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import itp341.liang.briana.finalproject.model.managers.ActivityManager;
import itp341.liang.briana.finalproject.model.managers.StorageManager;
import itp341.liang.briana.finalproject.model.managers.UserManager;
import itp341.liang.briana.finalproject.model.objects.UserInfo;

public class MainActivity extends AppCompatActivity {
    private MyPagerAdapter viewPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private FirebaseAuth auth;
    private String username;
    // Tab titles
    private static String[] tabs = { "Daily Fluids", "Daily Activities"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StorageManager.getDefaultManagerWithContext(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        username = auth.getCurrentUser().getEmail();
        Intent data = getIntent();
        if (data!=null){
            UserInfo user = (UserInfo) data.getSerializableExtra(SetUpProfileActivity.WEIGHT);
            UserManager.getDefaultManager().setUserInfo(user);
            ArrayList<UserInfo> all = UserManager.getDefaultManager().getAllUserInfos();
            int x= 5;
//            String weight = data.getStringExtra(SetUpProfileActivity.WEIGHT);
//            String waterGoal = data.getStringExtra(SetUpProfileActivity.WATER_GOAL);
        }
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void onDestroy() {
        super.onDestroy();

        System.out.println("MainActivity was destroyed...");
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_TABS = 2;
        private FragmentManager fm;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fm = fragmentManager;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_TABS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new OverviewFragment();
                case 1:
                    return new ActiveActivityFragment();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return tabs[0];
                case 1:
                    return tabs[1];
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int resId = item.getItemId();
        if (resId == R.id.menu_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
