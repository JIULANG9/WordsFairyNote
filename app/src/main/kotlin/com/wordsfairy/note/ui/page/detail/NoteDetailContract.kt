package com.wordsfairy.note.ui.page.detail

import android.os.Parcelable
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/9 17:10
 */

enum class UIState {
    Add, Read, Search
}
@Parcelize
data class ViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val recentUpdates: String,
    val canSaveTitle: Boolean,
    val canSaveContent: Boolean,
    val title: String,
    val noteContent: String,
    val selectedFolder: NoteFolderEntity?,
    val noteEntity: NoteEntity?,
    val modifyNoteContent: NoteContentEntity?,
    val uiState:UIState,
    val searchResultData:List<NoteContentEntity>,
) : MviViewState, Parcelable {
    companion object {
        fun initial() = ViewState(
            isLoading = true,
            isRefreshing = false,
            canSaveTitle = false,
            canSaveContent = false,
            recentUpdates = "",
            title = "",
            noteContent = "",
            selectedFolder = null,
            noteEntity = null,
            modifyNoteContent = null,
            uiState = UIState.Add,
            searchResultData = emptyList()
        )
    }
}

sealed interface ViewIntent : MviIntent {
    data class Initial(val noteEntity: NoteEntity) : ViewIntent
    object Clean : ViewIntent
    data class UIStateChanged(val uiState: UIState) : ViewIntent
    object RecentUpdates : ViewIntent

    //修改标题

    data class TitleChanged(val title: String) : ViewIntent
    object ModifyTitle : ViewIntent

    data class ContentChanged(val content: String) : ViewIntent
    //选择文件夹
    data class SelectFolder(val selectFolder: NoteFolderEntity) : ViewIntent
    data class SearchContent(val keyword:String) : ViewIntent
    object InitSearch: ViewIntent
    object AddNoteContent : ViewIntent
    data class MovePosition(val noteContents: List<NoteContentEntity>) : ViewIntent

    /** 修改内容 */
    data class ModifyContentChanged(val noteContentEntity: NoteContentEntity) : ViewIntent
    data class ModifyContent(val noteContentEntity: NoteContentEntity) : ViewIntent

    data class DeleteContent(val noteContentEntity: NoteContentEntity) : ViewIntent

}


sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        object Close : UI
    }
}

internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState

    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Init -> vs.copy( noteEntity = noteEntity, selectedFolder = noteFolder,title = noteEntity.title)
                is Clean -> vs.copy(canSaveTitle = false)
                is UIStateChanged -> vs.copy(uiState = uiState)
                is Title -> vs.copy(title = title, canSaveTitle = canSave)
                is Content -> vs.copy(noteContent = content, canSaveContent = canSave)
                is RecentUpdates -> vs.copy(recentUpdates = recentUpdates)
            }
        }
        data class Init(val noteEntity: NoteEntity,val noteFolder: NoteFolderEntity?) : UI()


        data class RecentUpdates(val recentUpdates: String) : UI()
        object Clean : UI()
        data class UIStateChanged(val uiState: UIState) : UI()
        data class Title(val title: String, val canSave: Boolean) : UI()
        data class Content(val content: String, val canSave: Boolean) : UI()
    }

    sealed class NoteData : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is SelectFolder -> vs.copy(selectedFolder = noteFolder)
                is SaveTitle -> vs.copy(canSaveTitle = false)
                is UpDataPosition -> vs
                is AddNoteContent -> vs.copy(noteContent = "", canSaveContent = false)
                is ModifyNoteContent -> vs.copy(modifyNoteContent = contentEntity)
                is UpDataNoteContent -> vs.copy(modifyNoteContent = null)
                is DeleteContent -> vs
                is SearchResultData -> vs.copy(searchResultData = resultData)
                is InitSearch -> vs.copy(searchResultData = resultData)
            }
        }

        data class SelectFolder(val noteFolder: NoteFolderEntity) : NoteData()
        object SaveTitle : NoteData()
        object AddNoteContent : NoteData()
        object UpDataPosition : NoteData()
        data class ModifyNoteContent(val contentEntity: NoteContentEntity) : NoteData()
        object UpDataNoteContent : NoteData()
        object DeleteContent : NoteData()
        data class SearchResultData(val resultData: List<NoteContentEntity>) : NoteData()
        data class InitSearch(val resultData: List<NoteContentEntity>)  : NoteData()
    }
}
