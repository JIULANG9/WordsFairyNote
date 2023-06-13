package com.wordsfairy.base.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.*
import java.nio.charset.Charset
import kotlin.experimental.and

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/17 14:07
 */



fun Context.readTxtFileByLinesWithProgress(uri: Uri): Sequence<Pair<String, Float>> {
    val inputStream = this.contentResolver.openInputStream(uri)!!
    val totalBytes = inputStream.available().toFloat()
    val bufferedStream = BufferedInputStream(inputStream)

    val reader = InputStreamReader(bufferedStream, Charsets.UTF_8)
    val bufferedReader = BufferedReader(reader)

    return sequence {
        var readBytes = 0f
        val lines = bufferedReader.useLines { it.toList().asReversed() }
        lines.forEach { line ->
            readBytes += line.toByteArray(Charsets.UTF_8).size.toFloat()
            val progress = readBytes / totalBytes * 100
            yield(line to progress)
        }
        bufferedStream.close()
        reader.close()
        bufferedReader.close()
    }
}




fun Context.isUTF8(uri: Uri): Boolean {
    val inputStream = this.contentResolver.openInputStream(uri)!!

    val buffer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        inputStream.readAllBytes()
    } else {
        inputStream.readBytes()
    }
    return isUtf8s(buffer)
}

private fun isUtf8s(buffer: ByteArray): Boolean {
    var i = 0
    while (i < buffer.size) {
        val b = buffer[i].toInt()

        if (b and 0x80 == 0x00) {
            // 0xxxxxxx
            i += 1
        } else if (b and 0xE0 == 0xC0) {
            // 110xxxxx 10xxxxxx
            if (i + 1 >= buffer.size || buffer[i + 1].toInt() and 0xC0 != 0x80) {
                return false
            }
            i += 2
        } else if (b and 0xF0 == 0xE0) {
            // 1110xxxx 10xxxxxx 10xxxxxx
            if (i + 2 >= buffer.size || buffer[i + 1].toInt() and 0xC0 != 0x80 || buffer[i + 2].toInt() and 0xC0 != 0x80) {
                return false
            }
            i += 3
        } else if (b and 0xF8 == 0xF0) {
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            if (i + 3 >= buffer.size || buffer[i + 1].toInt() and 0xC0 != 0x80 || buffer[i + 2].toInt() and 0xC0 != 0x80 || buffer[i + 3].toInt() and 0xC0 != 0x80) {
                return false
            }
            i += 4
        } else {
            // Invalid encoding
            return false
        }
    }
    return true
}
