package com.aidan.inventoryworkplatform.Utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsSingleton {
    public static SettingsSingleton getInstance() {
        if (instance == null) {
            synchronized (SettingsSingleton.class) {
                if (instance == null) {
                    instance = new SettingsSingleton();
                }
            }
        }
        return instance;
    }
    private static SettingsSingleton instance;

    private MutableLiveData<Boolean> showScannerInItemList = new MutableLiveData<>();

    private SettingsSingleton(){
        showScannerInItemList.setValue(false);
    }

    public MutableLiveData<Boolean> getShowScannerInItemList() {
        return showScannerInItemList;
    }

}
