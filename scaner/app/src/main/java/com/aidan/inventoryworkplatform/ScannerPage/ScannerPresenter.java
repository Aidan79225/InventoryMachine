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
        String[] temps = key.split("-");
        if (temps.length < 3) return;
        if(temps[0].length()<7){
            firstTypeScan(key,temps);
        } else{
            secondTypeScan(key,temps);
        }
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

    public void firstTypeScan(String key, String[] temps){
        temps[2] = temps[2].substring(2);
        int serialNumber = Integer.valueOf(temps[2]);
        for (Item item : itemList) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                view.showToast("已重複盤點 : " + key);
                return;
            }
        }
        for (Item item : ItemSingleton.getInstance().getItemList()) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
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

    public void secondTypeScan(String key, String[] temps){
        temps[1] = temps[0] + temps[1];
        int serialNumber = Integer.valueOf(temps[2]);
        for (Item item : itemList) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                view.showToast("已重複盤點 : " + key);
                return;
            }
        }
        for (Item item : ItemSingleton.getInstance().getItemList()) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                item.setConfirm(true);
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
