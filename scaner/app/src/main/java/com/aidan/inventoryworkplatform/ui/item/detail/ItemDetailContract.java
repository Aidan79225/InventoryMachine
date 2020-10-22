package com.aidan.inventoryworkplatform.ui.item.detail;

import android.content.DialogInterface;

import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Item;

import java.util.List;

/**
 * Created by s352431 on 2016/11/22.
 */
public interface ItemDetailContract {
    interface view{
        void setViewValue(Item item);
        void setViewClick();
        void showSetDialog(DialogInterface.OnClickListener clickListener, String title, final List<String> temp);
        void showSetDialog(SearchItemAdapter.OnClickListener clickListener, String title, List<SearchableItem> dataList);
        void showPrintDialog(Item item);
        void showLittlePrintDialog(Item item);
    }
    interface presenter{
        void start();
        void saveItemToChecked(boolean flag);
        void locationTextViewClick();
        void departmentTextViewClick();
        void agentTextViewClick();
        void useGroupTextViewClick();
        void userTextViewClick();
        void deleteTextViewClick();
        void printTextViewClick();
        void printButtonClick();
        void printLittleButtonClick();
        void tagContentTextViewClick();
    }
}