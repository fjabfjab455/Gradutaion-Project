package com.example.zeners.graduationproject;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zeners.graduationproject.Fragment.RadioFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    protected FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        fragments.add(new RadioFragment(context, "2") );
        fragments.add(new RadioFragment(context, "3") );
        fragments.add(new RadioFragment(context, "4") );
        fragments.add(new RadioFragment(context, "5") );
        fragments.add(new RadioFragment(context, "6") );
        fragments.add(new RadioFragment(context, "15") );
        fragments.add(new RadioFragment(context, "22") );
        fragments.add(new RadioFragment(context, "17") );
        fragments.add(new RadioFragment(context, "18") );
        fragments.add(new RadioFragment(context, "19") );
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_main);

        //系统兼容适应
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getWindow().setStatusBarColor(Color.parseColor("#303F9F"));

    }

    @Override
    protected void findViews() {
        super.findViews();
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tabLayout =  findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void initViews() {
        super.initViews();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //设置滚动模式，动态改变tabLayout的宽度
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);  // 绑定 viewPager

        //设置tab标题
        tabLayout.getTabAt(0).setText("莫萱日记");
        tabLayout.getTabAt(1).setText("爆笑糗事段子");
        tabLayout.getTabAt(2).setText("柜子开了");
        tabLayout.getTabAt(3).setText("酷我音乐调频");
        tabLayout.getTabAt(4).setText("一路向北");
        tabLayout.getTabAt(5).setText("请给我一首歌的时间");
        tabLayout.getTabAt(6).setText("小曹胡咧咧");
        tabLayout.getTabAt(7).setText("萱草私房歌");
        tabLayout.getTabAt(8).setText("每日正能量");
        tabLayout.getTabAt(9).setText("历史那点事");

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        List<android.support.v4.app.Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<android.support.v4.app.Fragment> fragmentList) {
            super(fm);
            this.fragments = fragmentList;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


}
