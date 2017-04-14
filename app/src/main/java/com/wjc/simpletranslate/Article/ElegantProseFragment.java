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
public class ElegantProseFragment extends Fragment {

    public ElegantProseFragment(){};

    public static ElegantProseFragment newInstance(){return new ElegantProseFragment();}

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
