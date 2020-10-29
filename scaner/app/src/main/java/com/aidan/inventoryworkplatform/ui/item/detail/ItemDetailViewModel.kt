package com.aidan.inventoryworkplatform.ui.item.detail

import android.text.TextUtils
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.Agent
import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.Entity.TagContent
import com.aidan.inventoryworkplatform.Model.AgentSingleton
import com.aidan.inventoryworkplatform.Model.SingleLiveEvent
import com.aidan.inventoryworkplatform.SettingConstants
import com.aidan.inventoryworkplatform.Singleton
import com.aidan.inventoryworkplatform.base.BaseViewModel
import com.aidan.inventoryworkplatform.database.ItemDAO
import java.util.*

/**
 * Created by s352431 on 2016/11/22.
 */
class ItemDetailViewModel : BaseViewModel() {
    private val deleteStrings = arrayOf("Y", "N")
    private val printStrings = arrayOf("Y", "N")
    private val tagContentStrings: List<String>
    val notify = SingleLiveEvent<Void>()
    val showSetDialog = SingleLiveEvent<ShowSetDialogEvent<SearchableItem>>()
    val showSetDialogString = SingleLiveEvent<ShowSetDialogEvent<String>>()
    lateinit var item: Item


    fun saveItemToChecked(flag: Boolean) {
        item.isConfirm = flag
        try {
            ItemDAO.getInstance().update(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            ItemDAO.getInstance().update(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun userTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(AgentSingleton.getInstance().getAgentList(item.itemType))
        showSetDialog.value = ShowSetDialogEvent({ item: SearchableItem? ->
            this.item.user = item as Agent?
            notify.call()
        }, "使用人列表", temp)
    }

    fun deleteTextViewClick() {
        showSetDialogString.value = ShowSetDialogEvent({ item ->
            this.item.setDelete(item)
            notify.call()
        }, "報廢", deleteStrings.toList())
    }

    fun printTextViewClick() {
        showSetDialogString.value = ShowSetDialogEvent({ item ->
            this.item.setPrint(item)
            notify.call()
        }, "補印", printStrings.toList())
    }

    fun tagContentTextViewClick() {
        showSetDialogString.value = ShowSetDialogEvent({ item ->
            val tagContent = TagContent.values().find { it.getName() == item }
            this.item.tagContent = tagContent
            notify.call()
            Singleton.preferenceEditor.putString(SettingConstants.ITEM_TAG_TYPE, tagContent.toString()).commit()
        }, "標籤內容", tagContentStrings)
    }

    init {
        val tagContent = Singleton.preferences.getString(SettingConstants.ITEM_TAG_TYPE, "")
        if (!TextUtils.isEmpty(tagContent)) {
            item.tagContent = TagContent.valueOf(tagContent!!)
        }
        tagContentStrings = mutableListOf<String>().apply {
            for (i in TagContent.values().indices) {
                add(TagContent.values()[i].getName())
            }
        }
    }

    data class ShowSetDialogEvent<T: Any>(
            val clickListener: (T) -> Unit,
            val title: String,
            val data: List<T>
    )
}