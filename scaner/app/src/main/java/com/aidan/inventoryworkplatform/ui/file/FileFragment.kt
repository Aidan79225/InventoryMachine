package com.aidan.inventoryworkplatform.ui.file

import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.aidan.inventoryworkplatform.Constants
import com.aidan.inventoryworkplatform.KeyConstants
import com.aidan.inventoryworkplatform.R
import com.aidan.inventoryworkplatform.Singleton
import kotlinx.android.synthetic.main.fragment_input_file.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Aidan on 2016/11/20.
 */
class FileFragment : DialogFragment(), FileContract.view {
    var presenter: FileContract.presenter? = null
    var fileRunnable: Runnable? = null
    var mProgressDialog: ProgressDialog? = null
    var type = 0
    override fun checkPermission() {
        val permission = ActivityCompat.checkSelfPermission(activity!!,
                permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE
            )
        } else {
            //已有權限，可進行檔案存取
            if (fileRunnable != null) {
                fileRunnable!!.run()
            }
        }
    }

    fun showToast(msg: String) {
        view!!.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限，進行檔案存取
                    if (fileRunnable != null) {
                        fileRunnable!!.run()
                    }
                }
                return
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ViewModelProviders.of(this).get(FilePresenter::class.java)
        ViewModelProviders.of(this).get(FilePresenter::class.java).apply {
            showProgress.observe(this@FileFragment, androidx.lifecycle.Observer {
                showProgress(it)
            })
            hideProgress.observe(this@FileFragment, androidx.lifecycle.Observer {
                hideProgress()
            })
            updateProgress.observe(this@FileFragment, androidx.lifecycle.Observer {
                updateProgress(it)
            })
            showToast.observe(this@FileFragment, androidx.lifecycle.Observer {
                showToast(it)
            })
        }
        setViewClick()
    }

    override fun setViewClick() {
        if (KeyConstants.showChanged) {
            outputChangedTextView!!.visibility = View.VISIBLE
            outputChangedItemTextView!!.visibility = View.VISIBLE
        }
        inputTextView!!.setOnClickListener { v: View? ->
            fileRunnable = Runnable { showFileChooser("text/*", FILE_SELECT_CODE) }
            checkPermission()
        }
        outputTextView!!.setOnClickListener { v: View? ->
            fileRunnable = createOutputCallback(false)
            checkPermission()
        }
        outputChangedTextView!!.setOnClickListener { v: View? ->
            fileRunnable = createOutputCallback(true)
            checkPermission()
        }
        outputItemTextView!!.setOnClickListener { v: View? ->
            fileRunnable = createOutputItemCallback(false)
            checkPermission()
        }
        outputChangedItemTextView!!.setOnClickListener { v: View? ->
            fileRunnable = createOutputItemCallback(true)
            checkPermission()
        }
        readNameTextView!!.setOnClickListener { v: View? ->
            fileRunnable = Runnable { showFileChooser(XLS_MIME, FILE_SELECT_NAME_CODE) }
            checkPermission()
        }
        readPurchaseDateTextView!!.setOnClickListener { v: View? ->
            fileRunnable = Runnable { showFileChooser(XLS_MIME, FILE_SELECT_PURCHASE_DATE_CODE) }
            checkPermission()
        }
        clearTextView!!.setOnClickListener { v: View? ->
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.clear_data).setMessage(R.string.clear_data_msg).setNegativeButton(R.string.cancel) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.setPositiveButton(R.string.confirm) { dialog: DialogInterface, which: Int ->
                presenter!!.clearData()
                dialog.dismiss()
            }.show()
        }
        inputItemTextView!!.setOnClickListener { v: View? ->
            fileRunnable = Runnable { showFileChooser(TEXT_MIME, FILE_SELECT_ITEM_CODE) }
            checkPermission()
        }
    }

    private fun createOutputCallback(onlyChanged: Boolean): Runnable {
        return Runnable {
            val allowType: MutableSet<String> = HashSet()
            allowType.add("0")
            allowType.add("1")
            allowType.add("2")
            allowType.add("3")
            allowType.add("4")
            allowType.add("5")
            showFileNameDialog("請輸入財產檔名", Constants.PREFERENCE_PROPERTY_KEY, allowType, onlyChanged)
        }
    }

    private fun createOutputItemCallback(onlyChanged: Boolean): Runnable {
        return Runnable {
            val allowType: MutableSet<String> = HashSet()
            allowType.add("6")
            showFileNameDialog("請輸入物品檔名", Constants.PREFERENCE_ITEM_KEY, allowType, onlyChanged)
        }
    }

    private fun showProgress(title: String?) {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setTitle(title)
        mProgressDialog!!.setMessage("正在處理請稍後...")
        mProgressDialog!!.max = 100
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog!!.show()
    }

    private fun hideProgress() {
        mProgressDialog!!.dismiss()
    }

    private fun updateProgress(value: Int) {
        mProgressDialog!!.progress = value
    }

    fun showFileNameDialog(title: String?, preferencesKey: String?, allowType: Set<String>?, onlyChanged: Boolean) {
        val editDialog = AlertDialog.Builder(activity!!)
        editDialog.setTitle(title)
        val editText = EditText(activity)
        val parse_DateFormatter = SimpleDateFormat("yyyyMMddHHmm")
        val date = Date()
        var id = ""
        try {
            val MS = Singleton.preferences.getString(preferencesKey, "")
            val jsonObject = JSONObject(MS)
            id = jsonObject.getString(Constants.MS01)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        editText.setText("PD" + id + parse_DateFormatter.format(date))
        editText.setSelection(editText.text.length)
        editDialog.setView(editText)

        // do something when the button is clicked
        editDialog.setPositiveButton("OK") { arg0: DialogInterface, arg1: Int ->
            presenter!!.saveFile(editText.text.toString(), preferencesKey, allowType, onlyChanged)
            arg0.dismiss()
        }
        // do something when the button is clicked
        editDialog.setNegativeButton("Cancel") { arg0: DialogInterface, arg1: Int -> arg0.dismiss() }
        editDialog.show()
    }

    private fun showFileChooser(mimeType: String, requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimeType
        activity!!.startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.data
                try {
                    presenter!!.readTxtButtonClick(readTextFromUri(uri))
                } catch (e: Exception) {
                }
            }
            FILE_SELECT_NAME_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.data
                try {
                    presenter!!.readNameTextViewClick(readTextFromUri(uri))
                } catch (e: Exception) {
                }
            }
            FILE_SELECT_ITEM_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.data
                try {
                    presenter!!.inputItemTextViewClick(readTextFromUri(uri))
                } catch (e: Exception) {
                }
            }
            FILE_SELECT_PURCHASE_DATE_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.data
                try {
                    presenter!!.readPurchaseDateTextViewClick(readTextFromUri(uri))
                } catch (e: Exception) {
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri?): FileDescriptor {
        val parcelFileDescriptor = context!!.contentResolver.openFileDescriptor(uri!!, "r")
        return parcelFileDescriptor!!.fileDescriptor
    }

    companion object {
        private const val readTxtType = 19
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private const val FILE_SELECT_CODE = 100
        private const val FILE_SELECT_NAME_CODE = 2
        private const val FILE_SELECT_ITEM_CODE = 3
        private const val FILE_SELECT_PURCHASE_DATE_CODE = 4
        private const val TEXT_MIME = "text/*"
        private const val XLS_MIME = "application/vnd.ms-excel"
    }
}