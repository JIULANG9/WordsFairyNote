package com.wordsfairy.common.tools

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import java.security.SecureRandom

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2024/10/5 22:12
 */

@OptIn(ExperimentalEncodingApi::class)
object AESEncryption {

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY = "9999999999999999" // 16 bytes key

    //加密
    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(KEY.toByteArray(), "AES")

        // 生成随机的 IV
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(value.toByteArray())
        // 将 IV 和加密后的数据拼接在一起
        val combined = iv + encrypted
        return Base64.encode(combined)
    }

    //解密
    fun decrypt(encrypted: String): String {
        val combined = Base64.decode(encrypted)

        // 从 combined 中提取 IV 和加密数据
        val iv = combined.slice(0..15).toByteArray()
        val encryptedData = combined.slice(16 until combined.size).toByteArray()
        val cipher = Cipher.getInstance(ALGORITHM)
        val keySpec = SecretKeySpec(KEY.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val original = cipher.doFinal(encryptedData)
        return String(original)
    }
}