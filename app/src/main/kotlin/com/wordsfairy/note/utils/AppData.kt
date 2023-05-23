package com.wordsfairy.note.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/12/28 16:15
 */

fun Activity.getVersionCode(): Int {
    val manager: PackageManager = this.packageManager
    var code = 0
    try {
        val info: PackageInfo = manager.getPackageInfo(this.packageName, 0)
        code = info.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return code
}

fun Activity.getVersionName(): String {
    val manager: PackageManager = this.packageManager
    var code = ""
    try {
        val info: PackageInfo = manager.getPackageInfo(this.packageName, 0)
        code = info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return code
}
