package com.wordsfairy.note.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import com.wordsfairy.base.utils.store.DataStoreUtils
import com.wordsfairy.note.MainActivity
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.ext.flowbus.FlowBusInitializer
import com.wordsfairy.note.ui.page.launcher.LauncherActivity
import dagger.hilt.android.HiltAndroidApp

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/22 17:04
 */
@HiltAndroidApp
class BaseApplication : Application(){
    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: Context
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext
        DataStoreUtils.init(this)
        FlowBusInitializer.init(this)
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // Dex 分包
        MultiDex.install(this)
    }

}