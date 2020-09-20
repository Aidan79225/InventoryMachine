package com.aidan.inventoryworkplatform.base

import android.app.Application
class InventoryApplication: Application() {
    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}

