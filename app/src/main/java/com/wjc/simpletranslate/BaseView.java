package com.wjc.simpletranslate;

import android.view.View;

/**
 * Created by wjc on 2017/4/11.
 */
public interface BaseView<T>{
    /**
     * set the presenter of mvp
     *  为View设置Presenter
     * @param presenter
     */
    void setPresenter(T presenter);

    /**
     * init the views of fragment
     * 初始化界面控件
     * @param view
     */
    void initViews(View view);
}
