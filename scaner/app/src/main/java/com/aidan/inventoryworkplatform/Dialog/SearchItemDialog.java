package com.aidan.inventoryworkplatform.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.aidan.inventoryworkplatform.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Aidan on 2017/3/26.
 */

public class SearchItemDialog extends Dialog implements SearchItemAdapter.Closeable {
    EditText searchEditText;
    RecyclerView recyclerView;
    SearchItemAdapter adapter;
    List<SearchableItem> dataList = new ArrayList<>();
    SearchItemAdapter.OnClickListener onClickListener;
    public SearchItemDialog( Context context,List<SearchableItem> dataList) {
        super(context);
        this.dataList = dataList;
    }
    @Override
    public void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.dialog_search_item);
        findView();
        setRecyclerView();
        setSearchEditText();
    }
    private void findView(){
        searchEditText = (EditText)findViewById(R.id.searchEditText);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }
    private void setRecyclerView(){
        adapter = new SearchItemAdapter(dataList,this);
        adapter.setOnClickListener(onClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
    private void setSearchEditText(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String replace = s.toString();
//                if(replace.contains("\n")){
//                    s.replace(0,s.length(),replace.replace("\n", ""));
//                }
                String key = s.toString();
                if(s.length() == 0){
                    adapter.stopSearch();
                }else{
                    adapter.search(key);
                }
            }
        });
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    return true;
                }else if((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_BACK){
                    if(searchEditText.getText().length() > 0){
                        searchEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public void setOnClickListener(SearchItemAdapter.OnClickListener onClickListener){
       this.onClickListener = onClickListener;
    }
    @Override
    public void close() {
        dismiss();
    }

}
