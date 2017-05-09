package com.wjc.simpletranslate.dailyone;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.CardRvAdapter;
import com.wjc.simpletranslate.model.DailyOneItem;
import com.wjc.simpletranslate.util.CustomSnapHelper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/7.
 */
public class DailyoneFragment extends Fragment implements DailyOneContract.View{

    private RecyclerView dailyone;
    private List<DailyOneItem> mDailyOneList;
    private CardRvAdapter adapter;

    private RequestQueue queue;

    private DailyOneContract.Presenter presenter;

    public DailyoneFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue= Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dailyone,container,false);
        initViews(view);

        //数据暂未添加
        dailyone.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        presenter.requestData();
        //Adapter暂由Presenter处理

        mDailyOneList= DataSupport.findAll(DailyOneItem.class);
        dailyone.setAdapter(new CardRvAdapter(getContext(),mDailyOneList));

        CustomSnapHelper customSnapHelper=new CustomSnapHelper();
        customSnapHelper.attachToRecyclerView(dailyone);

        return view;
    }


    @Override
    public void setPresenter(DailyOneContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews(View view) {
        dailyone=(RecyclerView)view.findViewById(R.id.dailyone);
    }


//    @Override
//    public void showResult(ArrayList<DailyOneItem> list) {
//        if(adapter==null){
//            if (list==null){
//                Log.e("null","SB");
//            }
//            adapter=new CardRvAdapter(getContext(),list);
//        }else {
//            adapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public RequestQueue initQueue() {
        return queue;
    }

}
