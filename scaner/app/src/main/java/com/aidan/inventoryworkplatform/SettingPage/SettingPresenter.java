package com.aidan.inventoryworkplatform.SettingPage;

import android.widget.TextView;

import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.SelectableItem;
import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2017/1/8.
 */

public class SettingPresenter implements SettingContract.presenter{
    private SettingContract.view view;
    private Location location;
    private Agent agent;
    private Department department;
    private Agent user;
    private Department useGroup;
    private List<Item> itemList;
    private SelectableItem.Type type = null;
    SettingPresenter(SettingContract.view view,List<Item> itemList){
        this.view = view;
        this.itemList = itemList;
    }

    @Override
    public void start() {
        view.findView();
        view.setViewClick();
    }

    @Override
    public void itemTextViewClick(final TextView itemTextView) {
        List<SearchableItem> temp = new ArrayList<>();
        temp.add(SelectableItem.Type.property);
        temp.add(SelectableItem.Type.item);
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                itemTextView.setText(item.getName());
                type = (SelectableItem.Type) item;
                location = null;
                agent = null;
                department = null;
                user = null;
                useGroup = null;
                view.reset();
            }
        },"目標列表",temp);
    }

    @Override
    public void locationTextViewClick(final TextView locationTextView){
        if (type == null) {
            view.showToast("請先選擇目標");
            return;
        }
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( LocationSingleton.getInstance().getLocationList(type));
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                locationTextView.setText(item.getName());
                location = (Location) item;
            }
        },"地點列表",temp);
    }
    @Override
    public void departmentTextViewClick(final TextView departmentTextView){
        if (type == null) {
            view.showToast("請先選擇目標");
            return;
        }
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList(type));
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                departmentTextView.setText(item.getName());
                department = (Department) item;
            }
        },"保管部門列表",temp);
    }
    @Override
    public void agentTextViewClick(final TextView agentTextView){
        if (type == null) {
            view.showToast("請先選擇目標");
            return;
        }
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList(type));
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                agentTextView.setText(item.getName());
                agent = (Agent) item;
            }
        },"保管人列表",temp);
    }

    @Override
    public void useGroupTextViewClick(final TextView useGroupTextVie) {
        if (type == null) {
            view.showToast("請先選擇目標");
            return;
        }
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList(type));
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                useGroupTextVie.setText(item.getName());
                useGroup = (Department) item;
            }
        },"使用部門列表",temp);
    }

    @Override
    public void userTextViewClick(final TextView userTextView) {
        if (type == null) {
            view.showToast("請先選擇目標");
            return;
        }
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList(type));
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                userTextView.setText(item.getName());
                user = (Agent) item;
            }
        },"使用人列表",temp);

    }

    @Override
    public void searchTextViewClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                view.showProgress("設定中");
                int count = 0;
                for(Item item : itemList){
                    if (item.getItemType() != type) {
                        continue;
                    }
                    if(location != null ){
                        item.setLocation(location);
                    }
                    if(agent != null ){
                        item.setCustodian(agent);
                    }
                    if(department != null){
                        item.setCustodyGroup(department);
                    }
                    if(user != null){
                        item.setUser(user);
                    }
                    if(useGroup != null){
                        item.setUseGroup(useGroup);
                    }
                    count++;
                    view.updateProgress(count * 100 / itemList.size());
                }
                ItemSingleton.getInstance().saveToDB();
                view.hideProgress();
                view.dismissAllowingStateLoss();
            }
        }).start();
    }

}
