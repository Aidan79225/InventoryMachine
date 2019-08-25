package com.aidan.inventoryworkplatform.Dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.aidan.inventoryworkplatform.R
import com.aidan.inventoryworkplatform.SettingConstants
import com.aidan.inventoryworkplatform.Singleton
import com.aidan.inventoryworkplatform.Utils.SettingsSingleton

class ScannerSettingDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.run {
            AlertDialog.Builder(this).let {
                it.setView(LayoutInflater.from(this).inflate(R.layout.dialog_scanner_setting, null).apply {
                    val preferenceEditor = Singleton.preferenceEditor
                    val preference = Singleton.preferences
                    findViewById<CheckBox>(R.id.printCheckBox).apply {
                        isChecked = preference.getBoolean(SettingConstants.PRINT_IN_SCANNER, false)
                        setOnClickListener {
                            preferenceEditor.putBoolean(SettingConstants.PRINT_IN_SCANNER, isChecked).commit()
                        }
                    }

                    findViewById<CheckBox>(R.id.deleteCheckBox).apply {
                        isChecked = preference.getBoolean(SettingConstants.DELETE_IN_SCANNER, false)
                        setOnClickListener {
                            preferenceEditor.putBoolean(SettingConstants.DELETE_IN_SCANNER, isChecked).commit()
                        }
                    }
                    findViewById<CheckBox>(R.id.scanCheckBox).apply {
                        SettingsSingleton.getInstance().showScannerInItemList.observe(this@ScannerSettingDialog, Observer {
                            isChecked = it
                        })
                        setOnClickListener {
                            val checked = SettingsSingleton.getInstance().showScannerInItemList.value
                            SettingsSingleton.getInstance().showScannerInItemList.value = !(checked ?: false)
                        }
                    }
                    findViewById<CheckBox>(R.id.scanAndShowCheckBox).apply {
                        isChecked = preference.getBoolean(SettingConstants.SHOW_AFTER_SCAN, false)
                        setOnClickListener {
                            preferenceEditor.putBoolean(SettingConstants.SHOW_AFTER_SCAN, isChecked).commit()
                        }
                    }
                    findViewById<TextView>(R.id.closeTextView).apply {
                        setOnClickListener {
                            dismiss()
                        }
                    }
                }).create()
            }
        } ?: super.onCreateDialog(savedInstanceState)
    }
}
