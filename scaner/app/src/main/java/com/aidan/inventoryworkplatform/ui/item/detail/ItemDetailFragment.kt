package com.aidan.inventoryworkplatform.ui.item.detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.aidan.inventoryworkplatform.Dialog.SearchItemAdapter
import com.aidan.inventoryworkplatform.Dialog.SearchItemDialog
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.ItemListPage.ItemListFragment.RefreshItems
import com.aidan.inventoryworkplatform.KeyConstants
import com.aidan.inventoryworkplatform.Printer.PrintItemLittleTagDialog
import com.aidan.inventoryworkplatform.Printer.PrinterItemDialog
import com.aidan.inventoryworkplatform.R
import kotlinx.android.synthetic.main.fragment_item_detail.view.*
import java.io.File

/**
 * Created by s352431 on 2016/11/22.
 */
class ItemDetailFragment : DialogFragment(), ItemDetailContract.view {
    var presenter: ItemDetailContract.presenter? = null
    var refreshItems: RefreshItems? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        presenter!!.start()
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun setViewValue(item: Item) {
        view?.run {
            yearsTextView.text = item.years
            buyDateTextView!!.text = item.ADtoCal() + ", " + item.purchaseDate
            brandTextView!!.text = item.brand
            typeTextView!!.text = item.type
            custodianTextView!!.text = item.custodian.name
            custodyGroupTextView!!.text = item.custodyGroup.name
            userTextView!!.text = item.user.name
            useGroupTextView!!.text = item.useGroup.name
            locationTextView!!.text = item.location.name
            nameTextView!!.text = item.name
            nickNameTextView!!.text = item.nickName
            itemIdTextView!!.text = item.number
            deleteTextView!!.text = if (item.isDelete) "Y" else "N"
            printTextView!!.text = if (item.isPrint) "Y" else "N"
            if (item.tagContent != null) {
                tagContentTextView!!.text = item.tagContent.getName()
            }
            printButton!!.visibility = if (KeyConstants.showPrint) View.VISIBLE else View.GONE
            printLittleButton!!.visibility = if (KeyConstants.showPrintLittleTag) View.VISIBLE else View.GONE
            photoButton!!.setOnClickListener { v: View? -> startPhotoActivity(item.number.replace("-", "")) }
        }
    }

    private fun startPhotoActivity(fileName: String) {
        val dir = Environment.getExternalStorageDirectory().absolutePath + "/欣華盤點系統/照片"
        val dirFile = File(dir)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        var count = 1
        var file = File(dir, "$fileName$count.jpg")
        while (file.exists()) {
            count++
            file = File(dir, "$fileName$count.jpg")
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context!!, context!!.packageName + ".fileprovider", file))
        startActivity(intent)
    }

    override fun setViewClick() {
        confirmButton!!.setOnClickListener { v: View? ->
            presenter!!.saveItemToChecked(true)
            refreshItems!!.refresh()
            dismiss()
        }
        cancelButton!!.setOnClickListener { v: View? ->
            presenter!!.saveItemToChecked(false)
            refreshItems!!.refresh()
            dismiss()
        }
        locationTextView!!.setOnClickListener { v: View? -> presenter!!.locationTextViewClick() }
        custodianTextView!!.setOnClickListener { v: View? -> presenter!!.agentTextViewClick() }
        custodyGroupTextView!!.setOnClickListener { v: View? -> presenter!!.departmentTextViewClick() }
        userTextView!!.setOnClickListener { v: View? -> presenter!!.userTextViewClick() }
        useGroupTextView!!.setOnClickListener { v: View? -> presenter!!.useGroupTextViewClick() }
        deleteTextView!!.setOnClickListener { v: View? -> presenter!!.deleteTextViewClick() }
        printTextView!!.setOnClickListener { v: View? -> presenter!!.printTextViewClick() }
        printButton!!.setOnClickListener { v: View? -> presenter!!.printButtonClick() }
        printLittleButton!!.setOnClickListener { v: View? -> presenter!!.printLittleButtonClick() }
        tagContentTextView!!.setOnClickListener { v: View? -> presenter!!.tagContentTextViewClick() }
    }

    override fun showSetDialog(clickListener: DialogInterface.OnClickListener, title: String, temp: Array<String>) {
        val dialog = AlertDialog.Builder(activity!!)
        dialog.setTitle(title)
        dialog.setItems(temp, clickListener)
        dialog.create().show()
    }

    override fun showSetDialog(clickListener: SearchItemAdapter.OnClickListener, title: String, dataList: List<SearchableItem>) {
        val dialog = SearchItemDialog(activity, dataList)
        dialog.setTitle(title)
        dialog.setOnClickListener(clickListener)
        dialog.show()
    }

    override fun showPrintDialog(item: Item) {
        val dialog = PrinterItemDialog(activity!!)
        dialog.setItem(item)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun showLittlePrintDialog(item: Item) {
        val dialog = PrintItemLittleTagDialog(activity!!)
        dialog.setItem(item)
        dialog.setCancelable(false)
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Item?, refreshItems: RefreshItems?): ItemDetailFragment {
            val fragment = ItemDetailFragment()
            fragment.presenter = ItemDetailPresenter(fragment, item)
            fragment.refreshItems = refreshItems
            return fragment
        }
    }
}