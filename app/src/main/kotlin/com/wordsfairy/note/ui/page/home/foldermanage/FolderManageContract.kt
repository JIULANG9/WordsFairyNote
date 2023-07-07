package com.wordsfairy.note.ui.page.home.foldermanage

import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/21 22:01
 */

sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    data class MovePosition(val folders: List<NoteFolderEntity>) : ViewIntent
    data class DeleteFolder(val folder: NoteFolderEntity) : ViewIntent
    data class ModifyFolder(val folder: NoteFolderEntity) : ViewIntent
    data class ModifyFolderChanged(val folder: NoteFolderEntity) : ViewIntent
    data class NoteFolderNameChanged(val folderName: String) : ViewIntent
    object CreateFolder : ViewIntent
    object Retry : ViewIntent
}

data class ViewState(
    val modifyFolderEntity : NoteFolderEntity?,
    val addNoteFolderName : String,
    val isLoading: Boolean,
    val isRefreshing: Boolean
) : MviViewState {
    companion object {
        fun initial() = ViewState(
            modifyFolderEntity= null,
            addNoteFolderName = "",
            isLoading = true,
            isRefreshing = false
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
    sealed class NoteData : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is CreateFolder -> vs
            }
        }
        object CreateFolder : NoteData()

    }
    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is UpDataPosition -> vs
                is ModifyFolder -> vs.copy(modifyFolderEntity = folder)
                is DeleteFolder -> vs
                is NoteFolderNameChanged -> vs.copy(addNoteFolderName = folderName)
            }
        }
        data class ModifyFolder(val folder: NoteFolderEntity) : UI()
        object UpDataPosition : UI()
        object DeleteFolder : UI()
        data class NoteFolderNameChanged(val folderName: String) : UI()

    }
}