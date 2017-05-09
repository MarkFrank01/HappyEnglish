package com.wjc.simpletranslate.adapter;

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

import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */

public class CardRvAdapter extends RecyclerView.Adapter<CardRvAdapter.ItemViewHolder> {
    private List<DailyOneItem> list;
    private Context context;

    public CardRvAdapter(Context context, List<DailyOneItem> list) {
        this.context = context;
        this.list = list;
        Log.e("S",this.list.size()+"");
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyone_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        DailyOneItem dailyOneItem=list.get(position);
//        Glide.with(context).load(list.get(position).getImgUrl()).into(holder.iv);
        Glide.with(context).load(dailyOneItem.getImgUrl()).into(holder.iv);
        holder.Eng.setText(dailyOneItem.getContent());
        holder.Chi.setText(dailyOneItem.getNote());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView Eng;
        private TextView Chi;


        public ItemViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.image_view_daily);
            Eng= (TextView) itemView.findViewById(R.id.text_view_eng);
            Chi= (TextView) itemView.findViewById(R.id.text_view_chi);
        }
    }
}
