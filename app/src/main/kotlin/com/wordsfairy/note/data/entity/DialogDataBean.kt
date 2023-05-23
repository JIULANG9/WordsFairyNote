package com.wordsfairy.note.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/18 18:22
 */

@Parcelize
data class DialogDataBean(
    val title: String,
    val message: String?,
    val isWaring:Boolean,
    val positiveBtnText: String?,
    val negativeBtnText: String?,
    val clackTag : Int
) : Parcelable {
    companion object {

        fun create(
            title: String = "",
            message: String = "",
            isWaring:Boolean = false,
            positiveBtnText: String = "",
            negativeBtnText: String = "",
            clackTag: Int = 0,
        ): DialogDataBean {
            return DialogDataBean(
                title,
                message,
                isWaring,
                positiveBtnText,
                negativeBtnText,
                clackTag
            )
        }
    }
}