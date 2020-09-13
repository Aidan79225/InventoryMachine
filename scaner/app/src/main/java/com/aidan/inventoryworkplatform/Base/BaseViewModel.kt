package com.aidan.inventoryworkplatform.Base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {
    val showToast = MutableLiveData<String>()

    fun showToast(msg: String) {
        showToast.postValue(msg)
    }
}