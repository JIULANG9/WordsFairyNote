package com.wordsfairy.note.ui.page.detail.set

import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.entity.DialogDataBean
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:01
 */

sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    data class SearchEngines(val url: String) : ViewIntent
    data class CopyJumpToWeChat(val jump: Boolean) : ViewIntent
//    长文本自动折叠
    data class LongTextAutoFold(val fold: Boolean) : ViewIntent
    object RecycleNote : ViewIntent
    object RecycleNoteContents : ViewIntent
    data class ShowDialog(val  dialogDataBean: DialogDataBean) : ViewIntent
}

data class ViewState(
    val isLoading: Boolean,
    val currentUrl: String,
    val jumpToWeChat: Boolean,
    val longTextAutoFold: Boolean,
    val dialogDataBean: DialogDataBean
) : MviViewState {
    companion object {
        fun initial() = ViewState(
            isLoading = true,
            currentUrl = AppSystemSetManage.searchEngines,
            jumpToWeChat = AppSystemSetManage.jumpToWeChat,
            longTextAutoFold = AppSystemSetManage.longTextAutoFold,
            dialogDataBean = DialogDataBean.create(),
        )
    }
}

sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        object ShowDialog : UI

    }
}

internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState
    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Init -> vs
                is Dialog -> vs
                is SetSearchEngines -> vs.copy(currentUrl = url)
                is CopyJumpToWeChat -> vs.copy(jumpToWeChat = jump)
                is LongTextAutoFold -> vs.copy(longTextAutoFold = fold)
                is ShowDialog -> vs.copy(dialogDataBean = dialogDataBean)
            }
        }
        object Init : UI()
        data class SetSearchEngines(val url:String) : UI()
        data class CopyJumpToWeChat(val jump:Boolean) : UI()
        data class LongTextAutoFold(val fold:Boolean) : UI()
        data class ShowDialog(val dialogDataBean :DialogDataBean) : UI()
        data class Dialog(val dialogDataBean :DialogDataBean) : UI()
    }
    sealed class NoteData : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is RecycleNote -> vs
                is RecycleNoteContents -> vs
            }
        }
        object RecycleNote : NoteData()
        object RecycleNoteContents : NoteData()

    }
}