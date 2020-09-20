package com.aidan.inventoryworkplatform.Utils

import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.Model.ItemSingleton
import jxl.Workbook
import jxl.read.biff.BiffException
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * Created by Aidan on 2017/10/29.
 */
class ReadExcel {
    private var progressAction: ProgressAction? = null
    fun readName(fileDescriptor: FileDescriptor?) {
        Thread(Runnable {
            if (progressAction != null) {
                progressAction!!.showProgress("讀取名稱中")
            }
            val w: Workbook
            try {
                w = Workbook.getWorkbook(FileInputStream(fileDescriptor))
                loadAndSetName(w)
            } catch (e: BiffException) {
                progressAction!!.showToast("檔案格式錯誤")
                e.printStackTrace()
            } catch (iOException: IOException) {
                progressAction!!.showToast("檔案格式錯誤")
                iOException.printStackTrace()
            } finally {
                if (progressAction != null) {
                    progressAction!!.hideProgress()
                }
            }
        }).start()
    }

    fun loadAndSetName(w: Workbook) {
        val sheet = w.getSheet(0)
        val itemList = ItemSingleton.getInstance().itemList
        val itemMap: MutableMap<String, MutableList<Item>> = HashMap()
        for (item in itemList) {
            var list = itemMap[item.number]
            if (list == null) {
                list = ArrayList()
            }
            list.add(item)
            itemMap[item.number] = list
        }
        for (i in 0 until sheet.rows) {
            if (i == 0) continue
            var id: String? = ""
            id += sheet.getCell(0, i).contents
            id += sheet.getCell(1, i).contents
            id += sheet.getCell(2, i).contents
            id += sheet.getCell(3, i).contents
            id += sheet.getCell(4, i).contents
            val name = sheet.getCell(5, i).contents
            val list: List<Item>? = itemMap[id]
            if (list != null) {
                for (item in list) {
                    item.setNAME(name)
                }
            }
            if (progressAction != null) {
                progressAction!!.updateProgress((i + 1) * 100 / sheet.rows)
            }
        }
        ItemSingleton.getInstance().saveToDB()
    }

    fun readPurchaseDate(fileDescriptor: FileDescriptor?) {
        Thread(Runnable {
            if (progressAction != null) {
                progressAction!!.showProgress("讀取名稱中")
            }
            val w: Workbook
            try {
                w = Workbook.getWorkbook(FileInputStream(fileDescriptor))
                loadAndSetPurchaseDate(w)
            } catch (e: BiffException) {
                progressAction!!.showToast("檔案格式錯誤")
                e.printStackTrace()
            } catch (iOException: IOException) {
                progressAction!!.showToast("檔案格式錯誤")
                iOException.printStackTrace()
            } finally {
                if (progressAction != null) {
                    progressAction!!.hideProgress()
                }
            }
        }).start()
    }

    fun loadAndSetPurchaseDate(w: Workbook) {
        val sheet = w.getSheet(0)
        val itemList = ItemSingleton.getInstance().itemList
        val itemMap: MutableMap<String, MutableList<Item>> = HashMap()
        for (item in itemList) {
            var list = itemMap[item.number]
            if (list == null) {
                list = ArrayList()
            }
            list.add(item)
            itemMap[item.number] = list
        }
        for (i in 0 until sheet.rows) {
            if (i == 0) continue
            val number = sheet.getCell(0, i).contents.split("-".toRegex()).toTypedArray()[0]
            val id = sheet.getCell(0, i).contents.split("-".toRegex()).toTypedArray()[1]
            val list: List<Item>? = itemMap[number]
            if (list != null) {
                for (item in list) {
                    if (item.serialNumber == id) {
                        item.purchaseDate = sheet.getCell(1, i).contents
                    }
                }
            }
            if (progressAction != null) {
                progressAction!!.updateProgress((i + 1) * 100 / sheet.rows)
            }
        }
        ItemSingleton.getInstance().saveToDB()
    }

    fun setProgressAction(progressAction: ProgressAction?) {
        this.progressAction = progressAction
    }

    interface ProgressAction {
        fun showProgress(msg: String?)
        fun hideProgress()
        fun updateProgress(value: Int)
        fun showToast(msg: String?)
    }
}