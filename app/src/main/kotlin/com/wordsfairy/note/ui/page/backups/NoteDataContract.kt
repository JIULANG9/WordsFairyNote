package com.wordsfairy.note.ui.page.backups

import android.graphics.Bitmap
import android.os.Parcelable
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/12 11:10
 */
sealed interface ViewIntent : MviIntent {
    data object Initial : ViewIntent
    data object Backups : ViewIntent
    data object Import : ViewIntent
    data class ImportAndCover(val isCover: Boolean) : ViewIntent
}

@Parcelize
data class ViewState(
    val progress: Float,
    val loadContent: String,
    val QRCodeBitmap: Bitmap?,
    val importAndCover: Boolean,
    val isRefreshing: Boolean,
) : MviViewState, Parcelable {
    companion object {
        fun initial() = ViewState(
            progress = 0F,
            loadContent = "",
            importAndCover = AppSystemSetManage.importAndCover,
            isRefreshing = false,
            QRCodeBitmap = null
        )
    }
}

sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        object Success : UI
        data class Error(val msg: String) : UI
    }
}

internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState
    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Success -> vs.copy(isRefreshing = false, progress = 100F)
                is Progress -> vs.copy(progress = progress)
                is Loading -> vs.copy(QRCodeBitmap = null)
                is QRCode -> vs.copy(QRCodeBitmap = QRCode)
                is ImportAndCover -> vs.copy(importAndCover = isCover)
                else -> vs
            }
        }

        data object Loading : UI()
        data object Success : UI()
        data class ImportAndCover(val isCover: Boolean) : UI()
        data class Error(val msg: String) : UI()
        data class Progress(val progress: Float) : UI()
        data class QRCode(val QRCode: Bitmap?) : UI()
    }
}

enum class ImportType {
    TXT, JSON
}