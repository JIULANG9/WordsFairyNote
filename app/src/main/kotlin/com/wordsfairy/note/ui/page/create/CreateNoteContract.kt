package com.wordsfairy.note.ui.page.create

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/28 22:08
 */

import com.wordsfairy.note.data.entity.DialogDataBean
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState


data class ViewState(
    val title: String,
    val currentTime: String,
    val wordNumber: String,
    val noteContent: String,
    val addNoteFolderName: String,
    val selectedFolder: NoteFolderEntity?,
    val noteContentItems: MutableList<NoteContentEntity>,
    val noteEntity: NoteEntity?,
    val canSaved: Boolean,
    val canSaveTitle: Boolean,
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val dialogDataBean: DialogDataBean
) : MviViewState {
    companion object {
        fun initial() = ViewState(
            title = "",
            currentTime = "",
            wordNumber = "",
            noteContent = "",
            addNoteFolderName = "",
            selectedFolder = null,
            noteContentItems = mutableListOf(),
            noteEntity = null,
            isLoading = true,
            canSaved = false,
            canSaveTitle = false,
            isRefreshing = false,
            dialogDataBean = DialogDataBean.create()
        )
    }
}


sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    object Clean : ViewIntent
    object CreateFolder : ViewIntent
    //创建笔记 实体
    object CreateNoteEntity : ViewIntent

    //标题
    data class TitleChanged(val title: String) : ViewIntent

    //笔记文本
    data class NoteContentChanged(val noteContent: String) : ViewIntent

    //获取当前时间
    object GetCurrentTime : ViewIntent

    //选择文件夹
    data class SelectFolder(val selectFolder: NoteFolderEntity) : ViewIntent

    //添加笔记文件夹名称
    data class NoteFolderNameChanged(val noteFolderName: String) : ViewIntent

    object NoteEntityChanged : ViewIntent
    object SaveNote : ViewIntent
    //批量导入
    object BatchImport : ViewIntent
    //显示弹窗
    data class ShowDialog(val  dialogDataBean: DialogDataBean) : ViewIntent
}


sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        data class Toast(val content: String) : UI
        data class AddNoteContent(val noteContent: NoteContentEntity) : UI
        object ShowDialog : UI
    }
}


internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState

    sealed class NoteData : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is CreateFolder -> vs.copy(selectedFolder = selectFolder)
                is SelectFolder -> vs.copy(selectedFolder = selectFolder)
                is CreateNoteEntity -> vs.copy(noteEntity = noteEntity,canSaveTitle = false)
                is SaveNote -> vs.copy(noteContent = "", noteEntity = noteEntity, canSaveTitle = false,canSaved = false)
            }
        }

        data class CreateFolder(val selectFolder: NoteFolderEntity) : NoteData()
        data class SelectFolder(val selectFolder: NoteFolderEntity) : NoteData()
        data class SaveNote(val noteContent: NoteContentEntity,val noteEntity: NoteEntity) : NoteData()
        data class CreateNoteEntity(val noteEntity: NoteEntity) : NoteData()
    }

    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Title -> vs.copy(title = title, canSaveTitle = canSave)
                is Time -> vs.copy(currentTime = time)
                is NoteContent -> vs.copy(noteContent = note,canSaved = canSaveNote)
                is AddFolderName -> vs.copy(addNoteFolderName = addFolderName)
                is Init -> vs
                is Clean -> vs.copy(
                    title = "",
                    currentTime = "",
                    wordNumber = "",
                    noteContent = "",
                    addNoteFolderName = "",
                    selectedFolder = null,
                    noteContentItems = mutableListOf(),
                    noteEntity = null,
                    isLoading = true,
                    canSaved = false,
                    canSaveTitle = false,
                    isRefreshing = false
                )
                is ShowDialog -> vs.copy(dialogDataBean=dialog)
            }
        }

        data class Title(val title: String,val canSave: Boolean) : UI()

        data class Time(val time: String) : UI()
        data class NoteContent(val note: String,val canSaveNote : Boolean) : UI()
        data class AddFolderName(val addFolderName: String) : UI()
        object Init: UI()
        object Clean: UI()
        data class  ShowDialog(val dialog: DialogDataBean): UI()
    }
}