package com.wordsfairy.note.ui.page.backups

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.wordsfairy.common.tools.DATE_FORMAT_Month_Day_Time_Second
import com.wordsfairy.common.tools.timestampToString
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.room.db.AppDataBase
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.ext.flow.flowFromSuspend
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.ToastModelError
import com.wordsfairy.note.ui.widgets.toast.ToastModelSuccess
import com.wordsfairy.note.ui.widgets.toast.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


import javax.inject.Inject

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/12 11:13
 */
@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class NoteDataViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: NoteFolderRepository,
    private val noteEntityRepository: NoteEntityRepository,
    private val contentRepository: NoteContentRepository,
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    var importType = ImportType.TXT

    init {
        val initialVS = ViewState.initial()

        viewStateFlow = merge(
            intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is ViewIntent.Initial })
            .toPartialChangeFlow()
            .sendSingleEvent().scan(initialVS) { vs, change -> change.reduce(vs) }.catch {
                Log.e(logTag, "[NoteDataViewModel] Throwable:", it)
            }.stateIn(
                viewModelScope, SharingStarted.Eagerly, initialVS
            )
    }

    fun clearAllTables() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDataBase.getInstance().clearAllTables()
        }
    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->
            val event = when (change) {
                is PartialChange.UI.Success -> SingleEvent.UI.Success
                is PartialChange.UI.Error -> SingleEvent.UI.Error(change.msg)
                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        run {
            val progressChange: (Float) -> PartialChange = {
                when (it) {
                    100F -> PartialChange.UI.Success
                    else -> PartialChange.UI.Progress(it)
                }
            }
            val importFileFlow = flow {
                if (viewStateFlow.value.importAndCover) {
                    clearAllTables()
                }
                when (importType) {
                    ImportType.TXT ->

                        importFile(
                            folderRepository = folderRepository,
                            noteEntityRepository = noteEntityRepository,
                            contentRepository = contentRepository
                        ) {
                            emit(progressChange(it))
                            println("进度：$it")
                        }

                    ImportType.JSON ->
                        importJsonFile(
                            folderRepository = folderRepository,
                            noteEntityRepository = noteEntityRepository,
                            contentRepository = contentRepository,
                            error = { msg ->
                                emit(PartialChange.UI.Error(msg))
                            }
                        ) {
                            emit(progressChange(it))
                            println("进度：$it")
                        }
                }

            }.cancellable().onCompletion {
                println("导入完成")
            }
            val importFlow = filterIsInstance<ViewIntent.Import>()
                .log("[导入]")
                .flatMapLatest {
                    importFileFlow
                }.flowOn(Dispatchers.IO)

            val backupsFileFlow = flow {
                when (importType) {
                    ImportType.TXT -> exportFile(noteRepository, error = {
                        emit(PartialChange.UI.Error(it))
                    }) {
                        println("进度：$it")
                        emit(progressChange(it))
                    }

                    ImportType.JSON -> exportToJsonFile(noteRepository, error = {
                        emit(PartialChange.UI.Error(it))
                    }, callBackSchedule = {
                        println("进度：$it")
                        emit(progressChange(it))
                    })
                }
            }.cancellable().onCompletion {
                println("备份完成")
            }
            val importAndCoverFlow = filterIsInstance<ViewIntent.ImportAndCover>()
                .log("[切换主题]")
                .map { it.isCover }.map { isCover ->
                    AppSystemSetManage.importAndCover = isCover
                    PartialChange.UI.ImportAndCover(isCover)
                }.flowOn(Dispatchers.IO)
            val backupsFlow = filterIsInstance<ViewIntent.Backups>()
                .log("[备份]")
                .flatMapLatest { backupsFileFlow }.flowOn(Dispatchers.IO)

            return merge(
                backupsFlow, importFlow, importAndCoverFlow
            )
        }

    /**
     * 创建文件夹  WordsFairy/Note
     * @param context Context
     * @param uri Uri
     * @return DocumentFile?
     */
    fun getNoteUri(context: Context, uri: Uri): DocumentFile? {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        val wordsFairFile =
            documentFile?.findFile(Constants.File.WordsFairy) ?: documentFile?.createDirectory(
                Constants.File.WordsFairy
            )
        //生成笔记文件夹名称
        val noteFileName = Constants.File.Note + "\t" + System.currentTimeMillis()
            .timestampToString(DATE_FORMAT_Month_Day_Time_Second)
        println("生成笔记文件夹名称 $noteFileName")
        val noteFile = wordsFairFile?.createDirectory(noteFileName)
        return noteFile
    }


    companion object {
        private const val VIEW_STATE = "NoteDetailsViewModel"


    }
}

