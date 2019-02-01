package com.fm.lingrui.base

import android.app.Application

class LRApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        lrApplication = this;
    }

    companion object {
        lateinit var lrApplication: LRApplication;

        fun getLRApplication(): LRApplication {
            return lrApplication;
        }
    }
}