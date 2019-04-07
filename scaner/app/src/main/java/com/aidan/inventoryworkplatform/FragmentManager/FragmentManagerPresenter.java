package com.aidan.inventoryworkplatform.FragmentManager;

import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;

/**
 * Created by Aidan on 2016/10/25.
 */

public class FragmentManagerPresenter implements FragmentManagerContract.presenter {
    FragmentManagerContract.view view;
    public FragmentManagerPresenter(FragmentManagerContract.view view){
        this.view = view;
    }

    @Override
    public void start() {
        view.findView();
        view.setViewClick();
        view.setScanner();
        ItemSingleton.getInstance().loadFromDB();
        DepartmentSingleton.getInstance().loadFromDB();
        AgentSingleton.getInstance().loadFromDB();
        LocationSingleton.getInstance().loadFromDB();
        loadFirstPage();
    }

    private void loadFirstPage(){
        if(ItemSingleton.getInstance().getItemList().size() > 0){
            view.loadItemListFragment();
        }else{
            view.loadFileFragment();
        }
    }
}
