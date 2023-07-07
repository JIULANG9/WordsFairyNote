package com.wordsfairy.note.ui.page.detail.search

import com.wordsfairy.note.data.entity.NoteEntity
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
}

data class ViewState(
    val isLoading: Boolean,
    val keyword: String,
    val resultData: List<NoteEntity>
) : MviViewState {
    companion object {
        fun initial() = ViewState(
            isLoading = true,
            keyword = "",
            resultData = emptyList()
        )
    }
}

sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        object Success : UI

    }
}

internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState
    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Success -> vs.copy()

            }
        }

        object Success : UI()
    }
}