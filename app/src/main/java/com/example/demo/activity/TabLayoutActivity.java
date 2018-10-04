package com.example.demo.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.demo.R;
import com.example.demo.fragment.GetIdeaFragment;

public class TabLayoutActivity extends AppCompatActivity {

    private String currentLocation;
    private CharSequence[] charSequence = new CharSequence[]{"Hotel", "Hospital", "Restaurant"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        currentLocation = getIntent().getStringExtra("location");
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlView);
        vpPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), currentLocation, charSequence));
        tabLayout.setupWithViewPager(vpPager);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        String location;
        CharSequence[] charSequence;

        public MyPagerAdapter(FragmentManager fragmentManager, String location, CharSequence[] charSequence) {
            super(fragmentManager);
            this.location = location;
            this.charSequence = charSequence;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return GetIdeaFragment.newInstance("lodging", location);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return GetIdeaFragment.newInstance("health", location);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return GetIdeaFragment.newInstance("restaurant", location);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return charSequence[position];
        }

    }
}
