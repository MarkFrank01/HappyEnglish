package com.wjc.simpletranslate.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.wjc.simpletranslate.Article.ElegantProseFragment;
import com.wjc.simpletranslate.Article.EnglishOralFragment;
import com.wjc.simpletranslate.Article.EnglishSongsFragment;
import com.wjc.simpletranslate.R;

/**
 * Created by Administrator on 2017/4/6.
 */
public class ArticleAdapter extends FragmentPagerAdapter{
    private String[] titles;
    private final Context context;

    private ElegantProseFragment proseFragment;
    private EnglishOralFragment oralFragment;
    private EnglishSongsFragment songsFragment;

    public ElegantProseFragment getProseFragment() {
        return proseFragment;
    }

    public EnglishOralFragment getOralFragment() {
        return oralFragment;
    }

    public EnglishSongsFragment getSongsFragment() {
        return songsFragment;
    }

    public ArticleAdapter(FragmentManager fm,
                          Context context,
                          ElegantProseFragment proseFragment,
                          EnglishOralFragment oralFragment,
                          EnglishSongsFragment songsFragment) {
        super(fm);
        this.context = context;

        titles=new String[]{
                 context.getResources().getString(R.string.elegant_prose),
                 context.getResources().getString(R.string.english_oral),
                 context.getResources().getString(R.string.english_songs)
        };
        this.proseFragment=proseFragment;
        this.oralFragment=oralFragment;
        this.songsFragment=songsFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return oralFragment;
        }else if(position==2){
            return songsFragment;
        }

        return proseFragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
