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

//        void showResult(ArrayList<DailyOneItem> list);

        RequestQueue initQueue();
    }

    interface Presenter extends BasePresenter{

        void doCopy(String result);

        void doShare(String result);

        void requestData();

//        ArrayList<DailyOneItem> returnResult();
    }
}
