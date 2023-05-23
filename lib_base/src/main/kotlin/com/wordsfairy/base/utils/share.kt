package com.wordsfairy.base.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/12/26 16:35
 */

fun Context.systemShare(content: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, "share")
    intent.putExtra(Intent.EXTRA_TEXT, content)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(Intent.createChooser(intent, "词仙笔记分享"))
}

fun Context.jumpToWeChat() {
    try{
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("weixin://")
        this.startActivity(intent)
    }catch(e:Exception){
       e.printStackTrace()
    }

}
