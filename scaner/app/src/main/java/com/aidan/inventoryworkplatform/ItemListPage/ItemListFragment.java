package com.aidan.inventoryworkplatform.ItemListPage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.BaseFragmentManager;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.ItemDetailPage.ItemDetailFragment;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.R;
import com.aidan.inventoryworkplatform.SettingPage.SettingFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Aidan on 2016/11/20.
 */

public class ItemListFragment extends DialogFragment implements ItemListContract.view {
    private ItemListContract.presenter presenter;
    private ItemListAdapter adapter;
    private ViewGroup rootView;
    private ListView itemListView;
    private TextView contentTextView,settingTextView;
    private BaseFragmentManager baseFragmentManager;
    private boolean showSetAll = false;

    public static ItemListFragment newInstance(List<Item> itemList,BaseFragmentManager baseFragmentManager,boolean showSetAll){
        ItemListFragment fragment = new ItemListFragment();
        fragment.presenter = new ItemListPresenter(fragment,itemList);
        fragment.baseFragmentManager = baseFragmentManager;
        fragment.showSetAll = showSetAll;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_item_list, container, false);
        if(presenter == null)presenter = new ItemListPresenter(this, ItemSingleton.getInstance().getItemList());
        presenter.start();
        return rootView;
    }

    @Override
    public void findView() {
        itemListView = rootView.findViewById(R.id.itemListView);
        contentTextView = rootView.findViewById(R.id.contentTextView);
        settingTextView = rootView.findViewById(R.id.settingTextView);
        settingTextView.setVisibility(showSetAll ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setListView(List<Item> itemList) {
        adapter = new ItemListAdapter(itemList);
        adapter.setContentInformationTextView(contentTextView);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener((parent, view, position, id) -> gotoDetailFragment(adapter.getItem(position-1), () -> adapter.notifyDataSetChanged()));
        itemListView.setOnLongClickListener(v -> false);
        adapter.notifyDataSetChanged();
        settingTextView.setOnClickListener(v -> {
            DialogFragment dialogFragment = SettingFragment.newInstance(baseFragmentManager, adapter.getItems(), () -> adapter.notifyDataSetChanged());
            dialogFragment.show(getFragmentManager(),dialogFragment.getClass().getName());
        });
    }

    private void gotoDetailFragment(Item item,ItemListFragment.RefreshItems refreshItems){
        DialogFragment fragment = ItemDetailFragment.newInstance(item,refreshItems);
        fragment.show(getFragmentManager(),ItemDetailFragment.class.getName());
    }

    public interface RefreshItems{
        void refresh();
    }
}
