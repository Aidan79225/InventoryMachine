package com.aidan.inventoryworkplatform.Model;

import com.aidan.inventoryworkplatform.Database.DBHelper;
import com.aidan.inventoryworkplatform.Database.LocationDAO;
import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Entity.SelectableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2016/11/20.
 */

public class LocationSingleton {
    static LocationSingleton locationSingleton;
    private List<Location> locationList = new ArrayList<>();
    public static LocationSingleton getInstance(){
        if(locationSingleton == null){
            synchronized (LocationSingleton.class){
                if(locationSingleton == null){
                    locationSingleton = new LocationSingleton();
                }
            }
        }
        return locationSingleton;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public List<Location> getLocationList(SelectableItem.Type type) {
        List<Location> temp = new ArrayList<>();
        for (Location location : locationList) {
            if (location.getType() == type) {
                temp.add(location);
            }
        }
        return temp;
    }


    public void saveToDB() {
        try {
            DBHelper.getDatabase().beginTransaction();
            for (Location location : locationList) {
                saveLocation(location);
            }
            DBHelper.getDatabase().setTransactionSuccessful();
            DBHelper.getDatabase().endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveLocation(Location location){
        if (!LocationDAO.getInstance().update(location))
            LocationDAO.getInstance().insert(location);
    }
    public void loadFromDB(){
        locationList = LocationDAO.getInstance().getAll();
    }
}
