package com.wjc.simpletranslate.dailyone;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.CardRvAdapter;
import com.wjc.simpletranslate.constant.Constants;
import com.wjc.simpletranslate.dailyone.TestDaily.TestDailyActivity;
import com.wjc.simpletranslate.db.NoteBookDBUtil;
import com.wjc.simpletranslate.model.DailyOneItem;
import com.wjc.simpletranslate.util.CustomSnapHelper;
import com.wjc.simpletranslate.util.DailyOneUtil.BlurBitmapUtils;
import com.wjc.simpletranslate.util.DailyOneUtil.CardScaleHelper;
import com.wjc.simpletranslate.util.DailyOneUtil.ViewSwitchUtils;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Administrator on 2017/5/7.
 */
public class DailyoneFragment extends Fragment implements DailyOneContract.View{

    private RecyclerView dailyone;
    private List<DailyOneItem> mDailyOneList;
    private CardRvAdapter adapter;

    private ImageView Today_daily;
    private ImageView more_daily;
    private ImageView choose_daily;
    private ImageView image_view_mark_star;
    private ImageView image_view_copy;
    private ImageView image_view_share;
    private TextView date;
    private TextView text_view_eng;
    private TextView text_view_chi;

    private ImageView mBlurView;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private CardScaleHelper mCardScaleHelper = null;
    private List<Integer> mList = new ArrayList<>();

    private boolean isMarked = false;

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private RequestQueue queue;

    private DailyOneContract.Presenter presenter;

    public DailyoneFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getActivity().getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        queue= Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.fragment_dailyone,container,false);
        initViews(view);

        //数据暂未添加
//        dailyone.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        presenter.requestData();

        mDailyOneList= DataSupport.findAll(DailyOneItem.class);

        final DailyOneItem dailyOneItem=mDailyOneList.get(mDailyOneList.size()-1);
        Glide.with(getActivity()).load(dailyOneItem.getImgUrl()).into(Today_daily);
        date.setText(dailyOneItem.getDateline());
        text_view_eng.setText(dailyOneItem.getContent());
        text_view_chi.setText(dailyOneItem.getNote());

        if(NoteBookDBUtil.queryIfItemExist(dailyOneItem.getContent())){
            image_view_mark_star.setImageResource(R.drawable.ic_grade_white_24dp);
            isMarked=true;
        }else {
            image_view_mark_star.setImageResource(R.drawable.ic_star_border_white_24dp);
            isMarked=false;
        }

        more_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), TestDailyActivity.class);
                getActivity().startActivity(intent);
            }
        });

        choose_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                now.set(mYear, mMonth, mDay);
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        Calendar temp = Calendar.getInstance();
                        temp.clear();
                        temp.set(year, monthOfYear, dayOfMonth);
                        Toast.makeText(getContext(), year+""+monthOfYear+""+dayOfMonth+"", Toast.LENGTH_SHORT).show();

                        long date=temp.getTimeInMillis();
                        String sDate;
                        Date d = new Date(date);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        sDate = format.format(d);

                        Log.e("DATEURL",Constants.DAILY_FORDATE+sDate);
//                        Constants.DAILY_FORDATE
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

                dialog.setMaxDate(Calendar.getInstance());
                Calendar minDate = Calendar.getInstance();
                // 2013.5.20是知乎日报api首次上线
                minDate.set(2013, 5, 20);
                dialog.setMinDate(minDate);
                dialog.vibrate(false);

                dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });

        image_view_mark_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 在没有被收藏的情况下
                if (!isMarked){
                    image_view_mark_star.setImageResource(R.drawable.ic_grade_white_24dp);
                    Snackbar.make(image_view_mark_star, R.string.add_to_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = true;

//                    ContentValues values = new ContentValues();
//                    values.put("input",model.getWord());
//                    values.put("output",result);
//                    DBUtil.insertValue(dbHelper,values);

//                    NoteBookDBUtil.insertValue(dailyOneItem.getContent(),dailyOneItem.getNote());
                    NoteBookDBUtil.insertDailyValue(dailyOneItem.getContent(),dailyOneItem.getNote());
//                    values.clear();

                } else {
                    image_view_mark_star.setImageResource(R.drawable.ic_star_border_white_24dp);
                    Snackbar.make(image_view_mark_star,R.string.remove_from_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = false;
                    NoteBookDBUtil.deleteValue(dailyOneItem.getContent());
//                    DBUtil.deleteValue(dbHelper, model.getWord());
                }
            }
        });

        image_view_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", String.valueOf(text_view_eng.getText() + "\n" + text_view_chi.getText()));
                manager.setPrimaryClip(clipData);

                Snackbar.make(image_view_copy, R.string.copy_done, Snackbar.LENGTH_SHORT).show();
            }
        });

        image_view_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(text_view_eng.getText()) + "\n" + text_view_chi.getText());
                startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));
            }
        });


//        dailyone.setAdapter(new CardRvAdapter(getContext(),mDailyOneList));
//
//        CustomSnapHelper customSnapHelper=new CustomSnapHelper();
//        customSnapHelper.attachToRecyclerView(dailyone);

//        for (int i = 0; i < 10; i++) {
//            mList.add(R.mipmap.pic4);
//            mList.add(R.mipmap.pic5);
//            mList.add(R.mipmap.pic6);
//        }
//
//        dailyone=(RecyclerView)view.findViewById(R.id.dailyone);
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        dailyone.setLayoutManager(linearLayoutManager);
//        dailyone.setAdapter(new CardRvAdapter(getContext(),mDailyOneList));
//        // mRecyclerView绑定scale效果
//        mCardScaleHelper = new CardScaleHelper();
//        mCardScaleHelper.setCurrentItemPos(2);
//        mCardScaleHelper.attachToRecyclerView(dailyone);


//        initBlurBackground();


        return view;
    }


    @Override
    public void setPresenter(DailyOneContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews(View view) {
        date=(TextView)view.findViewById(R.id.date);
        Today_daily=(ImageView)view.findViewById(R.id.Today_daily);
        text_view_eng=(TextView)view.findViewById(R.id.text_view_eng);
        text_view_chi=(TextView)view.findViewById(R.id.text_view_chi);
        more_daily=(ImageView)view.findViewById(R.id.more_daily);
        choose_daily=(ImageView)view.findViewById(R.id.choose_daily);


        image_view_mark_star=(ImageView)view.findViewById(R.id.image_view_mark_star);
        image_view_copy=(ImageView)view.findViewById(R.id.image_view_copy);
        image_view_share=(ImageView)view.findViewById(R.id.image_view_share);

//        dailyone=(RecyclerView)view.findViewById(R.id.dailyone);
//        mBlurView = (ImageView)view.findViewById(R.id.blurView);

    }


    @Override
    public RequestQueue initQueue() {
        return queue;
    }


    private void initBlurBackground() {
        dailyone.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });

        notifyBackgroundChange();
    }

    private void notifyBackgroundChange() {

        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        final int resId = mList.get(mCardScaleHelper.getCurrentItemPos());
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
            }
        };
        mBlurView.postDelayed(mBlurRunnable, 500);
    }
}
