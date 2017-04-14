package com.wjc.simpletranslate.Article;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjc.simpletranslate.R;

/**
 * Created by Administrator on 2017/4/6.
 */
public class EnglishSongsFragment extends Fragment {
    public EnglishSongsFragment(){};

    public static EnglishSongsFragment newInstance(){return new EnglishSongsFragment();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        return view;
    }
}
