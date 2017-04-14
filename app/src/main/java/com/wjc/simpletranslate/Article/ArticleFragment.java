package com.wjc.simpletranslate.Article;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.ArticleAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment {

    private Context context;
    private ArticleAdapter adapter;

    private TabLayout tabLayout;

    private ElegantProseFragment proseFragment;
    private EnglishOralFragment oralFragment;
    private EnglishSongsFragment songsFragment;

    public ArticleFragment() {}

    //保留使用
    public  static  ArticleFragment newInstance(){return new ArticleFragment();}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context=getActivity();

        if(savedInstanceState!=null){
            FragmentManager manager=getChildFragmentManager();
            proseFragment=(ElegantProseFragment)manager.getFragment(savedInstanceState,"prose");
            oralFragment=(EnglishOralFragment)manager.getFragment(savedInstanceState,"oral");
            songsFragment=(EnglishSongsFragment)manager.getFragment(savedInstanceState,"songs");
        }else {
            proseFragment=ElegantProseFragment.newInstance();
            oralFragment=EnglishOralFragment.newInstance();
            songsFragment=EnglishSongsFragment.newInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        initViews(view);

        return view;
    }

    public void initViews(View view){
        tabLayout=(TabLayout)view.findViewById(R.id.tab_layout);

        ViewPager viewPager=(ViewPager)view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        adapter=new ArticleAdapter(
            getChildFragmentManager(),
                context,
                proseFragment,
                oralFragment,
                songsFragment);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

/*    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager manager=getChildFragmentManager();
        manager.putFragment(outState,"prose",proseFragment);
        manager.putFragment(outState,"oral",oralFragment);
        manager.putFragment(outState,"songs",songsFragment);
    }*/
}
