package com.aidan.inventoryworkplatform.ui.search;



import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.BaseFragmentManager;
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListPresenter;
import com.aidan.inventoryworkplatform.KeyConstants;
import com.aidan.inventoryworkplatform.R;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


/**
 * Created by Aidan on 2017/1/8.
 */

public class SearchFragment extends DialogFragment implements SearchContract.view {
    SearchContract.presenter presenter;
    EditText serialMinNumberEditText, serialMaxNumberEditText;
    TextView locationTextView, agentTextView, departmentTextView;
    TextView searchTextView, clearTextView, printTextView;
    TextView useGroupTextView, userTextView;
    EditText c1EditText, c2EditText, c3EditText, c4EditText, c5EditText;
    BaseFragmentManager baseFragmentManager;
    TextView tagContentTextView, sortTextView, minDateTextView, maxDateTextView, printLittleTagTextView;
    EditText nameEditText, nicknameEditText;
    ProgressDialog mProgressDialog;

    public static SearchFragment newInstance(BaseFragmentManager baseFragmentManager) {
        SearchFragment fragment = new SearchFragment();
        fragment.baseFragmentManager = baseFragmentManager;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = ViewModelProviders.of(this).get(SearchPresenter.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
        setViewClick();
    }

    public void findView(View rootView) {
        c1EditText = (EditText) rootView.findViewById(R.id.c1EditText);
        c2EditText = (EditText) rootView.findViewById(R.id.c2EditText);
        c3EditText = (EditText) rootView.findViewById(R.id.c3EditText);
        c4EditText = (EditText) rootView.findViewById(R.id.c4EditText);
        c5EditText = (EditText) rootView.findViewById(R.id.c5EditText);
        serialMinNumberEditText = (EditText) rootView.findViewById(R.id.serialMinNumberEditText);
        serialMaxNumberEditText = (EditText) rootView.findViewById(R.id.serialMaxNumberEditText);
        locationTextView = (TextView) rootView.findViewById(R.id.locationTextView);
        agentTextView = (TextView) rootView.findViewById(R.id.agentTextView);
        departmentTextView = (TextView) rootView.findViewById(R.id.departmentTextView);
        searchTextView = (TextView) rootView.findViewById(R.id.searchTextView);
        clearTextView = (TextView) rootView.findViewById(R.id.clearTextView);
        printTextView = (TextView) rootView.findViewById(R.id.printTextView);
        printLittleTagTextView = (TextView) rootView.findViewById(R.id.printLittleTagTextView);
        useGroupTextView = (TextView) rootView.findViewById(R.id.useGroupTextView);
        userTextView = (TextView) rootView.findViewById(R.id.userTextView);
        tagContentTextView = (TextView) rootView.findViewById(R.id.tagContentTextView);
        sortTextView = (TextView) rootView.findViewById(R.id.sortTextView);
        minDateTextView = (TextView) rootView.findViewById(R.id.minDateTextView);
        maxDateTextView = (TextView) rootView.findViewById(R.id.maxDateTextView);
        nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
        nicknameEditText = (EditText) rootView.findViewById(R.id.nicknameEditText);
    }

    public void setViewClick() {
        locationTextView.setOnClickListener(v -> presenter.locationTextViewClick(locationTextView));
        agentTextView.setOnClickListener(v -> presenter.agentTextViewClick(agentTextView));
        departmentTextView.setOnClickListener(v -> presenter.departmentTextViewClick(departmentTextView));
        userTextView.setOnClickListener(v -> presenter.userTextViewClick(userTextView));
        useGroupTextView.setOnClickListener(v -> presenter.useGroupTextViewClick(useGroupTextView));
        tagContentTextView.setOnClickListener(v -> presenter.tagContentTextViewClick(tagContentTextView));
        sortTextView.setOnClickListener(v -> presenter.sortTextViewClick(sortTextView));
        minDateTextView.setOnClickListener(v -> presenter.minDateTextViewClick(getActivity()));
        maxDateTextView.setOnClickListener(v -> presenter.maxDateTextViewClick(getActivity()));
        clearTextView.setOnClickListener(v -> presenter.clearAll());
        searchTextView.setOnClickListener(v -> {
            String id = "";
            id += c1EditText.getText().toString();
            id += c2EditText.getText().toString();
            id += c3EditText.getText().toString();
            id += c4EditText.getText().toString();
            id += c5EditText.getText().toString();
            String name = nameEditText.getText().toString();
            String nickname = nicknameEditText.getText().toString();
            presenter.searchTextViewClick(name, nickname, id, serialMinNumberEditText.getText().toString(), serialMaxNumberEditText.getText().toString());
        });

        printTextView.setOnClickListener(v -> {
            String id = "";
            id += c1EditText.getText().toString();
            id += c2EditText.getText().toString();
            id += c3EditText.getText().toString();
            id += c4EditText.getText().toString();
            id += c5EditText.getText().toString();
            String name = nameEditText.getText().toString();
            String nickname = nicknameEditText.getText().toString();
            presenter.printTextViewClick(getContext(), name, nickname, id, serialMinNumberEditText.getText().toString(), serialMaxNumberEditText.getText().toString());
        });
        printLittleTagTextView.setOnClickListener(v -> {
            String id = "";
            id += c1EditText.getText().toString();
            id += c2EditText.getText().toString();
            id += c3EditText.getText().toString();
            id += c4EditText.getText().toString();
            id += c5EditText.getText().toString();
            String name = nameEditText.getText().toString();
            String nickname = nicknameEditText.getText().toString();
            presenter.printLittleTextViewClick(getContext(), name, nickname, id, serialMinNumberEditText.getText().toString(), serialMaxNumberEditText.getText().toString());
        });

        c1EditText.addTextChangedListener(getNextTextWatcher(1, c2EditText));
        c2EditText.addTextChangedListener(getNextTextWatcher(2, c3EditText));
        c3EditText.addTextChangedListener(getNextTextWatcher(2, c4EditText));
        c4EditText.addTextChangedListener(getNextTextWatcher(2, c5EditText));
        c5EditText.addTextChangedListener(getNextTextWatcher(4, serialMinNumberEditText));
        serialMinNumberEditText.addTextChangedListener(getNextTextWatcher(7, serialMaxNumberEditText));
        printTextView.setVisibility(KeyConstants.showPrint ? View.VISIBLE : View.GONE);
        printLittleTagTextView.setVisibility(KeyConstants.showPrintLittleTag ? View.VISIBLE : View.GONE);
    }

    private TextWatcher getNextTextWatcher(final int length, final EditText next) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == length) {
                    next.requestFocus();
                }
            }
        };
    }

    @Override
    public void clearViews() {
        c1EditText.setText("");
        c2EditText.setText("");
        c3EditText.setText("");
        c4EditText.setText("");
        c5EditText.setText("");
        serialMinNumberEditText.setText("");
        serialMaxNumberEditText.setText("");
        locationTextView.setText("請點選存置地點");
        agentTextView.setText("請點選保管人");
        departmentTextView.setText("請點選保管單位");
        tagContentTextView.setText("請點選標籤內容");
        sortTextView.setText("請點選排序條件");
        minDateTextView.setText("請點選起始日期");
        maxDateTextView.setText("請點選最後日期");
        nameEditText.setText("");
        nicknameEditText.setText("");
    }

    @Override
    public void showToast(final String msg) {
        getView().post(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void showSetDialog(SearchItemAdapter.OnClickListener clickListener, String title, List<SearchableItem> dataList) {
        SearchItemDialog dialog = new SearchItemDialog(getActivity(), dataList);
        dialog.setTitle(title);
        dialog.setOnClickListener(clickListener);
        dialog.show();
    }

    @Override
    public void showFragmentWithResult(List<Item> items) {
        ItemListPresenter presenter = ViewModelProviders.of(this).get(ItemListPresenter.class);
        presenter.itemList = items;
        Fragment fragment = ItemListFragment.newInstance(true);
        baseFragmentManager.loadFragment(fragment);
    }

    @Override
    public void showProgress(final String title) {
        getView().post(() -> {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage("正在處理請稍後...");
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        });
    }

    @Override
    public void hideProgress() {
        getView().post(() -> mProgressDialog.dismiss());
    }

    @Override
    public void updateProgress(final int value) {
        getView().post(() -> mProgressDialog.setProgress(value));
    }

    @Override
    public void setMinDateTextView(Calendar c) {
        minDateTextView.setText(String.valueOf(c.get(Calendar.YEAR) - 1911) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public void setMaxDateTextView(Calendar c) {
        maxDateTextView.setText(String.valueOf(c.get(Calendar.YEAR) - 1911) + "/" + String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
    }


}