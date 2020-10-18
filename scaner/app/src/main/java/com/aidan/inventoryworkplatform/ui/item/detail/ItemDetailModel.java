package com.aidan.inventoryworkplatform.ui.item.detail;

import com.aidan.inventoryworkplatform.Entity.Item;

/**
 * Created by s352431 on 2016/11/22.
 */
public class ItemDetailModel {
    Item item;
    public ItemDetailModel(Item item){
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
