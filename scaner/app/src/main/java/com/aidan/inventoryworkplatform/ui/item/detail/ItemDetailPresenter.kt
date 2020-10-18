package com.aidan.inventoryworkplatform.ui.item.detail

import android.content.DialogInterface
import android.text.TextUtils
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.*
import com.aidan.inventoryworkplatform.Model.AgentSingleton
import com.aidan.inventoryworkplatform.Model.DepartmentSingleton
import com.aidan.inventoryworkplatform.Model.LocationSingleton
import com.aidan.inventoryworkplatform.SettingConstants
import com.aidan.inventoryworkplatform.Singleton
import com.aidan.inventoryworkplatform.database.ItemDAO
import java.util.*

/**
 * Created by s352431 on 2016/11/22.
 */
class ItemDetailPresenter(private val view: ItemDetailContract.view, item: Item?) : ItemDetailContract.presenter {
    private val model: ItemDetailModel = ItemDetailModel(item)
    private val deleteStrings = arrayOf("Y", "N")
    private val printStrings = arrayOf("Y", "N")
    private lateinit var tagContentStrings: List<String>
    private fun init() {
        tagContentStrings = mutableListOf<String>().apply {
            for (i in TagContent.values().indices) {
                add(TagContent.values()[i].getName())
            }
        }
    }

    override fun start() {
        view.setViewValue(model.getItem())
        view.setViewClick()
    }

    override fun saveItemToChecked(flag: Boolean) {
        val item = model.getItem()
        item.isConfirm = flag
        try {
            ItemDAO.getInstance().update(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun locationTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(LocationSingleton.getInstance().getLocationList(model.getItem().itemType))
        view.showSetDialog({ item: SearchableItem? ->
            model.getItem().location = item as Location?
            view.setViewValue(model.getItem())
        }, "地點列表", temp)
    }

    override fun departmentTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(DepartmentSingleton.getInstance().getDepartmentList(model.getItem().itemType))
        view.showSetDialog({ item: SearchableItem? ->
            model.getItem().custodyGroup = item as Department?
            view.setViewValue(model.getItem())
        }, "保管部門列表", temp)
    }

    override fun agentTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(AgentSingleton.getInstance().getAgentList(model.getItem().itemType))
        view.showSetDialog({ item: SearchableItem? ->
            model.getItem().custodian = item as Agent?
            view.setViewValue(model.getItem())
        }, "保管人列表", temp)
    }

    override fun useGroupTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(DepartmentSingleton.getInstance().getDepartmentList(model.getItem().itemType))
        view.showSetDialog({ item: SearchableItem? ->
            model.getItem().useGroup = item as Department?
            view.setViewValue(model.getItem())
        }, "使用部門列表", temp)
    }

    override fun userTextViewClick() {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(AgentSingleton.getInstance().getAgentList(model.getItem().itemType))
        view.showSetDialog({ item: SearchableItem? ->
            model.getItem().user = item as Agent?
            view.setViewValue(model.getItem())
        }, "使用人列表", temp)
    }

    override fun deleteTextViewClick() {
        view.showSetDialog({ dialog: DialogInterface?, position: Int ->
            model.getItem().setDelete(deleteStrings[position])
            view.setViewValue(model.getItem())
        }, "報廢", deleteStrings)
    }

    override fun printTextViewClick() {
        view.showSetDialog({ dialog: DialogInterface?, position: Int ->
            model.getItem().setPrint(printStrings[position])
            view.setViewValue(model.getItem())
        }, "補印", printStrings)
    }

    override fun printButtonClick() {
        view.showPrintDialog(model.getItem())
    }

    override fun printLittleButtonClick() {
        view.showLittlePrintDialog(model.getItem())
    }

    override fun tagContentTextViewClick() {
        view.showSetDialog({ dialog: DialogInterface?, position: Int ->
            val tagContent = TagContent.values()[position]
            model.getItem().tagContent = tagContent
            view.setViewValue(model.getItem())
            Singleton.preferenceEditor.putString(SettingConstants.ITEM_TAG_TYPE, tagContent.toString()).commit()
        }, "標籤內容", tagContentStrings)
    }

    init {
        val tagContent = Singleton.preferences.getString(SettingConstants.ITEM_TAG_TYPE, "")
        if (!TextUtils.isEmpty(tagContent)) {
            model.getItem().tagContent = TagContent.valueOf(tagContent!!)
        }
        init()
    }
}