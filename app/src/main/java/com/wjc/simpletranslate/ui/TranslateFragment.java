package com.wjc.simpletranslate.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.SampleAdapter;
import com.wjc.simpletranslate.constant.Constants;
import com.wjc.simpletranslate.db.DBUtil;
import com.wjc.simpletranslate.db.NotebookDatabaseHelper;
import com.wjc.simpletranslate.model.BingModel;
import com.wjc.simpletranslate.util.NetworkUtil;


import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class TranslateFragment extends Fragment {

    private EditText editText;
    private TextView textViewClear;
    private ProgressBar progressBar;
    private TextView textViewResult;
    private ImageView imageViewMark;
    private View viewResult;
    private AppCompatButton button;

    private ArrayList<BingModel.Sample> samples;
    private RecyclerView recyclerView;
    private SampleAdapter adapter;

    private NotebookDatabaseHelper dbHelper;

    private String result = null;
    private BingModel model;

    private RequestQueue queue;

    private boolean isMarked = false;
    private boolean completeWithEnter;
    private boolean showSamples;

    // empty constructor required
    public TranslateFragment(){

    }

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        dbHelper = new NotebookDatabaseHelper(getActivity(),"MyStore.db",null,1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        initViews(view);

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if (!NetworkUtil.isNetworkConnected(getActivity())){
            showNoNetwork();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    showNoNetwork();
                } else if (editText.getText() == null || editText.getText().length() == 0) {
                    Snackbar.make(button, getString(R.string.no_input), Snackbar.LENGTH_SHORT)
                            .show();
                } else {

                    sendReq(editText.getText().toString());

                }
            }
        });

        textViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText.getEditableText().toString().length() != 0){
                    textViewClear.setVisibility(View.VISIBLE);
                } else {
                    textViewClear.setVisibility(View.INVISIBLE);
                }

                // handle the situation when text ends up with enter
                // 监听回车事件
                if (completeWithEnter) {
                    if (count == 1 && s.charAt(start) == '\n') {
                        editText.getText().replace(start, start + 1, "");
                        sendReq(editText.getEditableText().toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageViewMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 在没有被收藏的情况下
                if (!isMarked){
                    imageViewMark.setImageResource(R.drawable.ic_grade_white_24dp);
                    Snackbar.make(button, R.string.add_to_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = true;

                    ContentValues values = new ContentValues();
                    values.put("input",model.getWord());
                    values.put("output",result);
                    DBUtil.insertValue(dbHelper,values);

                    values.clear();

                } else {
                    imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);
                    Snackbar.make(button,R.string.remove_from_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = false;

                    DBUtil.deleteValue(dbHelper, model.getWord());
                }

            }
        });

        viewResult.findViewById(R.id.image_view_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,result);
                startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));
            }
        });

        viewResult.findViewById(R.id.image_view_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", result);
                manager.setPrimaryClip(clipData);

                Snackbar.make(button,R.string.copy_done,Snackbar.LENGTH_SHORT)
                        .show();
            }
        });


        return view;
    }

    private void initViews(View view) {

        editText = (EditText) view.findViewById(R.id.et_main_input);
        textViewClear = (TextView) view.findViewById(R.id.tv_clear);
        // 初始化清除按钮，当没有输入时是不可见的
        textViewClear.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        viewResult = view.findViewById(R.id.include);
        textViewResult = (TextView) view.findViewById(R.id.text_view_output);
        imageViewMark = (ImageView) view.findViewById(R.id.image_view_mark_star);
        imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);

        button = (AppCompatButton) view.findViewById(R.id.buttonTranslate);
    }

    private void sendReq(String in){

        progressBar.setVisibility(View.VISIBLE);
        viewResult.setVisibility(View.INVISIBLE);

        // 监听输入面板的情况，如果激活则隐藏
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(button.getWindowToken(),0);
        }

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
                                    imageViewMark.setImageResource(R.drawable.ic_grade_white_24dp);
                                    isMarked = true;
                                } else {
                                    imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);
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

                                    if (adapter == null) {
                                        adapter = new SampleAdapter(getActivity(), samples);
                                        recyclerView.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                viewResult.setVisibility(View.VISIBLE);

                                textViewResult.setText(result);
                            }
                        } catch (JsonSyntaxException ex) {
                            showTransError();
                        }

                        progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                showTransError();
            }
        });

        queue.add(request);

    }

    //去掉输入文本中的回车符号
    private String inputFormat(String in){
        in = in.replace("\n","");
        return in;
    }

    private void showNoNetwork(){
        Snackbar.make(button, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    private void showTransError() {
        Snackbar.make(button, R.string.trans_error, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        completeWithEnter = sp.getBoolean("enter_key", false);
        showSamples = sp.getBoolean("samples", true);
        if (samples != null) {
            samples.clear();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
