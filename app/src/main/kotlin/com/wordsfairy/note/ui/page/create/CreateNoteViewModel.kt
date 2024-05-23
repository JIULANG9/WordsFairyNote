package com.wordsfairy.note.ui.page.create


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.wordsfairy.common.tools.timestampToString
import com.wordsfairy.note.ext.flow.withLatestFrom
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/28 22:06
 */

@HiltViewModel
class CreateNoteViewModel @Inject internal constructor(
    private val folderRepository: NoteFolderRepository,
    private val noteEntityRepository: NoteEntityRepository,
    private val contentRepository: NoteContentRepository,
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    //笔记文件夹
    val noteFolders: Flow<List<NoteFolderEntity>> = folderRepository.getNoteFolder()

    var noteContents: (Long) -> Flow<List<NoteContentEntity>> = { id ->
        contentRepository.getNoteContexts(id)
    }

    init {
        val initialVS = ViewState.initial()
        Log.d(logTag, "[init] : $initialVS")
        viewStateFlow = merge(
            intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is ViewIntent.Initial }
        )
            .filterNot { it is ViewIntent.Initial }.toPartialChangeFlow().sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it)
            }.stateIn(
                viewModelScope, SharingStarted.Eagerly, initialVS
            )
    }


    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->
            val event = when (change) {

                is PartialChange.NoteData.CreateFolder -> SingleEvent.UI.Toast("添加成功")
                is PartialChange.NoteData.SaveNote -> SingleEvent.UI.AddNoteContent(change.noteContent)
                is PartialChange.UI.ShowDialog -> SingleEvent.UI.ShowDialog
                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        run {

            /** 初始化 */
            val initFlow = filterIsInstance<ViewIntent.Initial>()
                .log("[Intent]")
                .map { PartialChange.UI.Init }

            /** 获取当前时间 */
            val getCurrentTimeFlow = filterIsInstance<ViewIntent.GetCurrentTime>()
                .log("[获取当前时间]")
                .map { System.currentTimeMillis() }
                .distinctUntilChanged()

            /** 标题内容 */
            val titleContentFlow = filterIsInstance<ViewIntent.TitleChanged>()
                .log("[标题内容]:titleContentFlow")
                .map { it.title }
               

            val titleContentChangeFlow = titleContentFlow.map { title ->
                val canSave = if (viewStateFlow.value.title == title) {
                    false
                } else title.isNotEmpty()
                PartialChange.UI.Title(title, canSave)
            }

            /** 笔记内容 */
            val noteContentChanges = filterIsInstance<ViewIntent.NoteContentChanged>()
                .log("[笔记内容]:noteContentChanges")
                .map { it.noteContent }

            val noteContentChangesFlow = noteContentChanges.map { title ->
                val canSave = if (viewStateFlow.value.noteContent == title) {
                    false
                } else title.isNotEmpty()
                PartialChange.UI.NoteContent(title, canSave)
            }

            /** 选择文件夹 */
            val selectFolderFlow = filterIsInstance<ViewIntent.SelectFolder>()
                .log("[选择文件夹]")
                .map {
                    val note = it.selectFolder
                    //选择文件夹后 如果noteEntity已经创建则 更新笔记实体
                    val noteEntity = viewStateFlow.value.noteEntity
                    if (noteEntity != null) {
                        noteEntity.folderId = note.folderId
                        noteEntityRepository.update(noteEntity)
                    }
                    PartialChange.NoteData.SelectFolder(note)
                }.flowOn(Dispatchers.IO)

            /** 添加笔记文件夹名称 */
            val addFolderNameChanged = filterIsInstance<ViewIntent.NoteFolderNameChanged>()
                .log("[添加笔记文件夹名称]")
                .map { it.noteFolderName }

            /**  创建笔记文件夹 */
            val createFolderFlow = filterIsInstance<ViewIntent.CreateFolder>()
                .log("[创建笔记文件夹]")
                .map {
                    //文件夹位置递增
                    val position = folderRepository.getMaxPosition() + 1
                    val noteFolderEntity = NoteFolderEntity.create(
                        it.folderName,
                        System.currentTimeMillis(),
                        position = position
                    )
                    val id = folderRepository.createNoteFolder(noteFolderEntity)
                    noteFolderEntity.folderId = id
                    PartialChange.NoteData.CreateFolder(noteFolderEntity)
                }.flowOn(Dispatchers.IO)

            /**
             * 收集
             */

            val noteEntityForm = combine(
                getCurrentTimeFlow,
            ) { _, time ->
                val selectedFolder = viewStateFlow.value.selectedFolder

                val folderId = selectedFolder?.folderId ?: 0L
                val title = viewStateFlow.value.title

                val noteEntity = viewStateFlow.value.noteEntity
                val count = noteEntity?.noteContextCount ?: 0
                NoteEntity.create(
                    folderId,
                    title = title,
                    noteContextCount = count,
                    createdAt = time
                )
            }

            val noteEntityTitleForm = combine(
                noteEntityForm,
                titleContentFlow
            ) { entity, title ->
                entity.title = title
                entity
            }

            /** 创建笔记实体，如果笔记实体已经创建，则更新 标题或者文件夹 */
            val createNoteEntityFlow = filterIsInstance<ViewIntent.CreateNoteEntity>()
                .log("[创建笔记实体]")
                .withLatestFrom(noteEntityTitleForm) { _, note ->

                    val noteEntity = if (viewStateFlow.value.noteEntity == null) {
                        note.noteId = noteEntityRepository.createNoteEntity(note)
                        note
                    } else {
                        //更新标题 或者 文件夹
                        val newNoteEntity = viewStateFlow.value.noteEntity!!
                        if ((newNoteEntity.title != note.title) ||
                            (newNoteEntity.folderId != note.folderId)
                        ) {
                            newNoteEntity.title = note.title
                            newNoteEntity.folderId = note.folderId
                            noteEntityRepository.update(newNoteEntity)
                        }
                        newNoteEntity
                    }
                    PartialChange.NoteData.CreateNoteEntity(noteEntity)
                }.flowOn(Dispatchers.IO)


            val noteContentEntityForm = combine(
                noteContentChanges,
                getCurrentTimeFlow,
            ) { noteContent, time ->
                NoteContentEntity.create(0L, noteContent, time)
            }.mapNotNull { it }

            /** 保存笔记 实体 */
            val saveNoteFlow = filterIsInstance<ViewIntent.SaveNote>()
                .log("[保存笔记]")
                .withLatestFrom(noteEntityForm) { _, note ->
                    val noteEntity = if (viewStateFlow.value.noteEntity == null) {
                        note.noteId = noteEntityRepository.createNoteEntity(note)
                        note
                    } else {
                        //更新标题
                        val newNoteEntity = viewStateFlow.value.noteEntity!!
                        if ((newNoteEntity.title != note.title) ||
                            (newNoteEntity.folderId != note.folderId)
                        ) {
                            newNoteEntity.title = note.title
                            newNoteEntity.folderId = note.folderId
                            noteEntityRepository.update(newNoteEntity)
                        }
                        newNoteEntity
                    }
                    noteEntity
                }
                .flowOn(Dispatchers.IO)
                .withLatestFrom(noteContentEntityForm) { noteEntity, noteContent ->
                    noteContent.noteId = noteEntity.noteId
                    val maxPosition = contentRepository.getMaxPosition(noteEntity.noteId)
                    noteContent.position = maxPosition + 1
                    contentRepository.createNoteContentEntity(noteContent)

                    val selectedFolder = viewStateFlow.value.selectedFolder
                    if (selectedFolder != null) {
                        //文件夹笔记内容条数 +1
                        selectedFolder.noteContextCount += 1
                        folderRepository.update(selectedFolder)
                    }

                    PartialChange.NoteData.SaveNote(noteContent, noteEntity)
                }.flowOn(Dispatchers.IO)

            /** 输入文本改变 */
            val formValuesChanges = merge(
                getCurrentTimeFlow.map { PartialChange.UI.Time(it.timestampToString()) },
                titleContentChangeFlow,
                noteContentChangesFlow,
                addFolderNameChanged.map { PartialChange.UI.AddFolderName(it) }
            )

            /** 清除缓存 */
            val cleanFlow = filterIsInstance<ViewIntent.Clean>()
                .log("[清除缓存]").map {
                    PartialChange.UI.Clean
                }
            val showDialogFlow = filterIsInstance<ViewIntent.ShowDialog>()
                .log("[showDialog]").map {
                    PartialChange.UI.ShowDialog(it.dialogDataBean)
                }

            return merge(
                initFlow,
                cleanFlow,
                formValuesChanges,
                selectFolderFlow,
                createFolderFlow,
                createNoteEntityFlow,
                saveNoteFlow,
                showDialogFlow
            )
        }

    companion object {
        const val IsNotUTF8Tag = 103
    }
}
