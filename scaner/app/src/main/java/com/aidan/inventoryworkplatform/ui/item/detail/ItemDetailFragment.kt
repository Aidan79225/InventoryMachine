package com.aidan.inventoryworkplatform.ui.item.detail

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
class ItemDetailFragment : DialogFragment() {
    lateinit var presenter: ItemDetailPresenter

    var refreshItems: RefreshItems? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ViewModelProviders.of(requireActivity()).get(ItemDetailPresenter::class.java)
        presenter.apply {
            setViewValue(view, item)
            setViewClick(view)
            showSetDialog.observe(this@ItemDetailFragment, Observer {
                showSetDialog1(it.clickListener, it.title, it.data)
            })
            showSetDialogString.observe(this@ItemDetailFragment, Observer {
                showSetDialog(it.clickListener, it.title, it.data)
            })
        }
    }

    private fun setViewValue(view: View, item: Item) {
        view.run {
            dateTextView.text = item.pA3BD
            stockTypeTextView.text = item.PA3PS
            eventNumberTextView.text = item.PA3MK
            eventReasonTextView.text = item.PA3PY
            userTextView.text = item.PA3LOC
            moveDepartmentTextView.text = item.PA3LOCN
            brandTextView.text = item.PA3OUT
            receiptTextView.text = item.PA3OUTN
            processDateTextView.text = item.PA3OU
            processNumberTextView.text = item.PA3OUN
            processContentTextView.text = item.PA3UUT
            noteTextView.text = item.PA3UUTN
            resultEditText.setText(item.PA3UR)
            nickNameTextView.text = item.PA3P3
            itemIdTextView.text = item.number


            deleteTextView.text = if (item.isDelete) "Y" else "N"
            printTextView.text = if (item.isPrint) "Y" else "N"
            if (item.tagContent != null) {
                tagContentTextView!!.text = item.tagContent.getName()
            }
            printButton.visibility = if (KeyConstants.showPrint) View.VISIBLE else View.GONE
            printLittleButton.visibility = if (KeyConstants.showPrintLittleTag) View.VISIBLE else View.GONE
            photoButton.setOnClickListener { v: View? -> startPhotoActivity(item.number.replace("-", "")) }
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

    private fun setViewClick(view: View) {
        view.apply {
            confirmButton.setOnClickListener { v: View? ->
                presenter.saveItemToChecked(true)
                refreshItems!!.refresh()
                dismiss()
            }
            cancelButton.setOnClickListener { v: View? ->
                presenter.saveItemToChecked(false)
                refreshItems!!.refresh()
                dismiss()
            }
            userTextView.setOnClickListener { v: View? -> presenter.userTextViewClick() }
            deleteTextView.setOnClickListener { v: View? -> presenter.deleteTextViewClick() }
            printTextView.setOnClickListener { v: View? -> presenter.printTextViewClick() }
            printButton.setOnClickListener { v: View? -> showPrintDialog(presenter.item) }
            printLittleButton.setOnClickListener { v: View? -> showLittlePrintDialog(presenter.item) }
            tagContentTextView.setOnClickListener { v: View? -> presenter.tagContentTextViewClick() }
            resultEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    presenter.item.PA3UR = s?.toString() ?: ""
                    presenter.save()
                }
            })
        }
    }

    fun showSetDialog(clickListener: (item: String) -> Unit, title: String, temp: List<String>) {
        val dialog = AlertDialog.Builder(activity!!)
        dialog.setTitle(title)
        dialog.setItems(temp.toTypedArray()) { _, which ->
            clickListener(temp[which])
        }
        dialog.create().show()
    }

    fun showSetDialog1(clickListener: (item: SearchableItem) -> Unit, title: String, dataList: List<SearchableItem>) {
        val dialog = SearchItemDialog(activity, dataList)
        dialog.setTitle(title)
        dialog.setOnClickListener(clickListener)
        dialog.show()
    }

    fun showPrintDialog(item: Item) {
        val dialog = PrinterItemDialog(activity!!)
        dialog.setItem(item)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun showLittlePrintDialog(item: Item) {
        val dialog = PrintItemLittleTagDialog(activity!!)
        dialog.setItem(item)
        dialog.setCancelable(false)
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(fragmentActivity: FragmentActivity, item: Item, refreshItems: RefreshItems?): ItemDetailFragment {
            val fragment = ItemDetailFragment()
            val presenter = ViewModelProviders.of(fragmentActivity).get(ItemDetailPresenter::class.java)
            presenter.item = item
            fragment.refreshItems = refreshItems
            return fragment
        }
    }
}