package com.aidan.inventoryworkplatform.ItemListPage;


import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.ui.item.detail.ItemDetailFragment;
import com.aidan.inventoryworkplatform.R;
import com.aidan.inventoryworkplatform.SettingPage.SettingFragment;
import com.aidan.inventoryworkplatform.Singleton;
import com.aidan.inventoryworkplatform.Utils.SettingsSingleton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Aidan on 2016/11/20.
 */

public class ItemListFragment extends DialogFragment implements ItemListContract.view {
    private ItemListPresenter presenter;
    private ItemListAdapter adapter;
    private ListView itemListView;
    private TextView contentTextView, settingTextView;
    private boolean showSetAll = false;
    private EditText scanEditText;

    public static ItemListFragment newInstance(boolean showSetAll) {
        ItemListFragment fragment = new ItemListFragment();
        fragment.showSetAll = showSetAll;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = ViewModelProviders.of(getActivity()).get(ItemListPresenter.class);
        itemListView = view.findViewById(R.id.itemListView);
        contentTextView = view.findViewById(R.id.contentTextView);
        settingTextView = view.findViewById(R.id.settingTextView);
        settingTextView.setVisibility(showSetAll ? View.VISIBLE : View.GONE);
        scanEditText = view.findViewById(R.id.scanEditText);
        SettingsSingleton.getInstance().getShowScannerInItemList().observe(this, visible -> scanEditText.setVisibility(visible? View.VISIBLE : View.GONE));
        setEditTextScan();
        setListView(presenter.itemList);
        presenter.refreshList.observe(this, s -> refreshList());
        presenter.showItem.observe(this, this::showItem);
        presenter.getShowToast().observe(this, this::showToast);
    }

    @Override
    public void setListView(List<Item> itemList) {
        adapter = new ItemListAdapter(itemList);
        adapter.setContentInformationTextView(contentTextView);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Item item = adapter.getItem(position - 1);
                    if (item == null) {
                        return;
                    }
                    gotoDetailFragment(
                            item,
                            () -> adapter.notifyDataSetChanged()
                    );
                }
        );
        itemListView.setOnLongClickListener(v -> false);
        adapter.notifyDataSetChanged();
        settingTextView.setOnClickListener(v -> {
            DialogFragment dialogFragment = SettingFragment.newInstance(adapter.getItems(), () -> adapter.notifyDataSetChanged());
            dialogFragment.show(getFragmentManager(), dialogFragment.getClass().getName());
        });
    }

    public void setEditTextScan() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(scanEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanEditText.setShowSoftInputOnFocus(false);
        }
        scanEditText.requestFocus();
        scanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    Singleton.log(s.toString());
                    presenter.scan(s.toString());
                    scanEditText.setText("");
                }
            }
        });
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showItem(Item item) {
        gotoDetailFragment(item, () -> adapter.notifyDataSetChanged());
    }

    private void gotoDetailFragment(Item item, ItemListFragment.RefreshItems refreshItems) {
        DialogFragment fragment = ItemDetailFragment.newInstance(item, refreshItems);
        fragment.show(getFragmentManager(), ItemDetailFragment.class.getName());
    }

    public interface RefreshItems {
        void refresh();
    }
}
