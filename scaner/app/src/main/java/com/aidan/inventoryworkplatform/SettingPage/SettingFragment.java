package com.aidan.inventoryworkplatform.SettingPage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.BaseFragmentManager;
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.R;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


/**
 * Created by Aidan on 2017/1/8.
 */

public class SettingFragment extends DialogFragment implements SettingContract.view {
    SettingContract.presenter presenter;
    ViewGroup rootView;
    TextView locationTextView, agentTextView, departmentTextView;
    TextView settingTextView, cancelTextView;
    TextView useGroupTextView, userTextView;
    BaseFragmentManager baseFragmentManager;
    Reload reload;
    ProgressDialog mProgressDialog;
    TextView itemTextView;

    public interface Reload{
        void reload();
    }
    public static SettingFragment newInstance(BaseFragmentManager baseFragmentManager,List<Item> itemList,Reload reload) {
        SettingFragment fragment = new SettingFragment();
        fragment.baseFragmentManager = baseFragmentManager;
        fragment.presenter = new SettingPresenter(fragment,itemList);
        fragment.reload = reload;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        if (presenter == null) dismissAllowingStateLoss();
        presenter.start();
        return rootView;
    }

    @Override
    public void findView() {
        locationTextView = (TextView) rootView.findViewById(R.id.locationTextView);
        agentTextView = (TextView) rootView.findViewById(R.id.agentTextView);
        departmentTextView = (TextView) rootView.findViewById(R.id.departmentTextView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        settingTextView = (TextView) rootView.findViewById(R.id.settingTextView);
        useGroupTextView = (TextView) rootView.findViewById(R.id.useGroupTextView);
        userTextView = (TextView) rootView.findViewById(R.id.userTextView);
        itemTextView = (TextView) rootView.findViewById(R.id.itemTextView);
    }

    @Override
    public void setViewClick() {
        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.itemTextViewClick(itemTextView);
            }
        });
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.locationTextViewClick(locationTextView);
            }
        });
        agentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.agentTextViewClick(agentTextView);
            }
        });
        departmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.departmentTextViewClick(departmentTextView);
            }
        });
        userTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.userTextViewClick(userTextView);
            }
        });
        useGroupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.useGroupTextViewClick(useGroupTextView);
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        settingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("警告");
        builder.setMessage("將修改目前顯示上的所有項目");
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.searchTextViewClick();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void dismiss(){
        reload.reload();
        super.dismiss();
    }

    @Override
    public void showSetDialog(SearchItemAdapter.OnClickListener clickListener, String title, List<SearchableItem> dataList) {
        SearchItemDialog dialog = new SearchItemDialog(getActivity(),dataList);
        dialog.setTitle(title);
        dialog.setOnClickListener(clickListener);
        dialog.show();
    }

    @Override
    public void showProgress(final String title) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = new ProgressDialog(rootView.getContext());
                mProgressDialog.setCancelable(false);
                mProgressDialog.setTitle(title);
                mProgressDialog.setMessage("正在處理請稍後...");
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void hideProgress() {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void updateProgress(final int value) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setProgress(value);
            }
        });
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(rootView.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reset() {
        locationTextView.setText("請點選存置地點");
        departmentTextView.setText("請點選保管單位");
        agentTextView.setText("請點選保管人");
        useGroupTextView.setText("請點選使用單位");
        userTextView.setText("請點選使用人");

    }

}
