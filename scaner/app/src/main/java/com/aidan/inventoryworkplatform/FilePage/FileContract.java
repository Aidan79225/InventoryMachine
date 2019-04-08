package com.aidan.inventoryworkplatform.FilePage;

import com.aidan.inventoryworkplatform.Utils.ReadExcel;

import java.io.FileDescriptor;
import java.util.Set;

/**
 * Created by Aidan on 2016/11/20.
 */

public interface FileContract {
    interface view {
        void findView();
        void setViewClick();
        void showProgress(String msg);
        void hideProgress();
        void updateProgress(int value);
        void checkPermission();
        void showToast(String msg);
    }
    interface presenter{
        void start();
        void readTxtButtonClick(FileDescriptor fileDescriptor);
        void readNameTextViewClick(FileDescriptor fileDescriptor);
        void readPurchaseDateTextViewClick(FileDescriptor fileDescriptor);
        void saveFile(String fileName, String preferencesKey, Set<String> allowType);
        void clearData();
        void inputItemTextViewClick(FileDescriptor fileDescriptor);
    }
}
