package com.aidan.inventoryworkplatform.SettingPage;

import android.widget.TextView;

import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Item;

import java.util.List;

/**
 * Created by Aidan on 2017/1/8.
 */

public interface SettingContract {
    interface view{
        void findView();
        void setViewClick();
        void showSetDialog(SearchItemAdapter.OnClickListener clickListener, String title, List<SearchableItem> dataList);
        void dismiss();
        void showProgress(String msg);
        void hideProgress();
        void updateProgress(int value);
        void dismissAllowingStateLoss();
        void showToast(String msg);
        void reset();
    }
    interface presenter{
        void start();
        void itemTextViewClick(TextView itemTextView);
        void locationTextViewClick(TextView locationTextView);
        void departmentTextViewClick(TextView departmentTextView);
        void agentTextViewClick(TextView agentTextView);
        void useGroupTextViewClick(TextView departmentTextView);
        void userTextViewClick(TextView agentTextView);

        void searchTextViewClick();

    }
}
