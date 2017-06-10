package com.wjc.simpletranslate.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/10.
 */
public class NotebookMark extends DataSupport{

    // 原文
    private String input = null;

    // 译文
    private String output = null;


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
