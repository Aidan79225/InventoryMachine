package com.aidan.inventoryworkplatform.Entity;

import com.aidan.inventoryworkplatform.KeyConstants;
import com.aidan.inventoryworkplatform.Model.ItemSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aidan on 2016/10/24.
 */

public class Item {
    private static final String PurchaseDateKey = "purchaseDate";
    private long id = 0;
    private String PA341 = "PA341";
    private String PA342 = "PA342";
    private String PA343 = "PA343";
    private String PA3C1 = "PA3C1";
    private String PA3C2 = "PA3C2";
    private String PA3C3 = "PA3C3";
    private String PA3C4 = "PA3C4";
    private String PA3C5 = "PA3C5";
    private String PA3C6 = "PA3C6";
    private String PA3P3 = "PA3P3";
    private String PA3PS = "PA3PS";
    private String PA3MK = "PA3MK";
    private String PA3BD = "PA3BD"; // 取得日期
    private String purchaseDate = ""; //購置日期
    private String PA3PY = "PA3PY";
    private String PA3LOC = "PA3LOC";
    private String PA3LOCN = "PA3LOCN";
    private String PA3OUT = "PA3OUT";
    private String PA3OUTN = "PA3OUTN";
    private String PA3OU = "PA3OU";
    private String PA3OUN = "PA3OUN";
    private String PA3UUT = "PA3UUT";
    private String PA3UUTN = "PA3UUTN";
    private String PA3UR = "PA3UR";
    private String PA3URN = "PA3URN";
    private String PA308 = "PA308";
    private String PA3DEL = "PA3DEL";
    private String PA3PRN = "PA3PRN";
    private String NAME = "";
    private TagContent tagContent = null;
    private SelectableItem.Type type = SelectableItem.Type.property;

    public Item() {

    }

    public Item(JSONObject jsonObject) {
        try {
            PA341 = jsonObject.getString(ItemConstants.PA341);
            PA342 = jsonObject.getString(ItemConstants.PA342);
            PA343 = jsonObject.getString(ItemConstants.PA343);
            PA3C1 = jsonObject.getString(ItemConstants.PA3C1);
            PA3C2 = jsonObject.getString(ItemConstants.PA3C2);
            PA3C3 = jsonObject.getString(ItemConstants.PA3C3);
            PA3C4 = jsonObject.getString(ItemConstants.PA3C4);
            PA3C5 = jsonObject.getString(ItemConstants.PA3C5);
            PA3C6 = jsonObject.getString(ItemConstants.PA3C6);
            PA3P3 = jsonObject.getString(ItemConstants.PA3P3);
            PA3PS = jsonObject.getString(ItemConstants.PA3PS);
            PA3MK = jsonObject.getString(ItemConstants.PA3MK);
            PA3BD = jsonObject.getString(ItemConstants.PA3BD);
            PA3PY = jsonObject.getString(ItemConstants.PA3PY);
            PA3LOC = jsonObject.getString(ItemConstants.PA3LOC);
            PA3LOCN = jsonObject.getString(ItemConstants.PA3LOCN);
            PA3OUT = jsonObject.getString(ItemConstants.PA3OUT);
            PA3OUTN = jsonObject.getString(ItemConstants.PA3OUTN);
            PA3OU = jsonObject.getString(ItemConstants.PA3OU);
            PA3OUN = jsonObject.getString(ItemConstants.PA3OUN);
            PA3UUT = jsonObject.getString(ItemConstants.PA3UUT);
            PA3UUTN = jsonObject.getString(ItemConstants.PA3UUTN);
            PA3UR = jsonObject.getString(ItemConstants.PA3UR);
            PA3URN = jsonObject.getString(ItemConstants.PA3URN);
            PA308 = jsonObject.getString(ItemConstants.PA308);
            PA3DEL = jsonObject.getString(ItemConstants.PA3DEL);
            PA3PRN = jsonObject.getString(ItemConstants.PA3PRN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            type = SelectableItem.Type.valueOf(jsonObject.getString(ItemConstants.TYPE));
        } catch (Exception e){ }
        try {
            purchaseDate = jsonObject.getString(PurchaseDateKey);
        } catch (Exception e){ }
    }

    public void setData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            PA341 = jsonObject.getString(ItemConstants.PA341);
            PA342 = jsonObject.getString(ItemConstants.PA342);
            PA343 = jsonObject.getString(ItemConstants.PA343);
            PA3C1 = jsonObject.getString(ItemConstants.PA3C1);
            PA3C2 = jsonObject.getString(ItemConstants.PA3C2);
            PA3C3 = jsonObject.getString(ItemConstants.PA3C3);
            PA3C4 = jsonObject.getString(ItemConstants.PA3C4);
            PA3C5 = jsonObject.getString(ItemConstants.PA3C5);
            PA3C6 = jsonObject.getString(ItemConstants.PA3C6);
            PA3P3 = jsonObject.getString(ItemConstants.PA3P3);
            PA3PS = jsonObject.getString(ItemConstants.PA3PS);
            PA3MK = jsonObject.getString(ItemConstants.PA3MK);
            PA3BD = jsonObject.getString(ItemConstants.PA3BD);
            PA3PY = jsonObject.getString(ItemConstants.PA3PY);
            PA3LOC = jsonObject.getString(ItemConstants.PA3LOC);
            PA3LOCN = jsonObject.getString(ItemConstants.PA3LOCN);
            PA3OUT = jsonObject.getString(ItemConstants.PA3OUT);
            PA3OUTN = jsonObject.getString(ItemConstants.PA3OUTN);
            PA3OU = jsonObject.getString(ItemConstants.PA3OU);
            PA3OUN = jsonObject.getString(ItemConstants.PA3OUN);
            PA3UUT = jsonObject.getString(ItemConstants.PA3UUT);
            PA3UUTN = jsonObject.getString(ItemConstants.PA3UUTN);
            PA3UR = jsonObject.getString(ItemConstants.PA3UR);
            PA3URN = jsonObject.getString(ItemConstants.PA3URN);
            PA308 = jsonObject.getString(ItemConstants.PA308);
            PA3DEL = jsonObject.getString(ItemConstants.PA3DEL);
            PA3PRN = jsonObject.getString(ItemConstants.PA3PRN);
            NAME = jsonObject.getString(ItemConstants.NAME);
            try {
                type = SelectableItem.Type.valueOf(jsonObject.getString(ItemConstants.TYPE));
            } catch (Exception e){ }
            try {
                purchaseDate = jsonObject.getString(PurchaseDateKey);
            } catch (Exception e){ }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ItemConstants.PA341, PA341);
            jsonObject.put(ItemConstants.PA342, PA342);
            jsonObject.put(ItemConstants.PA343, PA343);
            jsonObject.put(ItemConstants.PA3C1, PA3C1);
            jsonObject.put(ItemConstants.PA3C2, PA3C2);
            jsonObject.put(ItemConstants.PA3C3, PA3C3);
            jsonObject.put(ItemConstants.PA3C4, PA3C4);
            jsonObject.put(ItemConstants.PA3C5, PA3C5);
            jsonObject.put(ItemConstants.PA3C6, PA3C6);
            jsonObject.put(ItemConstants.PA3P3, PA3P3);
            jsonObject.put(ItemConstants.PA3PS, PA3PS);
            jsonObject.put(ItemConstants.PA3MK, PA3MK);
            jsonObject.put(ItemConstants.PA3BD, PA3BD);
            jsonObject.put(ItemConstants.PA3PY, PA3PY);
            jsonObject.put(ItemConstants.PA3LOC, PA3LOC);
            jsonObject.put(ItemConstants.PA3LOCN, PA3LOCN);
            jsonObject.put(ItemConstants.PA3OUT, PA3OUT);
            jsonObject.put(ItemConstants.PA3OUTN, PA3OUTN);
            jsonObject.put(ItemConstants.PA3OU, PA3OU);
            jsonObject.put(ItemConstants.PA3OUN, PA3OUN);
            jsonObject.put(ItemConstants.PA3UUT, PA3UUT);
            jsonObject.put(ItemConstants.PA3UUTN, PA3UUTN);
            jsonObject.put(ItemConstants.PA3UR, PA3UR);
            jsonObject.put(ItemConstants.PA3URN, PA3URN);
            jsonObject.put(ItemConstants.PA308, PA308);
            jsonObject.put(ItemConstants.PA3DEL, PA3DEL);
            jsonObject.put(ItemConstants.PA3PRN, PA3PRN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject toDbJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ItemConstants.PA341, PA341);
            jsonObject.put(ItemConstants.PA342, PA342);
            jsonObject.put(ItemConstants.PA343, PA343);
            jsonObject.put(ItemConstants.PA3C1, PA3C1);
            jsonObject.put(ItemConstants.PA3C2, PA3C2);
            jsonObject.put(ItemConstants.PA3C3, PA3C3);
            jsonObject.put(ItemConstants.PA3C4, PA3C4);
            jsonObject.put(ItemConstants.PA3C5, PA3C5);
            jsonObject.put(ItemConstants.PA3C6, PA3C6);
            jsonObject.put(ItemConstants.PA3P3, PA3P3);
            jsonObject.put(ItemConstants.PA3PS, PA3PS);
            jsonObject.put(ItemConstants.PA3MK, PA3MK);
            jsonObject.put(ItemConstants.PA3BD, PA3BD);
            jsonObject.put(ItemConstants.PA3PY, PA3PY);
            jsonObject.put(ItemConstants.PA3LOC, PA3LOC);
            jsonObject.put(ItemConstants.PA3LOCN, PA3LOCN);
            jsonObject.put(ItemConstants.PA3OUT, PA3OUT);
            jsonObject.put(ItemConstants.PA3OUTN, PA3OUTN);
            jsonObject.put(ItemConstants.PA3OU, PA3OU);
            jsonObject.put(ItemConstants.PA3OUN, PA3OUN);
            jsonObject.put(ItemConstants.PA3UUT, PA3UUT);
            jsonObject.put(ItemConstants.PA3UUTN, PA3UUTN);
            jsonObject.put(ItemConstants.PA3UR, PA3UR);
            jsonObject.put(ItemConstants.PA3URN, PA3URN);
            jsonObject.put(ItemConstants.PA308, PA308);
            jsonObject.put(ItemConstants.PA3DEL, PA3DEL);
            jsonObject.put(ItemConstants.PA3PRN, PA3PRN);
            jsonObject.put(ItemConstants.NAME, NAME);
            jsonObject.put(ItemConstants.TYPE, type);
            jsonObject.put(PurchaseDateKey, purchaseDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getLoaclNumber() {
        return PA341 + PA342 + PA343;
    }

    public String getPA3C1() {
        return PA3C1;
    }

    public String getNumber() {
        return PA3C1 + PA3C2 + PA3C3 + PA3C4 + PA3C5;
    }

    public String getSerialNumber() {
        return PA3C6;
    }

    public String getIdNumber() {
        return getNumber() + "-" + getSerialNumber();
    }

    public String getTagIdNumber() {
        return PA3C1 + PA3C2 + PA3C3 + PA3C4 + "-" + PA3C5 + "-" + getSerialNumber();
    }

    public String getBarcodeNumber() {
        return "110-" + getNumber() + "-00" + getSerialNumber();
    }

    public String getName() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getNickName() {
        return PA3P3;
    }

    public String getType() {
        return PA3PS;
    }

    public String getBrand() {
        return PA3MK;
    }

    public Date getDate() {
        String PA3BD = this.PA3BD.replace("/", "");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(PA3BD.substring(0, PA3BD.length() - 4)), Integer.valueOf(PA3BD.substring(PA3BD.length() - 4, PA3BD.length() - 2)) - 1, Integer.valueOf(PA3BD.substring(PA3BD.length() - 2)));
        return c.getTime();
    }

    public String getPA3BD() {
        return PA3BD;
    }

    public String getYears() {
        return PA3PY;
    }


    public Agent getCustodian() {
        return new Agent(PA3OUN, PA3OU);
    }

    public void setCustodian(Agent agent) {
        PA3OUN = agent.name;
        PA3OU = agent.number;
        setConfirm(true);
    }

    public Agent getUser() {
        return new Agent(PA3URN, PA3UR);
    }

    public void setUser(Agent agent) {
        PA3URN = agent.name;
        PA3UR = agent.number;
        setConfirm(true);
    }

    public Department getCustodyGroup() {
        return new Department(PA3OUTN, PA3OUT);
    }

    public void setCustodyGroup(Department department) {
        PA3OUTN = department.name;
        PA3OUT = department.number;
        setConfirm(true);
    }

    public Department getUseGroup() {
        return new Department(PA3UUTN, PA3UUT);
    }

    public void setUseGroup(Department department) {
        PA3UUTN = department.name;
        PA3UUT = department.number;
        setConfirm(true);
    }

    public Location getLocation() {
        return new Location(PA3LOCN, PA3LOC);
    }

    public void setLocation(Location location) {
        PA3LOCN = location.name;
        PA3LOC = location.number;
        setConfirm(true);
    }

    public boolean isConfirm() {
        return PA308.equals("Y");
    }

    public void setConfirm(boolean flag) {
        if (flag) {
            PA308 = "Y";
            ItemSingleton.getInstance().saveItem(this);
        } else {
            PA308 = "N";
        }
    }

    public boolean isDelete() {
        return PA3DEL.equals("Y");
    }

    public void setDelete(boolean flag) {
        if (flag) {
            PA3DEL = "Y";
        } else {
            PA3DEL = "N";
        }
    }

    public void setDelete(String key) {
        PA3DEL = key;
        setConfirm(true);
    }

    public boolean isPrint() {
        return PA3PRN.equals("Y");
    }

    public void setPrint(boolean flag) {
        if (flag) {
            PA3PRN = "Y";
        } else {
            PA3PRN = "N";
        }
    }

    public void setPrint(String key) {
        PA3PRN = key;
        setConfirm(true);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getLittleTagContentString() {

        String ans = " ";
        ans += KeyConstants.LittleAuthorityName + (PA3C1.equals("6") ? KeyConstants.LittleItemName : "") + "\n";
        ans += " " + getTagIdNumber() + "\n";
        if(!getName().isEmpty()){
            ans += " " + getName() + "\n";
        }
        ans += " 取得:" + ADtoCal() + "  年限:" + getYears() + "\n";
        ans += " "+ getCustodian().getName() + "/" + getLocation().getName() ;
        return ans;
    }
    public String getTagContentString() {
        String ans = "  ";
        ans += KeyConstants.AuthorityName + (PA3C1.equals("6") ? KeyConstants.ItemName : "") + "\n";
        ans += "  財產區分別：110公務用_一般\n";
        ans += "  財產編號：" + getTagIdNumber() + "\n";
        ans += "  財產名稱：" + getName() + "\n";
        ans += "  財產別名：" + getNickName() + "\n";
        ans += "  取得/購置：" + ADtoCal() + ", " + purchaseDate +  "  年限：" + getYears() + "\n";
        if (tagContent != null) {
            ans += "  " + tagContent.getName() + "：";
            switch (tagContent) {
                case Agent:
                    ans += getCustodian().getName() + "\n";
                    break;
                case AgentGroup:
                    ans += getCustodian().getName() + "/" + getCustodyGroup().getName() + "\n";
                    break;
                case AgentLocation:
                    ans += getCustodian().getName() + "/" + getLocation().getName() + "\n";
                    break;
                case AgentGroupLocation:
                    ans += getCustodian().getName() + "/" + getCustodyGroup().getName() + "/" + getLocation().getName() + "\n";
                    break;
            }
        }

        ans += "  廠牌/型式：" + getBrand() + "/" + getType() + "\n";
        return ans;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String ADtoCal() {
        String temp = String.valueOf((Integer.parseInt(PA3BD.replace("/", "")) - 19110000));
        return temp.substring(0, temp.length() - 4) + "/" + temp.substring(temp.length() - 4, temp.length() - 2) + "/" + temp.substring(temp.length() - 2);
    }

    public void setTagContent(TagContent tagContent) {
        this.tagContent = tagContent;
    }

    public TagContent getTagContent() {
        return tagContent;
    }

    public SelectableItem.Type getItemType(){
        return type;
    }

    public void setItemType(SelectableItem.Type type) {
        this.type = type;
    }

}
