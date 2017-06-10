package com.wjc.simpletranslate.translate;

import com.android.volley.RequestQueue;
import com.wjc.simpletranslate.BasePresenter;
import com.wjc.simpletranslate.BaseView;
import com.wjc.simpletranslate.db.NotebookDatabaseHelper;
import com.wjc.simpletranslate.model.BingModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/1.
 */
public class TranslateContract {

    interface View extends BaseView<Presenter> {

        void nullInput();

        void clearText();

        void isCollect();

        void noCollect();

        void showCopy();

        void TransResult(String result);

        void showResult();

        void hideResult();

        void hideProgress();

        void hidePanels();

        void showNoNetwork();

        void showTransError();

        void setAdapter(ArrayList<BingModel.Sample> samples);

        RequestQueue initQueue();

        NotebookDatabaseHelper initdbHelper();


    }

    interface Presenter extends BasePresenter {
        void checkNet();

        void readyTrans(int length,boolean isNull,String in);

//        void sendReq(String in);

        void sendReq1(String in);

        String inputFormat(String in);
    }
}
