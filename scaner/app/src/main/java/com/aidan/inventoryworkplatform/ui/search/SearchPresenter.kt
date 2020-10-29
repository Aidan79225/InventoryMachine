package com.aidan.inventoryworkplatform.ui.search

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Environment
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.aidan.inventoryworkplatform.DatePicker.TimePickerView
import com.aidan.inventoryworkplatform.Dialog.SearchableItem
import com.aidan.inventoryworkplatform.Entity.*
import com.aidan.inventoryworkplatform.Model.*
import com.aidan.inventoryworkplatform.Printer.LittleTagCreator
import com.aidan.inventoryworkplatform.Printer.TagCreator
import com.aidan.inventoryworkplatform.base.BaseViewModel
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.google.zxing.BarcodeFormat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by Aidan on 2017/1/8.
 */
class SearchPresenter internal constructor(private val view: SearchContract.view) : BaseViewModel(), SearchContract.presenter {
    private var location: Location? = null
    private var agent: Agent? = null
    private var department: Department? = null
    private var user: Agent? = null
    private var useGroup: Department? = null
    private var selectTagContent: TagContent? = null
    private var sortCategory: SortCategory? = null
    private var minCalendar: Calendar
    private var maxCalendar: Calendar
    private var minDate: Date? = null
    private var maxDate: Date? = null
    override fun locationTextViewClick(locationTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(LocationSingleton.getInstance().locationList)
        view.showSetDialog({ item ->
            locationTextView.text = item.name
            location = item as Location
        }, "地點列表", temp)
    }

    override fun departmentTextViewClick(departmentTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(DepartmentSingleton.getInstance().departmentList)
        view.showSetDialog({ item ->
            departmentTextView.text = item.name
            department = item as Department
        }, "保管部門列表", temp)
    }

    override fun agentTextViewClick(agentTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(AgentSingleton.getInstance().agentList)
        view.showSetDialog({ item ->
            agentTextView.text = item.name
            agent = item as Agent
        }, "保管人列表", temp)
    }

    override fun useGroupTextViewClick(useGroupTextVie: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(DepartmentSingleton.getInstance().departmentList)
        view.showSetDialog({ item ->
            useGroupTextVie.text = item.name
            useGroup = item as Department
        }, "使用部門列表", temp)
    }

    override fun userTextViewClick(userTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        temp.addAll(AgentSingleton.getInstance().agentList)
        view.showSetDialog({ item ->
            userTextView.text = item.name
            user = item as Agent
        }, "使用人列表", temp)
    }

    override fun tagContentTextViewClick(tagContentTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        for (tagContent in TagContent.values()) {
            temp.add(tagContent)
        }
        view.showSetDialog({ item ->
            selectTagContent = item as TagContent
            tagContentTextView.text = item.getName()
        }, "標籤內容", temp)
    }

    override fun sortTextViewClick(sortTextView: TextView) {
        val temp: MutableList<SearchableItem> = ArrayList()
        for (sortCategory in SortCategory.values()) {
            temp.add(sortCategory)
        }
        view.showSetDialog({ item ->
            sortCategory = item as SortCategory
            sortTextView.text = item.getName()
        }, "排序條件", temp)
    }

    override fun minDateTextViewClick(activity: Activity) {
        showDatePicker(minCalendar, Runnable {
            minDate = minCalendar.time
            maxCalendar[minCalendar[Calendar.YEAR], minCalendar[Calendar.MONTH]] = minCalendar[Calendar.DAY_OF_MONTH]
            maxDate = maxCalendar.time
            view.setMinDateTextView(minCalendar)
            view.setMaxDateTextView(maxCalendar)
        }, activity)
    }

    override fun maxDateTextViewClick(activity: Activity) {
        showDatePicker(maxCalendar, Runnable {
            maxDate = maxCalendar.time
            view.setMaxDateTextView(maxCalendar)
        }, activity)
    }

    fun showDatePicker(c: Calendar, callback: Runnable, context: Context?) {
        val pvTime = TimePickerView.Builder(context) { date, v -> //选中事件回调
            val temp = Calendar.getInstance()
            temp.time = date
            c[temp[Calendar.YEAR], temp[Calendar.MONTH]] = temp[Calendar.DAY_OF_MONTH]
            callback.run()
        }.setType(booleanArrayOf(true, true, true, false, false, false)).build()
        pvTime.setDate(c)
        pvTime.show()
    }

    override fun searchTextViewClick(name: String, nickname: String, number: String, serialMinNumber: String, serialMaxNumber: String) {
        val minSerialNumber = if (serialMinNumber.length > 0) Integer.valueOf(serialMinNumber) else 0
        val maxSerialNumber = if (serialMaxNumber.length > 0) Integer.valueOf(serialMaxNumber) else Int.MAX_VALUE
        val itemList = getItemListWithCondition(name, nickname, number, minSerialNumber, maxSerialNumber)
        view.showFragmentWithResult(itemList)
    }

    override fun printTextViewClick(context: Context, name: String, nickname: String, number: String, serialMinNumber: String, serialMaxNumber: String) {
        val minSerialNumber = if (serialMinNumber.length > 0) Integer.valueOf(serialMinNumber) else 0
        val maxSerialNumber = if (serialMaxNumber.length > 0) Integer.valueOf(serialMaxNumber) else Int.MAX_VALUE
        val itemList = getItemListWithCondition(name, nickname, number, minSerialNumber, maxSerialNumber)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("列印").setMessage("將會列印 " + itemList.size + " 個項目，您確定要列印嗎？").setPositiveButton("確定") { dialog, which ->
            print(itemList)
            view.showToast("列印中請稍後")
            dialog.dismiss()
        }.setNegativeButton("取消") { dialog, which -> dialog.dismiss() }.show()
    }

    override fun printLittleTextViewClick(context: Context, name: String, nickname: String, number: String, serialMinNumber: String, serialMaxNumber: String) {
        val minSerialNumber = if (serialMinNumber.length > 0) Integer.valueOf(serialMinNumber) else 0
        val maxSerialNumber = if (serialMaxNumber.length > 0) Integer.valueOf(serialMaxNumber) else Int.MAX_VALUE
        val itemList = getItemListWithCondition(name, nickname, number, minSerialNumber, maxSerialNumber)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("列印").setMessage("將會列印 " + itemList.size + " 個項目，您確定要列印嗎？").setPositiveButton("確定") { dialog, which ->
            printLittleTags(itemList)
            view.showToast("列印中請稍後")
            dialog.dismiss()
        }.setNegativeButton("取消") { dialog, which -> dialog.dismiss() }.show()
    }

    fun getItemListWithCondition(name: String?, nickname: String?, number: String, minSerialNumber: Int, maxSerialNumber: Int): List<Item> {
        val itemList: MutableList<Item> = ArrayList()
        for (item in ItemSingleton.getInstance().itemList) {
            if (!item.name.contains(name!!)) {
                continue
            }
            if (!item.nickName.contains(nickname!!)) {
                continue
            }
            if (location != null && item.location.number != location!!.number) {
                continue
            }
            if (agent != null && item.custodian.number != agent!!.number) {
                continue
            }
            if (department != null && item.custodyGroup.number != department!!.number) {
                continue
            }
            if (user != null && item.user.number != user!!.number) {
                continue
            }
            if (useGroup != null && item.useGroup.number != useGroup!!.number) {
                continue
            }
            if (number.length > 1 && item.number != number) {
                continue
            }
            val serialNumber = Integer.valueOf(item.serialNumber.substring(2))
            if (serialNumber < minSerialNumber || serialNumber > maxSerialNumber) {
                continue
            }
            if (minDate != null && minDate!!.time > item.date.time) {
                continue
            }
            if (maxDate != null && maxDate!!.time < item.date.time) {
                continue
            }
            item.tagContent = selectTagContent
            itemList.add(item)
        }
        if (sortCategory != null) {
            when (sortCategory) {
                SortCategory.Agent -> Collections.sort(itemList) { o1, o2 -> o1.custodian.getName().compareTo(o2.custodian.getName()) }
                SortCategory.Group -> Collections.sort(itemList) { o1, o2 -> o1.custodyGroup.getName().compareTo(o2.custodyGroup.getName()) }
                SortCategory.Location -> Collections.sort(itemList) { o1, o2 -> o1.location.getName().compareTo(o2.location.getName()) }
            }
        }
        return itemList
    }

    override fun clearAll() {
        location = null
        agent = null
        department = null
        user = null
        useGroup = null
        selectTagContent = null
        sortCategory = null
        minDate = null
        maxDate = null
        minCalendar = Calendar.getInstance()
        minCalendar[Calendar.HOUR_OF_DAY] = 0
        minCalendar[Calendar.MINUTE] = 0
        maxCalendar = Calendar.getInstance()
        maxCalendar[Calendar.HOUR_OF_DAY] = 23
        maxCalendar[Calendar.MINUTE] = 59
        view.clearViews()
    }

    fun dpToPix(dp: Int): Int {
        return Resources.getSystem().displayMetrics.density.toInt() * dp
    }

    fun dpToPix(dp: Float): Int {
        return (Resources.getSystem().displayMetrics.density * dp).toInt()
    }

    fun print(itemList: List<Item>) {
        val trd = Thread(Runnable {
            view.showProgress("製造標籤中")
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/欣華盤點系統/圖片暫存"
            val fileList: MutableList<File> = ArrayList()
            val dirFile = File(dir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }
            for (i in itemList.indices) {
                val item = itemList[i]
                var bitmap = TagCreator.transStringToImage(item.tagContentString, TagCreator.height / 10 - dpToPix(2) * 2, dpToPix(2))
                try {
                    bitmap = TagCreator.mergeBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.barcodeNumber, BarcodeFormat.CODE_128, TagCreator.width, TagCreator.height / 5), dpToPix(2))
                    bitmap = TagCreator.mergeQRBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.barcodeNumber, BarcodeFormat.QR_CODE, TagCreator.height / 3, TagCreator.height / 3), dpToPix(2))
                    val fileName = item.number + item.serialNumber + ".png"
                    var file = File(dir, fileName)
                    if (file.exists()) {
                        file.delete()
                        file = File(dir, fileName)
                    }
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                    val bitmapdata = bos.toByteArray()
                    val fos = FileOutputStream(file)
                    fos.write(bitmapdata)
                    fos.flush()
                    fos.close()
                    fileList.add(file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                view.updateProgress((i + 1) * 100 / itemList.size)
            }
            view.hideProgress()
            view.showProgress("列印中")
            val printer = Printer()
            val netPrinters = printer.getNetPrinters("PT-P900W")
            if (netPrinters == null || netPrinters.size == 0) {
                view.hideProgress()
                view.showToast("列印失敗,找不到機器")
            } else {
                val printInfo = PrinterInfo()
                printInfo.printerModel = PrinterInfo.Model.PT_P900W
                printInfo.port = PrinterInfo.Port.NET
                printInfo.ipAddress = netPrinters[0].ipAddress
                printInfo.macAddress = netPrinters[0].macAddress
                printInfo.labelNameIndex = LabelInfo.PT.W36.ordinal
                printInfo.orientation = PrinterInfo.Orientation.LANDSCAPE
                printInfo.align = PrinterInfo.Align.CENTER
                printInfo.isAutoCut = false
                printInfo.isCutAtEnd = false
                printInfo.isHalfCut = true
                printer.printerInfo = printInfo
                printer.startCommunication()
                for (i in fileList.indices) {
                    val file = fileList[i]
                    val status = printer.printFile(file.absolutePath)
                    if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                        view.hideProgress()
                        view.showToast("列印失敗,找不到機器")
                        printer.endCommunication()
                        return@Runnable
                    }
                    view.updateProgress((i + 1) * 100 / fileList.size)
                }
                printer.endCommunication()
                view.hideProgress()
                view.showToast("列印成功")
            }
        })
        trd.start()
    }

    fun printLittleTags(itemList: List<Item>) {
        val trd = Thread(Runnable {
            view.showProgress("製造標籤中")
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/欣華盤點系統/圖片暫存"
            val fileList: MutableList<File> = ArrayList()
            val dirFile = File(dir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }
            for (i in itemList.indices) {
                val item = itemList[i]
                var bitmap = LittleTagCreator.transStringToImage(item.littleTagContentString, LittleTagCreator.height / 5 - dpToPix(2) * 2, dpToPix(1.5f))
                try {
                    bitmap = LittleTagCreator.addQRBitmap(bitmap, BarCodeCreator.encodeAsBitmap(item.barcodeNumber, BarcodeFormat.QR_CODE, LittleTagCreator.height, LittleTagCreator.height), dpToPix(2))
                    val fileName = item.number + item.serialNumber + "-little.png"
                    var file = File(dir, fileName)
                    if (file.exists()) {
                        file.delete()
                        file = File(dir, fileName)
                    }
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                    val bitmapdata = bos.toByteArray()
                    val fos = FileOutputStream(file)
                    fos.write(bitmapdata)
                    fos.flush()
                    fos.close()
                    fileList.add(file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                view.updateProgress((i + 1) * 100 / itemList.size)
            }
            view.hideProgress()
            view.showProgress("列印中")
            val printer = Printer()
            val netPrinters = printer.getNetPrinters("PT-P900W")
            if (netPrinters == null || netPrinters.size == 0) {
                view.hideProgress()
                view.showToast("列印失敗,找不到機器")
            } else {
                val printInfo = PrinterInfo()
                printInfo.printerModel = PrinterInfo.Model.PT_P900W
                printInfo.port = PrinterInfo.Port.NET
                printInfo.ipAddress = netPrinters[0].ipAddress
                printInfo.macAddress = netPrinters[0].macAddress
                printInfo.labelNameIndex = LabelInfo.PT.W12.ordinal
                printInfo.orientation = PrinterInfo.Orientation.LANDSCAPE
                printInfo.align = PrinterInfo.Align.CENTER
                printInfo.isAutoCut = false
                printInfo.isCutAtEnd = false
                printInfo.isHalfCut = true
                printer.printerInfo = printInfo
                printer.startCommunication()
                for (i in fileList.indices) {
                    val file = fileList[i]
                    val status = printer.printFile(file.absolutePath)
                    if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                        view.hideProgress()
                        view.showToast("列印失敗,找不到機器")
                        printer.endCommunication()
                        return@Runnable
                    }
                    view.updateProgress((i + 1) * 100 / fileList.size)
                }
                printer.endCommunication()
                view.hideProgress()
                view.showToast("列印成功")
            }
        })
        trd.start()
    }

    init {
        minCalendar = Calendar.getInstance()
        minCalendar[Calendar.HOUR_OF_DAY] = 0
        minCalendar[Calendar.MINUTE] = 0
        maxCalendar = Calendar.getInstance()
        maxCalendar[Calendar.HOUR_OF_DAY] = 23
        maxCalendar[Calendar.MINUTE] = 59
    }
}