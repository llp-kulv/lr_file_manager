package com.fm.lingrui

import android.content.Intent
import com.fm.lingrui.base.BaseApplication
import com.fm.lingrui.base.utils.Flog
import com.fm.lingrui.service.LRService

class LRApplication : BaseApplication() {
    val TAG: String = "LRApplication"

    override fun onCreate() {
        super.onCreate()

        // start service
        startService()
    }

    private fun startService() {
        val serviceIntent: Intent = Intent(this, LRService().javaClass)
        startService(serviceIntent)
    }
}