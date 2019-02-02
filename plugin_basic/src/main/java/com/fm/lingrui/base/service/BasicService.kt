package com.fm.lingrui.base.service

import android.app.IntentService
import android.content.Intent
import com.fm.lingrui.base.utils.Flog

@Suppress("UNREACHABLE_CODE")
abstract class BasicService : IntentService("BasicService") {
    val TAG : String = "BasicService"

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return;
        }

        Flog.d(TAG, "intent:$intent")
        handleIntent();
    }

    abstract fun handleIntent();
}