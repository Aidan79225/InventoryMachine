package com.aidan.inventoryworkplatform.Entity;

import com.aidan.inventoryworkplatform.Dialog.SearchableItem;

/**
 * Created by Aidan on 2017/11/19.
 */

public abstract class SelectableItem implements Comparable<SelectableItem>, SearchableItem {
    public long id = 0;
    public String number ="";
    public String name = "";

    public Type type = Type.property;

    public enum Type implements SearchableItem{
        property("財產"),
        item("物品");
        private String name = "";
        Type(String name){
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public int compareTo(SelectableItem o) {
        return name.compareTo(o.name) ;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


}
