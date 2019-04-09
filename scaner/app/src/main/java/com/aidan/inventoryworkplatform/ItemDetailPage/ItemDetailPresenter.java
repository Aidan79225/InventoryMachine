package com.aidan.inventoryworkplatform.ItemDetailPage;

import android.text.TextUtils;

import com.aidan.inventoryworkplatform.Database.ItemDAO;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.TagContent;
import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;
import com.aidan.inventoryworkplatform.SettingConstants;
import com.aidan.inventoryworkplatform.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s352431 on 2016/11/22.
 */
public class ItemDetailPresenter implements ItemDetailContract.presenter {
    private ItemDetailContract.view view;
    private ItemDetailModel model;
    private String[] deleteStrings ={"Y","N"};
    private String[] printStrings ={"Y","N"};
    private String[] tagContentStrings;
    public ItemDetailPresenter(ItemDetailContract.view view,Item item){
        this.view = view;
        model = new ItemDetailModel(item);
        String tagContent = Singleton.preferences.getString(SettingConstants.ITEM_TAG_TYPE, "");
        if (!TextUtils.isEmpty(tagContent)) {
            model.getItem().setTagContent(TagContent.valueOf(tagContent));
        }
        init();
    }
    private void init(){
        tagContentStrings = new String[TagContent.values().length];
        for(int i = 0 ;i < TagContent.values().length ; i++){
            tagContentStrings[i] = TagContent.values()[i].getName();
        }
    }

    @Override
    public void start() {
        view.findView();
        view.setViewValue(model.getItem());
        view.setViewClick();
    }

    @Override
    public void saveItemToChecked(boolean flag) {
        Item item = model.getItem();
        item.setConfirm(flag);
        try {
            ItemDAO.getInstance().update(item);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void locationTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( LocationSingleton.getInstance().getLocationList(model.getItem().getItemType()));
        view.showSetDialog(item -> {
            model.getItem().setLocation((Location)item);
            view.setViewValue(model.getItem());
        },"地點列表",temp);
    }
    @Override
    public void departmentTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList(model.getItem().getItemType()));
        view.showSetDialog(item -> {
            model.getItem().setCustodyGroup((Department) item);
            view.setViewValue(model.getItem());
        },"保管部門列表",temp);
    }
    @Override
    public void agentTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList(model.getItem().getItemType()));
        view.showSetDialog(item -> {
            model.getItem().setCustodian((Agent) item);
            view.setViewValue(model.getItem());
        },"保管人列表",temp);
    }

    @Override
    public void useGroupTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList(model.getItem().getItemType()));
        view.showSetDialog(item -> {
            model.getItem().setUseGroup((Department) item);
            view.setViewValue(model.getItem());
        },"使用部門列表",temp);
    }

    @Override
    public void userTextViewClick() {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList(model.getItem().getItemType()));
        view.showSetDialog(item -> {
            model.getItem().setUser((Agent) item);
            view.setViewValue(model.getItem());
        },"使用人列表",temp);
    }

    @Override
    public void deleteTextViewClick() {
        view.showSetDialog((dialog, position) -> {
            model.getItem().setDelete(deleteStrings[position]);
            view.setViewValue(model.getItem());
        },"報廢",deleteStrings);
    }

    @Override
    public void printTextViewClick() {
        view.showSetDialog((dialog, position) -> {
            model.getItem().setPrint(printStrings[position]);
            view.setViewValue(model.getItem());
        },"補印",printStrings);
    }

    @Override
    public void printButtonClick() {
        view.showPrintDialog(model.getItem());
    }

    @Override
    public void printLittleButtonClick() {
        view.showLittlePrintDialog(model.getItem());
    }

    @Override
    public void tagContentTextViewClick() {
        view.showSetDialog((dialog, position) -> {
            TagContent tagContent = TagContent.values()[position];
            model.getItem().setTagContent(tagContent);
            view.setViewValue(model.getItem());
            Singleton.preferenceEditor.putString(SettingConstants.ITEM_TAG_TYPE, tagContent.toString()).commit();
        },"標籤內容", tagContentStrings);
    }
}
