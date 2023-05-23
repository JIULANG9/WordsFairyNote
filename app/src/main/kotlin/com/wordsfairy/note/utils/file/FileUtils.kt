package com.wordsfairy.note.utils.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.view.View
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Method


/**
 * uri转File
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
fun uriToFile(uri: Uri, context: Context): File? {
    var file: File? = null
    //android10以上转换
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
        file = File(uri.path)
    } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        //把文件复制到沙盒目录
        val contentResolver = context.contentResolver
        val displayName: String =
            (System.currentTimeMillis().toString() + Math.round((Math.random() + 1) * 1000).toString() + "." + MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri)))
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val cache = File(context.cacheDir.absolutePath, displayName)
            val fos = FileOutputStream(cache)
            FileUtils.copy(inputStream!!, fos)
            file = cache
            fos.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return file
}