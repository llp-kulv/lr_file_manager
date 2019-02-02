package com.fm.lingrui

import android.os.Bundle
import com.fm.lingrui.base.BaseActivity
import com.fm.lingrui.base.utils.Flog

/**
 * main activity
 */
class LRMainActivity : BaseActivity() {
    val TAG: String = "LRMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lr_main)

        Flog.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()

        Flog.d(TAG, "onDestroy")
        Flog.commitByFileSize()
    }
}
