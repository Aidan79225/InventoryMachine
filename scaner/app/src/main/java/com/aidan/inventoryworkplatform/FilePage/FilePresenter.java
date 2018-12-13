package com.aidan.inventoryworkplatform.FilePage;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.aidan.inventoryworkplatform.Constants;
import com.aidan.inventoryworkplatform.Database.AgentDAO;
import com.aidan.inventoryworkplatform.Database.DepartmentDAO;
import com.aidan.inventoryworkplatform.Database.ItemDAO;
import com.aidan.inventoryworkplatform.Database.LocationDAO;
import com.aidan.inventoryworkplatform.Entity.Agent;
import com.aidan.inventoryworkplatform.Entity.Department;
import com.aidan.inventoryworkplatform.Entity.Item;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.SelectableItem;
import com.aidan.inventoryworkplatform.Model.AgentSingleton;
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;
import com.aidan.inventoryworkplatform.Model.LocationSingleton;
import com.aidan.inventoryworkplatform.ScannerPage.ScannerItemManager;
import com.aidan.inventoryworkplatform.Singleton;
import com.aidan.inventoryworkplatform.Utils.ReadExcel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.attr.borderlessButtonStyle;
import static android.R.attr.data;
import static android.R.attr.value;

/**
 * Created by Aidan on 2016/11/20.
 */

public class FilePresenter implements FileContract.presenter {
    FileContract.view view;

    public FilePresenter(FileContract.view view) {
        this.view = view;
    }

    @Override
    public void start() {
        view.findView();
        view.setViewClick();
    }

    @Override
    public void readTxtButtonClick(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> allowType = new HashSet<>();
                allowType.add("0");
                allowType.add("1");
                allowType.add("2");
                allowType.add("3");
                allowType.add("4");
                allowType.add("5");
                loadData(path, "讀取財產中", allowType, Constants.PREFERENCE_PROPERTY_KEY, SelectableItem.Type.property);
            }
        }).start();
    }

    private void loadData(String path, String msg, Set<String> allowType, String key, SelectableItem.Type type) {
        view.showProgress(msg);
        List<Item> itemList = ItemSingleton.getInstance().getItemList();
        List<Location> locationList = LocationSingleton.getInstance().getLocationList();
        List<Agent> agentList = AgentSingleton.getInstance().getAgentList();
        List<Department> departmentList = DepartmentSingleton.getInstance().getDepartmentList();
        try {
            File yourFile = new File(path);
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr = "";

            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jsonStr = Charset.forName("Big5").decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stream.close();

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject ASSETs = jsonObj.getJSONObject("ASSETs");
            JSONObject MS = jsonObj.getJSONObject(Constants.MS);
            Singleton.preferenceEditor.putString(key, MS.toString()).commit();
            getItems(ASSETs, itemList, allowType, type);
            getAgents(ASSETs, agentList, type);
            getDepartments(ASSETs, departmentList, type);
            getLocations(ASSETs, locationList, type);

            ItemSingleton.getInstance().saveToDB();
            DepartmentSingleton.getInstance().saveToDB();
            AgentSingleton.getInstance().saveToDB();
            LocationSingleton.getInstance().saveToDB();

        } catch (Exception e) {
            view.hideProgress();
            view.showToast("檔案格式錯誤");
            e.printStackTrace();
        }
    }


    @Override
    public void readNameTextViewClick(String path) {
        ReadExcel readExcel = new ReadExcel();
        readExcel.setProgressAction((ReadExcel.ProgressAction) view);
        readExcel.read(path);
    }

    private void dropTable() {
        try {
            ItemDAO.getInstance().dropTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DepartmentDAO.getInstance().dropTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            AgentDAO.getInstance().dropTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LocationDAO.getInstance().dropTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getItems(JSONObject ASSETs, List<Item> itemList, Set<String> allowType, SelectableItem.Type type) {
        try {
            JSONArray data = ASSETs.getJSONArray("PA3");
            Singleton.log("itemList size : " + data.length());
            int size = data.length();
            for (int i = 0; i < size; i++) {
                JSONObject c = data.getJSONObject(i);
                Item item = new Item(c);
                item.setItemType(type);
                if (allowType.contains(item.getPA3C1())) {
                    itemList.add(item);
                }
                view.updateProgress((i + 1) * 100 / size);
            }
            view.hideProgress();
            Singleton.log("itemList size : " + itemList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAgents(JSONObject ASSETs, List<Agent> agentList, SelectableItem.Type type) {
        try {
            JSONArray data = ASSETs.getJSONArray("D1");
            Singleton.log("agentList size : " + data.length());
            view.showProgress("讀取人名中");
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                Agent agent = new Agent(c);
                agent.setType(type);
                boolean isNotAdded = true;
                for(Agent mAgent : agentList){
                    if(agent.getName().equals(mAgent.getName()) && agent.getNumber().equals(mAgent.getNumber()) && agent.getType() == mAgent.getType()){
                        isNotAdded = false;
                        break;
                    }
                }
                if(isNotAdded){
                    agentList.add(agent);
                }
                view.updateProgress((i + 1) * 100 / data.length());
            }
            Singleton.log("agentList size : " + agentList.size());
            view.hideProgress();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(agentList);
    }

    private void getDepartments(JSONObject ASSETs, List<Department> departmentList, SelectableItem.Type type) {
        try {
            JSONArray data = ASSETs.getJSONArray("D2");
            Singleton.log("departmentList size : " + data.length());
            view.showProgress("讀取部門中");
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                Department department = new Department(c);
                department.setType(type);
                boolean isNotAdded = true;
                for(Department mDepartment : departmentList){
                    if(department.getName().equals(mDepartment.getName()) && department.getNumber().equals(mDepartment.getNumber()) && department.getType() == mDepartment.getType()){
                        isNotAdded = false;
                        break;
                    }
                }
                if(isNotAdded){
                    departmentList.add(department);
                }
                view.updateProgress((i + 1) * 100 / data.length());
            }
            Singleton.log("departmentList size : " + departmentList.size());
            view.hideProgress();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(departmentList);
    }

    private void getLocations(JSONObject ASSETs, List<Location> locationList, SelectableItem.Type type) {
        try {
            JSONArray data = ASSETs.getJSONArray("D3");
            Singleton.log("locationList size : " + data.length());
            view.showProgress("讀取地點中");
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                Location location = new Location(c);
                location.setType(type);
                boolean isNotAdded = true;
                for(Location mLocation : locationList){
                    if(location.getName().equals(mLocation.getName()) && location.getNumber().equals(mLocation.getNumber()) && location.getType() == mLocation.getType()){
                        isNotAdded = false;
                        break;
                    }
                }
                if(isNotAdded){
                    locationList.add(location);
                }
                view.updateProgress((i + 1) * 100 / data.length());
            }
            Singleton.log("locationList size : " + locationList.size());
            view.hideProgress();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(locationList);
    }

    @Override
    public void saveFile(String fileName, String preferencesKey, Set<String> allowType) {
        JSONObject jsonObject = getAllDataJSON(preferencesKey, allowType);
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/欣華盤點系統/行動裝置轉至電腦";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dir, fileName + ".txt");
        if (file.exists()) {
            file.delete();
            file = new File(dir, fileName + ".txt");
        }
        try {
            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "big5"));
            bufWriter.write(jsonObject.toString());
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Singleton.log(file.getAbsoluteFile() + "寫檔發生錯誤");
        }
    }

    @Override
    public void clearData() {
        List<Item> itemList = ItemSingleton.getInstance().getItemList();
        List<Location> locationList = LocationSingleton.getInstance().getLocationList();
        List<Agent> agentList = AgentSingleton.getInstance().getAgentList();
        List<Department> departmentList = DepartmentSingleton.getInstance().getDepartmentList();
        itemList.clear();
        locationList.clear();
        agentList.clear();
        departmentList.clear();
        ScannerItemManager.getInstance().getItemList().clear();
        dropTable();
    }

    @Override
    public void inputItemTextViewClick(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> allowType = new HashSet<>();
                allowType.add("6");
                loadData(path, "讀取物品中", allowType, Constants.PREFERENCE_ITEM_KEY, SelectableItem.Type.item);
            }
        }).start();
    }

    public JSONObject getAllDataJSON(String key, Set<String> allowType) {
        List<Item> itemList = ItemSingleton.getInstance().getItemList();
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject MS = new JSONObject(Singleton.preferences.getString(key, ""));
            jsonObject.put(Constants.MS, MS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject ASSETs = new JSONObject();
            JSONArray PA3 = new JSONArray();
            for (Item item : itemList) {
                if (allowType.contains(item.getPA3C1())) {
                    PA3.put(item.toJSON());
                }
            }
            JSONArray D1 = new JSONArray();
            JSONArray D2 = new JSONArray();
            JSONArray D3 = new JSONArray();
            ASSETs.put("D1", D1);
            ASSETs.put("D2", D2);
            ASSETs.put("D3", D3);
            ASSETs.put("PA3", PA3);
            jsonObject.put("ASSETs", ASSETs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
