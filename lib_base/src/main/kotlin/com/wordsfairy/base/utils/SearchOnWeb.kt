package com.wordsfairy.base.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/12 12:03
 */
fun Context.searchInBrowser(url: String,query: String) {
    val searchUrl = "$url$query"
    val uri = Uri.parse(searchUrl)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    this.startActivity(intent)
}
fun Context.searchInBrowser(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    this.startActivity(intent)
}