package com.wjc.simpletranslate.dailyone;

import com.android.volley.RequestQueue;
import com.wjc.simpletranslate.BasePresenter;
import com.wjc.simpletranslate.BaseView;
import com.wjc.simpletranslate.model.DailyOneItem;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/7.
 */
public class DailyOneContract {

    interface View extends BaseView<Presenter>{

        boolean checkData(String Content);

        void showData(String imgUrl,String dateline,String content,String note);

        RequestQueue initQueue();
    }

    interface Presenter extends BasePresenter{

        void requestData();

        void requestDataByDate(String URL);
    }
}
