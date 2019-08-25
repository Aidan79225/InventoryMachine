package com.aidan.inventoryworkplatform.ScannerPage;

import com.aidan.inventoryworkplatform.Entity.Item;

import java.util.List;

/**
 * Created by Aidan on 2017/1/22.
 */

public interface ScannerContract {
    interface view{
        void findView();
        void setEditTextScan();
        void setListView(List<Item> itemList);
        void refreshList();
        void showToast(String msg);
        void setViewClick();
        void showItem(Item item);
    }
    interface presenter{
        void start();
        void scan(String key);
    }
}
