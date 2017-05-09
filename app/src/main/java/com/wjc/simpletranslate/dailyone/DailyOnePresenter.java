package com.wjc.simpletranslate.dailyone;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.constant.Constants;
import com.wjc.simpletranslate.db.DBUtil;
import com.wjc.simpletranslate.model.DailyOneItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Administrator on 2017/5/8.
 */
public class DailyOnePresenter implements DailyOneContract.Presenter{

    private Context context;
    private DailyOneContract.View view;

    private RequestQueue queue;

    ArrayList<DailyOneItem> result;

    private String imageUrl = null;
    private String content = null;
    private String note = null;
    private String date = null;

    public DailyOnePresenter(Context context, DailyOneContract.View view){
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
    }

    @Override
    public void doCopy(String result) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", result);
        manager.setPrimaryClip(clipData);

//        Snackbar.make(ivCopy, R.string.copy_done, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void doShare(String result) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND).setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, result);
        context.startActivity(Intent.createChooser(intent,context.getString(R.string.choose_app_to_share)));
    }

    @Override
    public void requestData() {
        queue=view.initQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.DAILY_SENTENCE, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {


                    imageUrl = jsonObject.getString("picture2");
                    content = jsonObject.getString("content");
                    note = jsonObject.getString("note");
                    date = jsonObject.getString("dateline");

                    Log.e("imageUrl",imageUrl);
                    Log.e("content",content);

                    List<DailyOneItem> isSaveDaily=DataSupport.where("imgUrl = ?",imageUrl).find(DailyOneItem.class);
                    boolean Save=isSaveDaily.isEmpty();
                    if(Save) {
                        DailyOneItem dailyOneItem = new DailyOneItem();
                        dailyOneItem.setImgUrl(imageUrl);
                        dailyOneItem.setContent(content);
                        dailyOneItem.setNote(note);
                        dailyOneItem.setDateline(date);
                        dailyOneItem.save();
                    }
//                    List<DailyOneItem> list=DataSupport.findAll(DailyOneItem.class);
//                    int i=0;
//                    for(DailyOneItem dailyOneItem1:list){
//                        i++;
//                        Log.e("D",i+" : "+dailyOneItem1.getImgUrl());
//                    }

                    ArrayList<DailyOneItem> items= (ArrayList<DailyOneItem>) DataSupport.findAll(DailyOneItem.class);
                     int i=0;
                    for(DailyOneItem dailyOneItem1:items){
                        i++;
                        Log.e("DDDDD",i+" : "+dailyOneItem1.getImgUrl());
                    }

                    result=items;

//                    Glide.with(getActivity())
//                            .load(imageUrl)
//                            .asBitmap()
//                            .centerCrop()
//                            .into(imageViewMain);
//
////                    textViewEng.setText(jsonObject.getString("content"));
////                    textViewChi.setText(jsonObject.getString("note"));
//                    textViewEng.setText(content);
//                    textViewChi.setText(note);
//
//                    if (DBUtil.queryIfItemExist(dbHelper,textViewEng.getText().toString())){
//                        ivStar.setImageResource(R.drawable.ic_grade_white_24dp);
//                        isMarked = true;
//                    } else {
//                        ivStar.setImageResource(R.drawable.ic_star_border_white_24dp);
//                        isMarked = false;
//                    }
//
//                    dailyOneItem.setImgUrl(imageUrl);
//                    dailyOneItem.setContent(content);
//                    dailyOneItem.setNote(note);
//                    Log.e("dateline",jsonObject.getString("dateline"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        queue.add(request);
    }

    @Override
    public void start() {}
}
