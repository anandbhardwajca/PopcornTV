package com.androidsalad.popcorntvapp.Activity.Welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.androidsalad.popcorntvapp.Activity.NewCeleb.NewCelebActivity;
import com.androidsalad.popcorntvapp.Activity.NewPost.AddPostImageActivity;
import com.androidsalad.popcorntvapp.Activity.NewPost.NewPostActivity;
import com.androidsalad.popcorntvapp.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    //views
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //initializeToolbarAndFAB:
        initializeToolbarAndFAB();

    }

    private void initializeToolbarAndFAB() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addCelebFab = (FloatingActionButton) findViewById(R.id.addCelebFAB);
        addCelebFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, NewCelebActivity.class));
                finish();
            }
        });

        FloatingActionButton addPostFAB = (FloatingActionButton) findViewById(R.id.addPostFAB);
        addPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, NewPostActivity.class));
                finish();
            }
        });

        FloatingActionButton addImageFAB = (FloatingActionButton) findViewById(R.id.addImageFAB);
        addImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, AddPostImageActivity.class));
                finish();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.welcomeActivityViewPager);
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentFragment(), "Recent");
        adapter.addFragment(new PopularFragment(), "Popular");
        viewPager.setAdapter(adapter);
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
