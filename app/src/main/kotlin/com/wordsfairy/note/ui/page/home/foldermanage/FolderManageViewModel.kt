package com.wordsfairy.note.ui.page.home.foldermanage


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.ext.flow.withLatestFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/21 21:59
 */

@HiltViewModel
class FolderManageViewModel @Inject internal constructor(
    private val folderRepository: NoteFolderRepository,
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    // 查询笔记信息并只显示最新5条笔记内容
    val noteInfoList = folderRepository.getNoteFolder()

    lateinit var beDeletedFolder :NoteFolderEntity
    init {
        val initialVS = ViewState.initial()

        viewStateFlow = merge(
            intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is ViewIntent.Initial }

        )
            .toPartialChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )

    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->

        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        shareWhileSubscribed().run {

            val modifyFolderFlow = filterIsInstance<ViewIntent.ModifyFolder>()
                .log("[修改]")
                .map {
                    folderRepository.update(it.folder)
                    PartialChange.UI.ModifyFolder(it.folder)
                }.flowOn(Dispatchers.IO)
            val modifyFolderChanged = filterIsInstance<ViewIntent.ModifyFolderChanged>()
                .log("[改变]")
                .map { PartialChange.UI.ModifyFolder(it.folder) }
            val movePositionFlow = filterIsInstance<ViewIntent.MovePosition>()
                .log("[移动位置]")
                .map {
                    folderRepository.update(it.folders)
                    PartialChange.UI.UpDataPosition
                }.flowOn(Dispatchers.IO)
            val deleteFolderFlow = filterIsInstance<ViewIntent.DeleteFolder>()
                .log("[删除]")
                .map {
                    folderRepository.delete(it.folder)
                    PartialChange.UI.DeleteFolder
                }.flowOn(Dispatchers.IO)

            /**
             * 创建文件夹
             */
            /** 添加笔记文件夹名称 */
            val addFolderNameChanged = filterIsInstance<ViewIntent.NoteFolderNameChanged>()
                .log("[添加笔记文件夹名称]")
                .map { it.folderName }

            val createFolderFlow = filterIsInstance<ViewIntent.CreateFolder>()
                .log("[创建笔记文件]")
                .withLatestFrom(addFolderNameChanged) { _, title -> title }
                .map { folderName ->
                    //文件夹位置递增
                    val position = folderRepository.getMaxPosition() + 1
                    val noteFolderEntity = NoteFolderEntity.create(
                        folderName,
                        System.currentTimeMillis(),
                        position = position
                    )
                    folderRepository.createNoteFolder(noteFolderEntity)
                    PartialChange.NoteData.CreateFolder
                }.flowOn(Dispatchers.IO)

            val formValuesChanges = merge(
                addFolderNameChanged.map { PartialChange.UI.NoteFolderNameChanged(it) },
            )
            return merge(
                modifyFolderChanged,
                modifyFolderFlow,
                movePositionFlow,
                deleteFolderFlow,
                formValuesChanges,
                createFolderFlow
            )
        }
}