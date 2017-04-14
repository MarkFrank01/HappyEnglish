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
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.Article.ArticleFragment;
import com.wjc.simpletranslate.BaseActivity;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.search.SearchActivity;
import com.wjc.simpletranslate.util.ActivityCollectorUtil;
import com.wjc.simpletranslate.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageView header_day_pic;

    private TranslateFragment translateFragment;
    private ArticleFragment articleFragment;
    private DailyOneFragment dailyOneFragment;
    private NoteBookFragment noteBookFragment;


    private static final String ACTION_ARTICLE = "com.wjc.simpletranslate.article";
    private static final String ACTION_DAILY_ONE = "com.wjc.simpletranslate.dailyone";
    private static final String ACTION_NOTEBOOK = "com.wjc.simpletranslate.notebook";
    private static final String ACTION_TRANSLATE= "com.wjc.simpletranslate.translate";


    private SharedPreferences prefs;
    private long exitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        prefs= PreferenceManager.getDefaultSharedPreferences(this);

        initViews();

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            articleFragment = (ArticleFragment) manager.getFragment(savedInstanceState, "articleFragment");
            translateFragment = (TranslateFragment) manager.getFragment(savedInstanceState, "translateFragment");
            dailyOneFragment = (DailyOneFragment) manager.getFragment(savedInstanceState, "dailyOneFragment");
            noteBookFragment = (NoteBookFragment) manager.getFragment(savedInstanceState, "noteBookFragment");

        } else {
            articleFragment = new ArticleFragment();
            translateFragment = new TranslateFragment();
            dailyOneFragment = new DailyOneFragment();
            noteBookFragment = new NoteBookFragment();
        }
        FragmentManager manager = getSupportFragmentManager();

        if (!articleFragment.isAdded()) {
            manager.beginTransaction()
                    .add(R.id.container_main, articleFragment, "articleFragment")
                    .commit();
        }

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

        if (ACTION_TRANSLATE.equals(intent.getAction())) {
            showHideFragment(1);
        } else if (ACTION_DAILY_ONE.equals(intent.getAction())) {
            showHideFragment(2);
        } else if (ACTION_NOTEBOOK.equals(intent.getAction())) {
            showHideFragment(3);
        } else {
            showHideFragment(0);
        }

    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.article);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        header_day_pic=(ImageView)headerView.findViewById(R.id.header_day_pic);
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
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).placeholder(R.drawable.nav_header).into(header_day_pic);
        }else {
            loadingPic();
        }
    }

    private void loadingPic() {
        String requestPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bing_pic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic",bing_pic);
                editor.apply();
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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

      if (id == R.id.nav_article) {

            showHideFragment(0);

        } if (id == R.id.nav_translate) {

            showHideFragment(1);

        } else if (id == R.id.nav_daily) {

            showHideFragment(2);

        } else if (id == R.id.nav_notebook) {

            showHideFragment(3);

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
        if (articleFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "articleFragment", articleFragment);
        }

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
     * @param position which fragment to show, only 4 values at this time
     *                 0 for article  fragment
     *                 1 for translate fragment
     *                 2 for daily one fragment
     *                 3 for notebook fragment
     */
    private void showHideFragment(@IntRange(from = 0, to = 3) int position) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().hide(translateFragment).commit();
        manager.beginTransaction().hide(articleFragment).commit();
        manager.beginTransaction().hide(dailyOneFragment).commit();
        manager.beginTransaction().hide(noteBookFragment).commit();

        if (position == 0) {
            toolbar.setTitle(R.string.article);
            manager.beginTransaction().show(articleFragment).commit();
            navigationView.setCheckedItem(R.id.nav_article);
        } else if (position == 1) {
            manager.beginTransaction().show(translateFragment).commit();
            toolbar.setTitle(R.string.translate);
            navigationView.setCheckedItem(R.id.nav_translate);
        }else if (position == 2) {
            toolbar.setTitle(R.string.daily_one);
            manager.beginTransaction().show(dailyOneFragment).commit();
            navigationView.setCheckedItem(R.id.nav_daily);
        } else if (position == 3) {
            toolbar.setTitle(R.string.notebook);
            manager.beginTransaction().show(noteBookFragment).commit();
            navigationView.setCheckedItem(R.id.nav_notebook);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getRepeatCount()==0){
                exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitApp(){
        //判断2次点击事件的时间
        if((System.currentTimeMillis()-exitTime)>2000){
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        }else {
            ActivityCollectorUtil.finishAll();
        }
    }
}
