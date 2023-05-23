package com.wordsfairy.note.ui.page.home

import android.os.Parcelable
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.entity.NoteInfo
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 23:09
 */
sealed interface HomeViewIntent : MviIntent {
    object Initial : HomeViewIntent
    object GetNoteFolder : HomeViewIntent
    object GetNoteInfo : HomeViewIntent
    object Clean : HomeViewIntent
    data class CurrentNoteFolder(val folderEntity: NoteFolderEntity) : HomeViewIntent
    data class OpenNoteEntity(val noteEntity: NoteEntity) : HomeViewIntent
}

@Parcelize
data class HomeViewState(
    val searchContent: String,
    val noteInfo: List<NoteInfo>,
    val noteEntity: NoteEntity?,
    val noteFolder: NoteFolderEntity?
) : MviViewState , Parcelable {
    companion object {
        fun initial() = HomeViewState(
            searchContent = "",
            noteInfo = emptyList(),
            noteEntity = null,
            noteFolder = null
        )
    }
}

sealed interface HomeSingleEvent : MviSingleEvent {
    sealed interface Refresh : HomeSingleEvent {
        object Success : Refresh
    }
}


internal sealed interface HomePartialChange {
    fun reduce(vs: HomeViewState): HomeViewState


    sealed class NoteData : HomePartialChange {
        override fun reduce(vs: HomeViewState): HomeViewState {
            return when (this) {
                is NoteFolders -> vs
                is NoteInfoData -> vs.copy(noteInfo = noteInfo)
                is NoteEntityData -> vs.copy(noteEntity = noteEntity)
                is NoteFolderData -> vs.copy(noteFolder = noteFolder)
            }
        }

        data class NoteFolders(val noteFolders: Flow<List<NoteFolderEntity>>) : NoteData()
        data class NoteInfoData(val noteInfo: List<NoteInfo>) : NoteData()
        data class NoteEntityData(val noteEntity: NoteEntity) : NoteData()
        data class NoteFolderData(val noteFolder: NoteFolderEntity) : NoteData()
    }

    sealed class UI : HomePartialChange {
        override fun reduce(vs: HomeViewState): HomeViewState {
            return when (this) {

                is Close -> {
                    vs.copy(searchContent = "")
                }
                is Init-> vs
            }
        }

        object Init : UI()
        object Close : UI()
    }

    sealed class Search : HomePartialChange {
        override fun reduce(vs: HomeViewState): HomeViewState {
            return when (this) {
                is Context -> vs.copy(searchContent = content)
                is Submit -> vs
            }
        }

        data class Context(val content: String) : Search()
        object Submit : Search()
    }
}