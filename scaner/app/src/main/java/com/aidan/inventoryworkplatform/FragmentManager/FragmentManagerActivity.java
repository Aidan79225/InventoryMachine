package com.aidan.inventoryworkplatform.FragmentManager;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.BaseFragmentManager;
import com.aidan.inventoryworkplatform.Database.AgentDAO;
import com.aidan.inventoryworkplatform.Database.DepartmentDAO;
import com.aidan.inventoryworkplatform.Database.ItemDAO;
import com.aidan.inventoryworkplatform.Database.LocationDAO;
import com.aidan.inventoryworkplatform.Dialog.ScannerSettingDialog;
import com.aidan.inventoryworkplatform.FilePage.FileFragment;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment;
import com.aidan.inventoryworkplatform.ItemListPage.ItemListPresenter;
import com.aidan.inventoryworkplatform.KeyConstants;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.R;
import com.aidan.inventoryworkplatform.ScannerPage.ScannerFragment;
import com.aidan.inventoryworkplatform.SearchPage.SearchFragment;
import com.aidan.inventoryworkplatform.Singleton;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class FragmentManagerActivity extends AppCompatActivity implements FragmentManagerContract.view, BaseFragmentManager {
    FragmentManagerContract.presenter presenter;
    View fragmentContainer;
    TextView fileTextView, scanTextView, searchTextView, itemListTextView, itemDetailTextView;
    private Fragment curFragment;
    private IntentFilter filter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemDAO.init(getApplicationContext());
        DepartmentDAO.init(getApplicationContext());
        AgentDAO.init(getApplicationContext());
        LocationDAO.init(getApplicationContext());
        Singleton.setPreference(getApplicationContext());
        setContentView(R.layout.activity_fragment_maneger);
        presenter = new FragmentManagerPresenter(this);
        presenter.start();
    }

    @Override
    public void findView() {
        fragmentContainer = findViewById(R.id.fragmentContainer);
        fileTextView =  findViewById(R.id.fileTextView);
        scanTextView = findViewById(R.id.scanTextView);
        searchTextView =  findViewById(R.id.searchTextView);
        itemListTextView =  findViewById(R.id.itemListTextView);
        itemDetailTextView =  findViewById(R.id.itemDetailTextView);
        loadFileFragment();
    }


    @Override
    public void setViewClick() {
        fileTextView.setOnClickListener(v -> loadFileFragment());
        itemListTextView.setOnClickListener(v -> loadItemListFragment());
        searchTextView.setOnClickListener(v -> loadSearchFragment());
        scanTextView.setOnClickListener(v -> loadScannerFragment());
    }

    @Override
    public void setScanner() {

        // ***************************************************//
        // Register for the IntentFilter whose content is the
        // com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA string
        // Later, when myDataReceiver, a BroadcastReceiver class, receives the intent coming from service, it will then be able to deal with something else.
        // ***************************************************//
        filter = new IntentFilter();
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
        filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);
    }


    @Override
    public void loadScannerFragment() {
        Fragment fragment = ScannerFragment.newInstance(this);
        loadFragment(fragment);
    }

    @Override
    public void loadItemListFragment() {
        ItemListPresenter presenter = ViewModelProviders.of(this).get(ItemListPresenter.class);
        presenter.itemList = ItemSingleton.getInstance().getItemList();
        Fragment fragment = ItemListFragment.newInstance(false);
        loadFragment(fragment);
    }

    @Override
    public void loadFileFragment() {
        Fragment fragment = FileFragment.instantiate(this, FileFragment.class.getName());
        loadFragment(fragment);
    }

    @Override
    public void loadSearchFragment() {
        Fragment fragment = SearchFragment.newInstance(this);
        loadFragment(fragment);
    }

    @Override
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStackImmediate();
        }
        curFragment = fragment;
        transaction.replace(R.id.fragmentContainer, fragment, fragment.getClass().getName());
        transaction.commit();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (curFragment != null) {
            curFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* For Back Control */
    private Boolean hasPressBack = false;
    private Timer timer = new Timer(true);
    private class BackTimerTask extends TimerTask {
        public void run() {
            hasPressBack = false;
        }
    }

    @Override
    public void onBackPressed() {
        if(hasPressBack){
            timer.cancel();
        }else {
            hasPressBack = true;
            Toast.makeText(this, "再按一次離開本程式", Toast.LENGTH_SHORT).show();
            //設定時間點刪去back記錄
            timer.schedule(new BackTimerTask(), 2500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return KeyConstants.showMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ScannerMenu:
                showScannerSettingDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showScannerSettingDialog() {
        new ScannerSettingDialog().show(getSupportFragmentManager(), ScannerSettingDialog.class.getName());
    }



}
