@file:Suppress("unused")
@file:JvmName("TimeTools")

package com.wordsfairy.common.tools


import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/* ----------------------------------------------------------------------------------------- */
/* |                                        时间相关                                        | */
/* ----------------------------------------------------------------------------------------- */

/** 默认时间格式化 */
const val DATE_FORMAT_DEFAULT = "yyyy-MM-dd"
const val YEAR_DATE_TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm"
const val DATE_TIME_FORMAT_DEFAULT = "MM月dd日 HH:mm"
/** 时间格式化 月-日 */
const val DATE_FORMAT_Month_day = "MM-dd"
const val DATE_FORMAT_Month_day_CHINESE = "MM月dd"
/** 时间格式化 月-日 时:分 */
const val DATE_FORMAT_Month_Day_Time = "MM-dd HH:mm"



/** 根据[format]格式化时间，[format]默认[DATE_FORMAT_DEFAULT] */
@JvmOverloads
fun <N : Number> N.dateFormat(format: String = DATE_FORMAT_DEFAULT): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (e: ParseException) {
        ""
    }
}
/** 根据[format]格式化时间，[format]默认[DATE_FORMAT_DEFAULT] */
@JvmOverloads
fun <N : Number> N.dateTimeFormat(format: String = DATE_TIME_FORMAT_DEFAULT): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (e: ParseException) {
        ""
    }
}

/** 根据[format]格式化时间，[format]默认[DATE_FORMAT_DEFAULT] */
@JvmOverloads
fun Date.dateFormat(format: String = DATE_FORMAT_DEFAULT): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (e: ParseException) {
        ""
    }
}

/** 根据[format]格式化时间，[format]默认[DATE_FORMAT_DEFAULT] */
@JvmOverloads
fun String.paresDate(format: String = DATE_FORMAT_DEFAULT): Date {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).parse(this)
    } catch (e: ParseException) {
        Date()
    }
}
/** 根据[format]格式化时间，[format]默认[DATE_FORMAT_DEFAULT] */
@JvmOverloads
fun String.paresDateTime(format: String = DATE_TIME_FORMAT_DEFAULT): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (e: ParseException) {
        "pares exception"
    }
}

/** 将字符串时间转换为 [Long] 类型时间 */
@JvmOverloads
fun String?.toLongTime(format: String = DATE_FORMAT_DEFAULT): Long {
    return if (this.isNullOrEmpty()) {
        Date().time
    } else {
        try {
            SimpleDateFormat(format, Locale.getDefault()).parse(this).time
        } catch (e: ParseException) {
            Date().time
        }
    }
}

/**
 *  将时间戳转换为字符串
 * @receiver Long
 * @param format String
 * @return String
 */
@JvmOverloads
fun Long.timestampToString(format: String = DATE_TIME_FORMAT_DEFAULT):String{
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}
