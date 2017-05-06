package com.wjc.simpletranslate.model;

import org.litepal.crud.DataSupport;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class DailyOneItem extends DataSupport{


    // 英文内容
    private String content = null;
    // 中文内容
    private String note = null;
    // 大图地址
    private String imgUrl = null;

    //日期
    private String dateline = null;

    // 每一句的id
    private int id;



    public void setContent(String content) {
        this.content = content;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getContent() {
        return content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getNote() {
        return note;
    }

    public int getId() {
        return id;
    }

    public String getDateline() {
        return dateline;
    }
}
