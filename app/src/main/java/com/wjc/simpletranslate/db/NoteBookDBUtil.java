package com.wjc.simpletranslate.db;

import android.util.Log;

import com.wjc.simpletranslate.model.NotebookMark;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2017/6/10.
 */
public class NoteBookDBUtil {

    public static boolean queryIfItemExist(String queryString){
        List<NotebookMark> notebookMark= DataSupport.where("input = ?",queryString).find(NotebookMark.class);
        Log.e("queryString",queryString);
        if(notebookMark.size()>0){
            Log.e("size",notebookMark.size()+"");
            Log.e("content",notebookMark.get(0).getInput()+"\n"+notebookMark.get(0).getOutput());
            return true;
        }
        return false;
    }

    public static void insertValue(String input,String output){
        NotebookMark notebookMark=new NotebookMark();
        notebookMark.setInput(input);
        String output1=output.substring(input.length()+1,output.length());
        notebookMark.setOutput(output1);
        notebookMark.save();
        Log.e("Add","A");
    }

    public static void deleteValue(String input){
        DataSupport.deleteAll(NotebookMark.class,"input = ?",input);
        Log.e("Del","D");
    }

}
