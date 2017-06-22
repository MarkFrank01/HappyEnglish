package com.wjc.simpletranslate.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wjc.simpletranslate.R;
import com.wjc.simpletranslate.adapter.NotebookAdapter;
import com.wjc.simpletranslate.db.NoteBookDBUtil;
import com.wjc.simpletranslate.db.NotebookDatabaseHelper;
import com.wjc.simpletranslate.interfaze.OnRecyclerViewOnClickListener;
import com.wjc.simpletranslate.model.NotebookMark;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by lizhaotailang on 2016/7/12.
 */

public class NoteBookFragment extends Fragment {

    private RecyclerView recyclerViewNotebook;
    private FloatingActionButton fab;

//    private ArrayList<NotebookMarkItem> list = new ArrayList<NotebookMarkItem>();

    private List<NotebookMark> notebookMarkList = new ArrayList<>();

    private NotebookAdapter adapter;
    private TextView tvNoNote;

    private NotebookDatabaseHelper dbHelper;

    public NoteBookFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new NotebookDatabaseHelper(getActivity(), "MyStore.db", null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notebook, container, false);

        initViews(view);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setTitle(R.string.add_to_notebook);
                LayoutInflater li = getActivity().getLayoutInflater();
                final View v = li.inflate(R.layout.add_note, null);
                dialog.setView(v);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        TextInputEditText etInput = (TextInputEditText) v.findViewById(R.id.et_input);
                        TextInputEditText etOutput = (TextInputEditText) v.findViewById(R.id.et_output);

                        String in = etInput.getText().toString();
                        String out = etOutput.getText().toString();

                        if (in.isEmpty() || out.isEmpty()) {

                            Snackbar.make(fab, R.string.no_input, Snackbar.LENGTH_SHORT).show();

                        } else {

                            if (tvNoNote.getVisibility() == View.VISIBLE) {
                                tvNoNote.setVisibility(View.GONE);
                            }

                            NotebookMark notebookMark = new NotebookMark();
                            notebookMark.setInput(in);
                            notebookMark.setOutput(out);
                            notebookMark.save();

                            notebookMark = DataSupport.findLast(NotebookMark.class);
                            notebookMarkList.add(0, notebookMark);
                            adapter.notifyItemInserted(0);
                            recyclerViewNotebook.smoothScrollToPosition(0);
                        }


                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
            }
        });

        loadData();

        isData();
        handleResults();

        return view;
    }

    private void isData() {
        if (notebookMarkList.isEmpty()) {
            tvNoNote.setVisibility(View.VISIBLE);
        } else {
            tvNoNote.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        notebookMarkList = DataSupport.findAll(NotebookMark.class);
        Log.e("L1", notebookMarkList.size() + "p");
    }


    private void handleResults() {


        adapter = new NotebookAdapter(getActivity(), notebookMarkList);
        recyclerViewNotebook.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {


            @Override
            public void OnItemClick(View view, int position) {

            }

            @Override
            public void OnSubViewClick(View view, final int position) {

                switch (view.getId()) {

                    case R.id.image_view_share:

                        NotebookMark item1 = notebookMarkList.get(position);

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(item1.getInput() + "\n" + item1.getOutput()));
                        startActivity(Intent.createChooser(intent, getString(R.string.choose_app_to_share)));

                        break;

                    case R.id.image_view_copy:

                        NotebookMark item2 = notebookMarkList.get(position);

                        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", String.valueOf(item2.getInput() + "\n" + item2.getOutput()));
                        manager.setPrimaryClip(clipData);

                        Snackbar.make(recyclerViewNotebook, R.string.copy_done, Snackbar.LENGTH_SHORT).show();

                        break;

                    case R.id.image_view_mark_star:


                        final NotebookMark item3 = notebookMarkList.get(position);

//                            DBUtil.deleteValue(dbHelper,item3.getInput());
//
//                            list.remove(position);
                        NoteBookDBUtil.deleteValue(item3.getInput());
                        notebookMarkList.remove(position);


                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, notebookMarkList.size());
                        isData();
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

    }

    private void initViews(View view) {

        recyclerViewNotebook = (RecyclerView) view.findViewById(R.id.recycler_view_notebook);
        recyclerViewNotebook.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        tvNoNote = (TextView) view.findViewById(R.id.tv_no_note);

    }


}
