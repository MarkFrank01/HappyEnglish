package com.wjc.simpletranslate.dailyone.TestDaily;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.model.DailyOneItem;
import com.wjc.simpletranslate.util.DailyOneUtil.CardAdapterHelper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import jameson.io.library.util.ToastUtils;

/**
 * Created by jameson on 8/30/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Integer> mList = new ArrayList<>();
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private List<DailyOneItem> mDailyOneList;
    private Context context;

    public CardAdapter(List<Integer> mList,List<DailyOneItem> mDailyOneList,Context context) {
        this.mList = mList;
        this.mDailyOneList=mDailyOneList;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card_item, parent, false);

        mCardAdapterHelper.onCreateViewHolder(parent, itemView);

//        mDailyOneList= DataSupport.findAll(DailyOneItem.class);
        Log.e("INB",mDailyOneList.size()+"");
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());

        DailyOneItem dailyOneItem=mDailyOneList.get(position);

//        holder.mImageView.setImageResource(mList.get(position));
//        holder.mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToastUtils.show(holder.mImageView.getContext(), "" + position);
//            }
//        });
        Glide.with(context).load(dailyOneItem.getImgUrl()).into(holder.mImageView);

        holder.date.setText(dailyOneItem.getDateline());
        holder.text_view_eng.setText(dailyOneItem.getContent());
        holder.text_view_chi.setText(dailyOneItem.getNote());

    }

    @Override
    public int getItemCount() {
        return mDailyOneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public ImageView Today_daily;
        public TextView date;
        public TextView text_view_eng;
        public TextView text_view_chi;

        public ViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            date=(TextView)itemView.findViewById(R.id.date);
            text_view_eng=(TextView)itemView.findViewById(R.id.text_view_eng);
            text_view_chi=(TextView)itemView.findViewById(R.id.text_view_chi);
        }


    }

}
