package com.wjc.simpletranslate.translate;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wjc.simpletranslate.constant.Constants;
import com.wjc.simpletranslate.db.DBUtil;
import com.wjc.simpletranslate.db.NotebookDatabaseHelper;
import com.wjc.simpletranslate.model.BingModel;
import com.wjc.simpletranslate.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/1.
 */
public class TranslatePresenter implements TranslateContract.Presenter {

    private ArrayList<BingModel.Sample> samples;
    private NotebookDatabaseHelper dbHelper;
    private TranslateContract.View view;
    private Context context;

    private String result = null;
    private BingModel model;

    private RequestQueue queue;

    private boolean isMarked = false;
    private boolean completeWithEnter;
    private boolean showSamples;


    public TranslatePresenter(Context context, TranslateContract.View view){
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        this.queue=view.initQueue();
        this.dbHelper=view.initdbHelper();
    }

    @Override
    public void checkNet() {
        if(!NetworkUtil.isNetworkConnected(context)){
            view.showNoNetwork();
        }
    }

    @Override
    public void readyTrans(int length, boolean isNull, String in) {
        if (!NetworkUtil.isNetworkConnected(context)) {
            view.showNoNetwork();
        } else if (length==0||isNull==true) {
            view.nullInput();
        } else {
            sendReq(in);
        }
    }


    @Override
    public void sendReq(String in) {
        queue=view.initQueue();
        dbHelper=view.initdbHelper();
//        progressBar.setVisibility(View.VISIBLE);
//        viewResult.setVisibility(View.INVISIBLE);
        view.hideResult();

/*        // 监听输入面板的情况，如果激活则隐藏
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(button.getWindowToken(),0);
        }*/

        in = inputFormat(in);

        String url = Constants.BING_BASE + "?Word=" + in + "&Samples=";

        if (showSamples) {
            url += "true";
        } else {
            url += "false";
        }

        StringRequest request = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {

                            Gson gson = new Gson();
                            model = gson.fromJson(s, BingModel.class);

                            if (model != null) {

                                result = model.getWord() + "\n";

                                if (DBUtil.queryIfItemExist(dbHelper, model.getWord())) {
                                    view.isCollect();
                                    isMarked = true;
                                } else {
                                    view.noCollect();
                                    isMarked = false;
                                }

                                if (model.getPronunciation() != null) {
                                    BingModel.Pronunciation p = model.getPronunciation();
                                    result = result + "\nAmE:" + p.getAmE() + "\nBrE:" + p.getBrE() + "\n";
                                }

                                for (BingModel.Definition def : model.getDefs()) {
                                    result = result + def.getPos() + "\n" + def.getDef() + "\n";
                                }

                                result = result.substring(0, result.length() - 1);

                                if (model.getSams() != null && model.getSams().size() != 0) {

                                    if (samples == null) {
                                        samples = new ArrayList<>();
                                    }

                                    samples.clear();

                                    for (BingModel.Sample sample : model.getSams()) {
                                        samples.add(sample);
                                    }

//                                    if (adapter == null) {
//                                        adapter = new SampleAdapter(getActivity(), samples);
//                                        recyclerView.setAdapter(adapter);
//                                    } else {
//                                        adapter.notifyDataSetChanged();
//                                    }
                                    Log.e("samples",samples.size()+"");
                                    view.setAdapter(samples);
                                }

//                                progressBar.setVisibility(View.INVISIBLE);
//                                viewResult.setVisibility(View.VISIBLE);
//                                textViewResult.setText(result);
                                view.showResult();
                                view.TransResult(result);


                            }
                        } catch (JsonSyntaxException ex) {
                            view.showTransError();
                        }

                        view.hideProgress();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                view.hideProgress();
                view.showTransError();
            }
        });

        queue.add(request);
    }

    @Override
    public String inputFormat(String in) {
        in = in.replace("\n","");
        return in;
    }

    @Override
    public void start() {}
}
