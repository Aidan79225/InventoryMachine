package com.aidan.inventoryworkplatform.ui.search

import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.Model.ItemSingleton
import com.aidan.inventoryworkplatform.Model.SingleLiveEvent
import com.aidan.inventoryworkplatform.base.BaseViewModel
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchViewModel : BaseViewModel() {
    val minCalendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = 0
    }
    val maxCalendar: Calendar = Calendar.getInstance()
    val searchResultLiveEvent = SingleLiveEvent<List<Item>>()

    data class Number(
            val c1: Int,
            val c2: Int,
            val c3: Int,
            val c4: Int
    )

    fun searchTextViewClick(
            start: Number?,
            end: Number?,
            name: String,
            stockType: String,
            eventNumber: String,
            eventReason: String,
            user: String,
            location: String,
            note: String
    ) {
        searchResultLiveEvent.value = getItemListWithCondition(start, end, name, stockType, eventNumber, eventReason, user, location, note)
    }

    private fun getItemListWithCondition(
            start: Number?,
            end: Number?,
            name: String,
            stockType: String,
            eventNumber: String,
            eventReason: String,
            user: String,
            location: String,
            note: String): List<Item> {
        val itemList: MutableList<Item> = ArrayList()
        val minDate = minCalendar.time
        val maxDate = maxCalendar.time

        for (item in ItemSingleton.getInstance().itemList) {

            if (name.isNotEmpty() && !item.PA3P3.contains(name)) {
                continue
            }

            if (stockType.isNotEmpty() && !item.PA3PS.contains(stockType)) {
                continue
            }

            if (eventNumber.isNotEmpty() && !item.PA3MK.contains(eventNumber)) {
                continue
            }

            if (eventReason.isNotEmpty() && !item.PA3PY.contains(eventReason)) {
                continue
            }

            if (user.isNotEmpty() && !item.PA3LOC.contains(user)) {
                continue
            }

            if (location.isNotEmpty() && !item.PA3LOCN.contains(location)) {
                continue
            }

            if (note.isNotEmpty() && !item.PA3UUTN.contains(note)) {
                continue
            }

            val i1 = Integer.valueOf(item.PA3C1)
            val i2 = Integer.valueOf(item.PA3C2)
            val i3 = Integer.valueOf(item.PA3C3)
            val i4 = Integer.valueOf(item.PA3C4)
            if (start != null) {
                if (i1 < start.c1) {
                    continue
                }

                if (i2 < start.c2) {
                    continue
                }

                if (i3 < start.c3) {
                    continue
                }

                if (i1 == start.c1 && i2 == start.c2 && i3 == start.c3 && i4 < start.c4) {
                    continue
                }
            }

            if (end != null) {
                if (i1 > end.c1) {
                    continue
                }

                if (i2 > end.c2) {
                    continue
                }

                if (i3 > end.c3) {
                    continue
                }
                if (i1 == end.c1 && i2 == end.c2 && i3 == end.c3 && i4 > end.c4) {
                    continue
                }
            }

            val date = item.date
            if (date != null && minDate.time > date.time) {
                continue
            }
            if (date != null && maxDate.time < date.time) {
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