package com.aidan.inventoryworkplatform.ItemListPage;

import com.aidan.inventoryworkplatform.Base.BaseViewModel;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.SingleLiveEvent;
import com.aidan.inventoryworkplatform.SettingConstants;
import com.aidan.inventoryworkplatform.Singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.lifecycle.ViewModel;

/**
 * Created by Aidan on 2016/11/20.
 */

public class ItemListPresenter extends BaseViewModel {
    public List<Item> itemList;
    List<Item> scaned = new ArrayList<>();
    SingleLiveEvent<String> refreshList = new SingleLiveEvent<>();
    SingleLiveEvent<Item> showItem = new SingleLiveEvent<>();

    public void scan(String key) {
        if(key == null )return;
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

    public void firstTypeScan(String key, String[] temps){
        temps[2] = temps[2].substring(2);
        int serialNumber = Integer.valueOf(temps[2]);


        for (Item item : scaned) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                showToast("已重複盤點 : " + key);
                return;
            }
        }

        for (Item item : itemList) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                item.setConfirm(true);
                if (Singleton.preferences.getBoolean(SettingConstants.PRINT_IN_SCANNER, false)) {
                    item.setPrint(true);
                }
                if (Singleton.preferences.getBoolean(SettingConstants.DELETE_IN_SCANNER, false)) {
                    item.setDelete(true);
                }
                scaned.add(0, item);
                ItemSingleton.getInstance().saveItem(item);
                refreshList.postValue("");
                showToast("盤點到編號 : " + key);
                if (Singleton.preferences.getBoolean(SettingConstants.SHOW_AFTER_SCAN, false)) {
                    showItem.postValue(item);
                }
                return;
            }
        }
        showToast("找不到對應編號 : " + key);
    }

    public void secondTypeScan(String key, String[] temps){
        temps[1] = temps[0] + temps[1];
        int serialNumber = Integer.valueOf(temps[2]);
        for (Item item : scaned) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                showToast("已重複盤點 : " + key);
                return;
            }
        }
        for (Item item : ItemSingleton.getInstance().getItemList()) {
            if (item.getNumber().equals(temps[1]) && serialNumber == Integer.valueOf(item.getSerialNumber().substring(2))) {
                item.setConfirm(true);
                scaned.add(0, item);
                ItemSingleton.getInstance().saveItem(item);
                refreshList.postValue("");
                showToast("盤點到編號 : " + key);
                if (Singleton.preferences.getBoolean(SettingConstants.SHOW_AFTER_SCAN, false)) {
                    showItem.postValue(item);
                }
                return;
            }
        }
        showToast("找不到對應編號 : " + key);
    }
}
