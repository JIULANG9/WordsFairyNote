@file:Suppress("unused")
@file:JvmName("NumberTools")

package com.wordsfairy.common.tools

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.regex.Pattern

/* ----------------------------------------------------------------------------------------- */
/* |                                        数字相关                                        | */
/* ----------------------------------------------------------------------------------------- */

/** 将字符串转换为 [Int] 类型，失败返回0 */
fun String?.toIntOrZero(): Int {
    return toDoubleOrZero().toInt()
}

/** 将字符串转换为 [Long] 类型，失败返回0L */
fun String?.toLongOrZero(): Long {
    return toDoubleOrZero().toLong()
}

/** 将字符串转换为 [Float] 类型 ，失败返回0.0f*/
fun String?.toFloatOrZero(): Float {
    return toDoubleOrZero().toFloat()
}

/** 将字符串转换为 [Double] 类型，失败返回0.0 */
fun String?.toDoubleOrZero(): Double {
    return this?.toDoubleOrNull() ?: 0.0
}

/** 将任意数字类型转换为 [Double] 类型，失败返回0.0 */
fun <N : Number> N?.toDoubleOrZero(): Double {
    return this?.toDouble() ?: 0.0
}

/** 将字符转换为 [BigDecimal] 类型，失败返回0 */
fun String?.toBigDecimalOrZero(): BigDecimal {
    return BigDecimal(toDoubleOrZero().toString())
}

/** 将数字转换为 [BigDecimal] 类型，失败返回0 */
fun <N : Number> N?.toBigDecimalOrZero(): BigDecimal {
    return BigDecimal(toDoubleOrZero().toString())
}

/** 根据格式[format]格式化数字，[format]默认`"0.00"` */
@JvmOverloads
fun String?.decimalFormat(format: String = "0.00"): String {
    return toDoubleOrZero().decimalFormat(format)
}

/** 根据格式[format]格式化数字，[format]默认`"0.00"` */
@JvmOverloads
fun <N : Number> N?.decimalFormat(format: String = "0.00"): String {
    return DecimalFormat(format).format(toDoubleOrZero())
}

/***
 * 手机号码检测
 */

fun String.checkPhoneNumber(): Boolean{
    val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(166)|(19[8,9])|)\\d{8}$"
    val p = Pattern.compile(regExp)
    val m = p.matcher(this)
    return m.matches()
}
