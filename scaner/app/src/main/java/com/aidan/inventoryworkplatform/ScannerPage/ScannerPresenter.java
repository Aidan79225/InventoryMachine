package com.aidan.inventoryworkplatform.ScannerPage;

import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.SettingConstants;
import com.aidan.inventoryworkplatform.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2017/1/22.
 */

public class ScannerPresenter implements ScannerContract.presenter {
    private ScannerContract.view view;
    private List<Item> itemList;

    public ScannerPresenter(ScannerContract.view view) {
        this.view = view;
        this.itemList = ScannerItemManager.getInstance().getItemList();
    }

    @Override
    public void start() {
        view.findView();
        view.setEditTextScan();
        view.setListView(itemList);
        view.setViewClick();
    }

    @Override
    public void scan(String key) {
        if(key == null )return;
        key = trim(key, '*');
        key = key.replace("\n","");
        if(key.length() == 0 )return;
        Singleton.log(key);

        firstTypeScan(key);
    }

    public String trim(String key, char c) {
        int len = key.length();
        int st = 0;

        while ((st < len) && (key.charAt(st) == c)) {
            st++;
        }
        while ((st < len) && (key.charAt(len - 1) == c)) {
            len--;
        }
        return ((st > 0) || (len < key.length())) ? key.substring(st, len) : key;
    }

    public void firstTypeScan(String key){
        for (Item item : itemList) {
            if (item.getTotalNumber().equals(key)) {
                view.showToast("已重複盤點 : " + key);
                return;
            }
        }
        for (Item item : ItemSingleton.getInstance().getItemList()) {
            if (item.getTotalNumber().equals(key)) {
                item.setConfirm(true);
                if (Singleton.preferences.getBoolean(SettingConstants.PRINT_IN_SCANNER, false)) {
                    item.setPrint(true);
                }
                if (Singleton.preferences.getBoolean(SettingConstants.DELETE_IN_SCANNER, false)) {
                    item.setDelete(true);
                }
                itemList.add(0, item);
                ItemSingleton.getInstance().saveItem(item);
                view.refreshList();
                if (Singleton.preferences.getBoolean(SettingConstants.SHOW_AFTER_SCAN, false)) {
                    view.showItem(item);
                }
                return;
            }
        }
        view.showToast("找不到對應編號 : " + key);
    }
}
