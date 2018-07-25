package com.aidan.inventoryworkplatform.ItemDetailPage;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.aidan.inventoryworkplatform.Database.ItemDAO;
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter;
import com.aidan.inventoryworkplatform.Dialog.SearchableItem;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.TagContent;
import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.BarcodeMatrix;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by s352431 on 2016/11/22.
 */
public class ItemDetailPresenter implements ItemDetailContract.presenter {
    private ItemDetailContract.view view;
    private ItemDetailModel model;
    private String[] locationStrings ={};
    private String[] agentStrings ={};
    private String[] departmentStrings ={};
    private String[] deleteStrings ={"Y","N"};
    private String[] printStrings ={"Y","N"};
    private String[] tagContentStrings;
    public ItemDetailPresenter(ItemDetailContract.view view,Item item){
        this.view = view;
        model = new ItemDetailModel(item);
        init();
    }
    private void init(){
        List<Location> locationList = LocationSingleton.getInstance().getLocationList();
        List<String> locationStringList = new ArrayList<>();
        for(Location location : locationList ){
            locationStringList.add(location.name);
        }
        locationStrings = locationStringList.toArray(locationStrings);

        List<Agent> agentList = AgentSingleton.getInstance().getAgentList();
        List<String> agentStringList = new ArrayList<>();
        for(Agent agent : agentList ){
            agentStringList.add(agent.name);
        }
        agentStrings = agentStringList.toArray(agentStrings);


        List<Department> departmentList = DepartmentSingleton.getInstance().getDepartmentList();
        List<String> departmentStringList = new ArrayList<>();
        for(Department department : departmentList ){
            departmentStringList.add(department.name);
        }
        departmentStrings = departmentStringList.toArray(departmentStrings);

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
        temp.addAll( LocationSingleton.getInstance().getLocationList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                model.getItem().setLocation((Location)item);
                view.setViewValue(model.getItem());
            }
        },"地點列表",temp);
    }
    @Override
    public void departmentTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                model.getItem().setCustodyGroup((Department) item);
                view.setViewValue(model.getItem());
            }
        },"保管部門列表",temp);
    }
    @Override
    public void agentTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                model.getItem().setCustodian((Agent) item);
                view.setViewValue(model.getItem());
            }
        },"保管人列表",temp);
    }

    @Override
    public void useGroupTextViewClick(){
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( DepartmentSingleton.getInstance().getDepartmentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                model.getItem().setUseGroup((Department) item);
                view.setViewValue(model.getItem());
            }
        },"使用部門列表",temp);
    }

    @Override
    public void userTextViewClick() {
        List<SearchableItem> temp = new ArrayList<>();
        temp.addAll( AgentSingleton.getInstance().getAgentList());
        view.showSetDialog(new SearchItemAdapter.OnClickListener() {
            @Override
            public void onClick(SearchableItem item) {
                model.getItem().setUser((Agent) item);
                view.setViewValue(model.getItem());
            }
        },"使用人列表",temp);
    }

    @Override
    public void deleteTextViewClick() {
        view.showSetDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                model.getItem().setDelete(deleteStrings[position]);
                view.setViewValue(model.getItem());
            }
        },"報廢",deleteStrings);
    }

    @Override
    public void printTextViewClick() {
        view.showSetDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                model.getItem().setPrint(printStrings[position]);
                view.setViewValue(model.getItem());
            }
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
        view.showSetDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                model.getItem().setTagContent(TagContent.values()[position]);
                view.setViewValue(model.getItem());
            }
        },"標籤內容", tagContentStrings);
    }
}
