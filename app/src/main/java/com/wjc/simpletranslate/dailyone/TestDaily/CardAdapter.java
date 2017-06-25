package com.wjc.simpletranslate.dailyone.TestDaily;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.db.NoteBookDBUtil;
import com.wjc.simpletranslate.model.DailyOneItem;
import com.wjc.simpletranslate.model.NotebookMark;
import com.wjc.simpletranslate.util.DailyOneUtil.CardAdapterHelper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import jameson.io.library.util.ToastUtils;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by jameson on 8/30/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Integer> mList = new ArrayList<>();
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private List<DailyOneItem> mDailyOneList;
    private Context context;

    private boolean isMarked = false;

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

        final DailyOneItem dailyOneItem=mDailyOneList.get(position);

        if(NoteBookDBUtil.queryIfItemExist(dailyOneItem.getContent())){
            holder.image_view_mark_star.setImageResource(R.drawable.ic_grade_white_24dp);
            isMarked=true;
        }else {
            holder.image_view_mark_star.setImageResource(R.drawable.ic_star_border_white_24dp);
            isMarked=false;
        }

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

        holder.image_view_mark_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMarked){
                    holder.image_view_mark_star.setImageResource(R.drawable.ic_grade_white_24dp);
                    Snackbar.make(holder.image_view_mark_star, R.string.add_to_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = true;

                    NoteBookDBUtil.insertDailyValue(dailyOneItem.getContent(),dailyOneItem.getNote());

                }else {
                    holder.image_view_mark_star.setImageResource(R.drawable.ic_star_border_white_24dp);
                    Snackbar.make(holder.image_view_mark_star,R.string.remove_from_notebook,Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = false;

                    NoteBookDBUtil.deleteValue(dailyOneItem.getContent());

                }

            }
        });

        holder.image_view_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", String.valueOf(holder.text_view_eng.getText() + "\n" + holder.text_view_chi.getText()));
                manager.setPrimaryClip(clipData);

                Snackbar.make(holder.image_view_copy, R.string.copy_done, Snackbar.LENGTH_SHORT).show();
            }
        });

        holder.image_view_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(holder.text_view_eng.getText()) + "\n" + holder.text_view_chi.getText());
                context.startActivity(Intent.createChooser(intent,context.getString(R.string.choose_app_to_share)));
            }
        });

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
        public ImageView image_view_mark_star;
        public ImageView image_view_copy;
        public ImageView image_view_share;

        public ViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            date=(TextView)itemView.findViewById(R.id.date);
            text_view_eng=(TextView)itemView.findViewById(R.id.text_view_eng);
            text_view_chi=(TextView)itemView.findViewById(R.id.text_view_chi);
            image_view_mark_star=(ImageView)itemView.findViewById(R.id.image_view_mark_star);
            image_view_copy=(ImageView)itemView.findViewById(R.id.image_view_copy);
            image_view_share=(ImageView)itemView.findViewById(R.id.image_view_share);
        }


    }

}
