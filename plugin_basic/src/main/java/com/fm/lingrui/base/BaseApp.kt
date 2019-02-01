package com.fm.lingrui.base

import android.app.Application

open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        lrApplication = this;
    }

    companion object {
        lateinit var lrApplication: BaseApplication;

        fun getApplication(): BaseApplication {
            return lrApplication;
        }
    }
}