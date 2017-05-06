package com.wjc.simpletranslate.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/5/6.
 */
public class DailyPic extends DataSupport{

    //每日一图的id
    private int id;

    //每日一图的日期
    private String date;

    //每日一图的地址
    private String PicUrl;

    public int getId() {
        return id;
    }


    public String getDate() {
        return date;
    }


    public String getPicUrl() {
        return PicUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}
