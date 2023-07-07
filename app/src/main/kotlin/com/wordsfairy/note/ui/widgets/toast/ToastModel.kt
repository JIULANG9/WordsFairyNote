package com.wordsfairy.note.ui.widgets.toast

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/7/5 1:02
 */

open class ToastModel(
    open val message: String,
    val type: Type = Type.Normal,
    open val durationTime: Long? = null,
) {
    enum class Type {
        Normal, Success, Info, Warning, Error,
    }
}

data class ToastModelSuccess(
    override val message: String,
) : ToastModel(message, Type.Success, null)

data class ToastModelInfo(
    override val message: String,
) : ToastModel(message, Type.Success, null)

data class ToastModelError(
    override val message: String,
) : ToastModel(message, Type.Success, null)

data class ToastModelWarning(
    override val message: String,
) : ToastModel(message, Type.Success, null)


