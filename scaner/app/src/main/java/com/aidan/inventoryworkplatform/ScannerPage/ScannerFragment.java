package com.aidan.inventoryworkplatform.ScannerPage;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.BaseFragmentManager;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.ItemDetailPage.ItemDetailFragment;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListAdapter;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment.RefreshItems;
import com.aidan.inventoryworkplatform.R;
import com.aidan.inventoryworkplatform.SettingConstants;
import com.aidan.inventoryworkplatform.Singleton;
import com.cipherlab.barcode.ReaderManager;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Aidan on 2017/1/22.
 */

public class ScannerFragment extends DialogFragment implements ScannerContract.view, QRCodeView.Delegate {
    ViewGroup rootView;
    EditText scanEditText;
    ListView itemListView;
    ScannerContract.presenter presenter;
    BaseFragmentManager baseFragmentManager;
    ItemListAdapter adapter;
    ZBarView zbarview;
    TextView cameraButton, listTitleTextView, titleTextView;
    boolean isOpenCamera = false;

    private ReaderManager readerManager;
    private IntentFilter filter;

    public static ScannerFragment newInstance(BaseFragmentManager baseFragmentManager) {
        ScannerFragment fragment = new ScannerFragment();
        fragment.baseFragmentManager = baseFragmentManager;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_scanner, container, false);
        isOpenCamera = getActivity().getSharedPreferences(SettingConstants.SETTING_CAMERA,0).getBoolean(SettingConstants.CAMERA_IS_OPEN,false);
        presenter = new ScannerPresenter(this);
        presenter.start();
        return rootView;
    }

    @Override
    public void onDestroy() {
        zbarview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void findView() {
        scanEditText = rootView.findViewById(R.id.scanEditText);
        itemListView = rootView.findViewById(R.id.itemListView);
        zbarview = rootView.findViewById(R.id.zbarview);
        cameraButton = rootView.findViewById(R.id.cameraButton);
        listTitleTextView = rootView.findViewById(R.id.listTitleTextView);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        zbarview.setDelegate(this);
        zbarview.changeToScanBarcodeStyle();
    }

    public void configView() {
        if (isOpenCamera) {
            cameraButton.setText("關閉相機");
            cameraButton.setTextColor(getResources().getColor(R.color.bg_white));
            listTitleTextView.setTextColor(getResources().getColor(R.color.bg_white));
            titleTextView.setVisibility(View.VISIBLE);
            zbarview.setVisibility(View.VISIBLE);
            zbarview.startCamera();
            zbarview.showScanRect();
            zbarview.startSpotDelay(333);
        } else {
            cameraButton.setText("開啟相機");
            cameraButton.setTextColor(getResources().getColor(R.color.text_black));
            listTitleTextView.setTextColor(getResources().getColor(R.color.text_black));
            titleTextView.setVisibility(View.GONE);
            zbarview.setVisibility(View.GONE);
            zbarview.stopCamera();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
//        getActivity().unregisterReceiver(scanReceiver);
//        qrCodeReaderView.stopScanner();
        zbarview.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().registerReceiver(scanReceiver, filter);
        configView();
    }


    @Override
    public void setEditTextScan() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(scanEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        scanEditText.setShowSoftInputOnFocus(false);
        scanEditText.requestFocus();
        scanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    Singleton.log(s.toString());
                    presenter.scan(s.toString());
                    scanEditText.setText("");
                }
            }
        });
        readerManager = ReaderManager.InitInstance(getActivity().getApplicationContext());
        filter = new IntentFilter();
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);


    }

    @Override
    public void setListView(List<Item> itemList) {
        adapter = new ItemListAdapter(itemList);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener((parent, view, position, id) -> gotoDetailFragment(adapter.getItem(position - 1), () -> adapter.notifyDataSetChanged()));
    }

    private void gotoDetailFragment(Item item, RefreshItems refreshItems) {
        DialogFragment fragment = ItemDetailFragment.newInstance(item, refreshItems);
        fragment.show(getFragmentManager(), ItemDetailFragment.class.getName());
    }


    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setViewClick() {
        cameraButton.setOnClickListener(v -> {
            isOpenCamera = !isOpenCamera;
            v.getContext().getSharedPreferences(SettingConstants.SETTING_CAMERA,0).edit().putBoolean(SettingConstants.CAMERA_IS_OPEN,isOpenCamera).apply();
            configView();
        });
    }

    @Override
    public void showItem(Item item) {
        gotoDetailFragment(item, () -> adapter.notifyDataSetChanged());
    }

//    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // If intent of the Intent_SOFTTRIGGER_DATA string is received
//            if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {
//
//                // fetch the data within the intent
//                String data = intent.getStringExtra(GeneralString.BcReaderData);
//
//                // display the fetched data
//                Singleton.log(data);
//                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
////                    scanEditText.setText(data);
//                presenter.scan(data);
//
////                if (readerManager != null) {
////                    readerManager.SoftScanTrigger();
////                }
//
//            } else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)) {
//
//                // fetch the data within the intent
//                String data = intent.getStringExtra(GeneralString.BcReaderData);
//
//                // display the fetched data
//                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
//                Singleton.log(data);
//
//            } else if (intent.getAction().equals(GeneralString.Intent_READERSERVICE_CONNECTED)) {
//                try {
//
//                    BcReaderType myReaderType = readerManager.GetReaderType();
//
//                    Toast.makeText(context, myReaderType.toString(), Toast.LENGTH_SHORT).show();
//                    ReaderOutputConfiguration settings = new ReaderOutputConfiguration();
//                    settings.enableKeyboardEmulation = Enable_State.FALSE;
//                    readerManager.Set_ReaderOutputConfiguration(settings);
////                    if (readerManager != null) {
////                        readerManager.SoftScanTrigger();
////                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    };

    @Override
    public void onScanQRCodeSuccess(String result) {
        presenter.scan(result);
        zbarview.startSpotDelay(333);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast("Scanner Error: ");
        zbarview.startSpotDelay(333);
    }
}
