package com.aidan.inventoryworkplatform.ui.file;

import java.io.FileDescriptor;
import java.util.Set;

/**
 * Created by Aidan on 2016/11/20.
 */

public interface FileContract {
    interface view {
        void setViewClick();
        void checkPermission();
        void showToast(String msg);
    }
    interface presenter{
        void readTxtButtonClick(FileDescriptor fileDescriptor);
        void readNameTextViewClick(FileDescriptor fileDescriptor);
        void readPurchaseDateTextViewClick(FileDescriptor fileDescriptor);
        void saveFile(String fileName, String preferencesKey, Set<String> allowType, boolean onlyChanged);
        void clearData();
        void inputItemTextViewClick(FileDescriptor fileDescriptor);
    }
}
