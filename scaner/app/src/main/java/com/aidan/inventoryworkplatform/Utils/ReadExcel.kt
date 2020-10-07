package com.aidan.inventoryworkplatform.Utils

import com.aidan.inventoryworkplatform.Entity.Item
import com.aidan.inventoryworkplatform.Entity.WordName
import com.aidan.inventoryworkplatform.Model.ItemSingleton
import com.aidan.inventoryworkplatform.database.AppDatabase
import com.aidan.inventoryworkplatform.database.WordNameRepository
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

    fun readWordName(fileDescriptor: FileDescriptor?) {
        Thread {
            if (progressAction != null) {
                progressAction!!.showProgress("讀取字號名稱中")
            }
            try {
                loadAndSetWordName(Workbook.getWorkbook(FileInputStream(fileDescriptor)))
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
        }.start()
    }

    private fun loadAndSetWordName(w: Workbook) {
        val sheet = w.getSheet(0)
        val wordList = mutableListOf<WordName>()
        for (i in 0 until sheet.rows) {
            if (i == 0) continue
            wordList.add(
                    WordName(
                            sheet.getCell(0, i).contents,
                            sheet.getCell(1, i).contents
                    )
            )
        }
        if (wordList.size == 0) {
            return
        }
        val repository = WordNameRepository()
        AppDatabase.getInstance().runInTransaction {
            repository.deleteAll()
            repository.addAll(wordList)
            progressAction?.showToast("讀取字號成功")
        }
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