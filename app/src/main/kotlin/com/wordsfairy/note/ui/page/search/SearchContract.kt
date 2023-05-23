package com.wordsfairy.note.ui.page.search

import android.os.Parcelable
import com.wordsfairy.note.data.entity.SearchNoteEntity
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 14:00
 */

sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    data class SearchKeyword(val keyword: String) : ViewIntent
    object InitAllDataFlow : ViewIntent
}

@Parcelize
data class ViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val keyword: String,
    val resultData: List<SearchNoteEntity>
) : MviViewState, Parcelable {
    companion object {
        fun initial() = ViewState(
            isLoading = true,
            isRefreshing = false,
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
                is Success -> vs.copy(isRefreshing = false)
                is Init -> vs
            }
        }

        object Init : UI()
        object Success : UI()
    }

    sealed class NoteData : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is InitData -> vs.copy(resultData = searchNotes)
                is SearchResultData -> vs.copy(keyword = keyword, resultData = searchNotes)

            }
        }
        data class InitData(val searchNotes: List<SearchNoteEntity>) : NoteData()
        data class SearchResultData(val keyword: String, val searchNotes: List<SearchNoteEntity>) : NoteData()
    }
}

