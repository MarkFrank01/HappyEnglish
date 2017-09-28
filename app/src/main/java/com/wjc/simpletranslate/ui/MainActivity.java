package com.wjc.simpletranslate.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.BaseActivity;
import com.wjc.simpletranslate.MyApplication;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.dailyone.DailyOnePresenter;
import com.wjc.simpletranslate.dailyone.DailyoneFragment;
import com.wjc.simpletranslate.model.DailyPic;
import com.wjc.simpletranslate.search.SearchActivity;
import com.wjc.simpletranslate.translate.TranslateFragment;
import com.wjc.simpletranslate.translate.TranslatePresenter;
import com.wjc.simpletranslate.util.ActivityCollectorUtil;
import com.wjc.simpletranslate.util.HttpUtil;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageView header_day_pic;

    private TranslateFragment translateFragment;
    private DailyoneFragment dailyOneFragment;
    private NoteBookFragment noteBookFragment;

    private TranslatePresenter translatePresenter;
    private DailyOnePresenter dailyOnePresenter;

    private static final String ACTION_DAILY_ONE = "com.wjc.simpletranslate.dailyone";
    private static final String ACTION_NOTEBOOK = "com.wjc.simpletranslate.notebook";
    private static final String ACTION_TRANSLATE = "com.wjc.simpletranslate.translate";


    private SharedPreferences prefs;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        initViews();

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            translateFragment = (TranslateFragment) manager.getFragment(savedInstanceState, "translateFragment");
            dailyOneFragment = (DailyoneFragment) manager.getFragment(savedInstanceState, "dailyOneFragment");
            noteBookFragment = (NoteBookFragment) manager.getFragment(savedInstanceState, "noteBookFragment");

        } else {
            translateFragment = new TranslateFragment();
            dailyOneFragment = new DailyoneFragment();
            noteBookFragment = new NoteBookFragment();
        }

        translatePresenter = new TranslatePresenter(MyApplication.getContext(), translateFragment);
        dailyOnePresenter = new DailyOnePresenter(MyApplication.getContext(), dailyOneFragment);

        FragmentManager manager = getSupportFragmentManager();


        if (!translateFragment.isAdded()) {
            manager.beginTransaction()
                    .add(R.id.container_main, translateFragment, "translateFragment")
                    .commit();
        }

        if (!dailyOneFragment.isAdded()) {
            manager.beginTransaction()
                    .add(R.id.container_main, dailyOneFragment, "dailyOneFragment")
                    .commit();
        }

        if (!noteBookFragment.isAdded()) {
            manager.beginTransaction()
                    .add(R.id.container_main, noteBookFragment, "noteBookFragment")
                    .commit();
        }


        Intent intent = getIntent();

        if (ACTION_DAILY_ONE.equals(intent.getAction())) {
            showHideFragment(1);
        } else if (ACTION_NOTEBOOK.equals(intent.getAction())) {
            showHideFragment(2);
        } else {
            showHideFragment(0);
        }

    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.translate);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        header_day_pic = (ImageView) headerView.findViewById(R.id.header_day_pic);
        header_day_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPic();
                Toast.makeText(MainActivity.this, "每日一图~", Toast.LENGTH_SHORT).show();
            }
        });
        ReadyLoadPic();

    }

    private void ReadyLoadPic() {
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).placeholder(R.drawable.nav_header).into(header_day_pic);
            Log.e("bingPic", " " + bingPic);

//            DailyPic dailyPic=new DailyPic();
//            dailyPic.setPicUrl();
            List<DailyPic> Photos = DataSupport.findAll(DailyPic.class);
            int i = 0;
            for (DailyPic Photo : Photos) {
                i++;
                Log.e("dailyPic", i + " " + Photo.getPicUrl());
            }
        } else {
            loadingPic();
        }
    }

    private void loadingPic() {
        String requestPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bing_pic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bing_pic);
                editor.apply();


                List<DailyPic> isSavePic = DataSupport.where("PicUrl = ?", bing_pic).find(DailyPic.class);
                boolean Save = isSavePic.isEmpty();
                if (Save) {
                    //将响应地址存到数据库
                    DailyPic dailyPic = new DailyPic();
                    dailyPic.setPicUrl(bing_pic);
                    dailyPic.save();
                    //
                    List<DailyPic> Photos = DataSupport.findAll(DailyPic.class);
                    int i = 0;
                    for (DailyPic Photo : Photos) {
                        i++;
                        Log.e("dailyPic1", i + " " + Photo.getPicUrl());
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bing_pic).placeholder(R.drawable.nav_header).into(header_day_pic);
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_translate) {

            showHideFragment(0);

        } else if (id == R.id.nav_daily) {

            showHideFragment(1);

        } else if (id == R.id.nav_notebook) {

            showHideFragment(2);

        } else if (id == R.id.nav_change_dayornight) {

            // change the day/night mode after the drawer closed
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    SharedPreferences sp = getSharedPreferences("user_settings", MODE_PRIVATE);
                    if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                            == Configuration.UI_MODE_NIGHT_YES) {
                        sp.edit().putInt("theme", 0).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        sp.edit().putInt("theme", 1).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    recreate();
                    //回到选中第一个
                    showHideFragment(0);
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } else if (id == R.id.nav_setting) {

            startActivity(new Intent(MainActivity.this, SettingsPreferenceActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (translateFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "translateFragment", translateFragment);
        }

        if (dailyOneFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "dailyOneFragment", dailyOneFragment);
        }

        if (noteBookFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "noteBookFragment", noteBookFragment);
        }

    }

    /**
     * show or hide the fragment
     * and handle other operations like set toolbar's title
     * set the navigation's checked item
     *
     * @param position which fragment to show, only 3 values at this time
     *                 0 for translate fragment
     *                 1 for daily one fragment
     *                 2 for notebook fragment
     */
    private void showHideFragment(@IntRange(from = 0, to = 3) int position) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().hide(translateFragment).commit();
        manager.beginTransaction().hide(dailyOneFragment).commit();
        manager.beginTransaction().hide(noteBookFragment).commit();

        if (position == 0) {
            manager.beginTransaction().show(translateFragment).commit();
            toolbar.setTitle(R.string.translate);
            navigationView.setCheckedItem(R.id.nav_translate);
        } else if (position == 1) {
            toolbar.setTitle(R.string.daily_one);
            manager.beginTransaction().show(dailyOneFragment).commit();
            navigationView.setCheckedItem(R.id.nav_daily);
        } else if (position == 2) {
            toolbar.setTitle(R.string.notebook);
            manager.beginTransaction().show(noteBookFragment).commit();
            navigationView.setCheckedItem(R.id.nav_notebook);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                //判断2次点击事件的时间
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    ActivityCollectorUtil.finishAll();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
