package com.aidan.inventoryworkplatform.Entity;

import com.aidan.inventoryworkplatform.Dialog.SearchableItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aidan on 2016/10/25.
 */

public class Location extends SelectableItem{


    public Location() {

    }

    public Location(JSONObject jsonObject) {
        try {
            number = jsonObject.getString(LocationConstants.D3KY);
            name = jsonObject.getString(LocationConstants.D3NM);
            type = Type.valueOf(jsonObject.getString(ItemConstants.TYPE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            setData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(JSONObject jsonObject) {
        try {
            number = jsonObject.getString(LocationConstants.D3KY);
            name = jsonObject.getString(LocationConstants.D3NM);
            type = Type.valueOf(jsonObject.getString(ItemConstants.TYPE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(LocationConstants.D3KY, number);
            jsonObject.put(LocationConstants.D3NM, name);
            jsonObject.put(ItemConstants.TYPE, type.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public Location(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
