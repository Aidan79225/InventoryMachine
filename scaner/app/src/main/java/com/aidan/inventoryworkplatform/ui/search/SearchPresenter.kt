package com.aidan.inventoryworkplatform.ui.search

import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.Model.ItemSingleton
import com.aidan.inventoryworkplatform.Model.SingleLiveEvent
import com.aidan.inventoryworkplatform.base.BaseViewModel
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchPresenter: BaseViewModel() {
    val minCalendar: Calendar = Calendar.getInstance()
    val maxCalendar: Calendar = Calendar.getInstance()
    val searchResultLiveEvent = SingleLiveEvent<List<Item>>()

    fun searchTextViewClick(name: String, number: String, serialMinNumber: String, serialMaxNumber: String) {
        val minSerialNumber = if (serialMinNumber.length > 0) Integer.valueOf(serialMinNumber) else 0
        val maxSerialNumber = if (serialMaxNumber.length > 0) Integer.valueOf(serialMaxNumber) else Int.MAX_VALUE
        searchResultLiveEvent.value = getItemListWithCondition(name, number, minSerialNumber, maxSerialNumber)
    }

    fun getItemListWithCondition(name: String?, number: String, minSerialNumber: Int, maxSerialNumber: Int): List<Item> {
        val itemList: MutableList<Item> = ArrayList()
        val minDate = minCalendar.time
        val maxDate = maxCalendar.time
        for (item in ItemSingleton.getInstance().itemList) {
            if (!item.name.contains(name!!)) {
                continue
            }
            if (number.length > 1 && item.number != number) {
                continue
            }
            val serialNumber = Integer.valueOf(item.serialNumber.substring(2))
            if (serialNumber < minSerialNumber || serialNumber > maxSerialNumber) {
                continue
            }
            if (minDate.time > item.date.time) {
                continue
            }
            if (maxDate.time < item.date.time) {
                continue
            }
            itemList.add(item)
        }
        return itemList
    }

    fun clearAll() {
        minCalendar.time = Calendar.getInstance().time
        maxCalendar.time = Calendar.getInstance().time
        minCalendar[Calendar.HOUR_OF_DAY] = 0
        minCalendar[Calendar.MINUTE] = 0
        maxCalendar[Calendar.HOUR_OF_DAY] = 23
        maxCalendar[Calendar.MINUTE] = 59
    }

    init {
        minCalendar[Calendar.HOUR_OF_DAY] = 0
        minCalendar[Calendar.MINUTE] = 0
        maxCalendar[Calendar.HOUR_OF_DAY] = 23
        maxCalendar[Calendar.MINUTE] = 59
    }
}