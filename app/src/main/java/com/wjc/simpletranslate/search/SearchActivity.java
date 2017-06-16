package com.wjc.simpletranslate.search;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.NotebookAdapter;
import com.wjc.simpletranslate.adapter.NotebookMarkItemAdapter;
import com.wjc.simpletranslate.db.DBUtil;
import com.wjc.simpletranslate.db.NoteBookDBUtil;
import com.wjc.simpletranslate.db.NotebookDatabaseHelper;
import com.wjc.simpletranslate.interfaze.OnRecyclerViewOnClickListener;
import com.wjc.simpletranslate.model.NotebookMark;
import com.wjc.simpletranslate.model.NotebookMarkItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textView;
    private ProgressBar progressBar;
    private SearchView searchView;

    private NotebookAdapter adapter;

    private ArrayList<NotebookMarkItem> list = new ArrayList<NotebookMarkItem>();

    private List<NotebookMark> notebookMarkList=new ArrayList<>();

//    private NotebookDatabaseHelper dbHelper;
//    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        dbHelper = new NotebookDatabaseHelper(SearchActivity.this,"MyStore.db",null,1);
//        db = dbHelper.getReadableDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notebook);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        textView = (TextView) findViewById(R.id.text_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility(View.VISIBLE);

                if (list != null) {
                    list.clear();
                }

                notebookMarkList= DataSupport.where("input like ? or output like ?","%"+query+"%","%"+query+"%").find(NotebookMark.class);
                Log.e("NOTE",notebookMarkList.size()+"A");

//                Cursor cursor = db.query("notebook",null,null,null,null,null,null);
//                if (cursor.moveToFirst()){
//                    do {
//                        String in = cursor.getString(cursor.getColumnIndex("input"));
//                        String out = cursor.getString(cursor.getColumnIndex("output"));
//
//                        if (in.contains(query) || out.contains(query)){
//                            NotebookMarkItem item1 = new NotebookMarkItem(in,out);
//                            list.add(item1);
//                        }
//
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();

                if (notebookMarkList.isEmpty()){
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                    handleResults();
                }

                progressBar.setVisibility(View.GONE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void handleResults() {

        Collections.reverse(list);
        if (adapter == null) {
//            adapter = new NotebookAdapter(SearchActivity.this,list);
            adapter = new NotebookAdapter(SearchActivity.this,notebookMarkList);
            recyclerView.setAdapter(adapter);
            adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {

                @Override
                public void OnItemClick(View view, int position) {

                }

                @Override
                public void OnSubViewClick(View view, final int position) {

                    switch (view.getId()){

                        case R.id.image_view_share:

                            NotebookMark item1 = notebookMarkList.get(position);

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT,String.valueOf(item1.getInput() + "\n" + item1.getOutput()));
                            startActivity(Intent.createChooser(intent,getString(R.string.choose_app_to_share)));

                            break;

                        case R.id.image_view_copy:

                            NotebookMark item2 = notebookMarkList.get(position);

                            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("text", String.valueOf(item2.getInput() + "\n" + item2.getOutput()));
                            manager.setPrimaryClip(clipData);

                            Snackbar.make(recyclerView, R.string.copy_done, Snackbar.LENGTH_SHORT).show();

                            break;

                        case R.id.image_view_mark_star:

                            final NotebookMark item3 = notebookMarkList.get(position);

//                            DBUtil.deleteValue(dbHelper,item3.getInput());
//
//                            list.remove(position);
                            NoteBookDBUtil.deleteValue(item3.getInput());
                            notebookMarkList.remove(position);

                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position,notebookMarkList.size());

//                            Snackbar.make(recyclerView, R.string.add_to_notebook, Snackbar.LENGTH_LONG)
//                                    .setAction(R.string.undo, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
////                                            ContentValues values = new ContentValues();
////                                            values.put("input",item3.getInput());
////                                            values.put("output",item3.getOutput());
////
////                                            DBUtil.insertValue(dbHelper,values);
////
////                                            values.clear();
////
////                                            list.add(position,item3);
//                                            NoteBookDBUtil.insertValue(item3.getInput(),item3.getOutput());
//
//                                            adapter.notifyItemInserted(position);
//                                            recyclerView.smoothScrollToPosition(position);
//
//                                        }
//                                    }).show();

                            break;

                        default:
                            break;
                    }
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        if (id == R.id.action_search){
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(),0);
        }
    }

}
