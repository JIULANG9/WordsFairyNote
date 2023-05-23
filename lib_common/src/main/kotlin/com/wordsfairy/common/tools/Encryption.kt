@file:Suppress("unused")
@file:JvmName("EncryptionTools")

package com.wordsfairy.common.tools

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * 加密相关
 *
 * - 创建时间：2019/11/22
 *
 * @author 王杰
 */

/** 字符串对应的 **MD5** 值 */
val String.md5: String
    get() = try {
        MessageDigest.getInstance("MD5").digest(this.toByteArray()).toHexString()
    } catch (throwable: Throwable) {
        ""
    }

/** 文件对应的 **MD5** 值 */
val File.md5: String
    get() {
        var bi: BigInteger? = null
        try {
            val buffer = ByteArray(8192)
            var len: Int
            val md = MessageDigest.getInstance("MD5")
            val fis = FileInputStream(this)
            while (fis.read(buffer).also { len = it } != -1) {
                md.update(buffer, 0, len)
            }
            fis.close()
            val b = md.digest()
            bi = BigInteger(1, b)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (bi != null) bi.toString(16) else ""
    }

/** 将 [ByteArray] 转为 16 进制字符串 [String] */
fun ByteArray.toHexString(): String {
    //转成16进制后是32字节
    return with(StringBuilder()) {
        this@toHexString.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) {
                append("0").append(hexStr)
            } else {
                append(hexStr)
            }
        }
        toString().uppercase(Locale.getDefault())
    }
}

/** 将 16 进制字符串 [String] 转换为字节数组 [ByteArray] */
fun String.toHexByteArray(): ByteArray {
    val hexString = uppercase(Locale.getDefault())
    val len = hexString.length / 2
    val charArray = hexString.toCharArray()
    val byteArray = ByteArray(len)
    for (i in 0 until len) {
        val pos = i * 2
        byteArray[i] = (charArray[pos].toHexByte().toInt() shl 4 or charArray[pos + 1].toHexByte().toInt()).toByte()
    }
    return byteArray
}

/** 将 [Char] 转换为 16 进制 [String] 对应的 [Byte] */
fun Char.toHexByte(): Byte {
    return "0123456789ABCDEF".indexOf(this).toByte()
}