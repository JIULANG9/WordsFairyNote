package com.wordsfairy.note.ui.page.backups

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.wordsfairy.common.tools.DATE_FORMAT_Month_Day_Time_Second
import com.wordsfairy.common.tools.timestampToString
import com.wordsfairy.note.base.BaseApplication
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.entity.NoteAndNoteContent
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.room.db.AppDataBase
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.utils.file.contentEntityToTxtFile
import com.wordsfairy.note.utils.file.readTxtFilesFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


import javax.inject.Inject

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/12 11:13
 */
@HiltViewModel
class NoteDataViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: NoteFolderRepository,
    private val noteEntityRepository: NoteEntityRepository,
    private val contentRepository: NoteContentRepository,
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    init {
        val initialVS = ViewState.initial()

        viewStateFlow = merge(
            intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is ViewIntent.Initial })
            .toPartialChangeFlow()
            .sendSingleEvent().scan(initialVS) { vs, change -> change.reduce(vs) }.catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it)
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
                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        run {


            val importFileFlow = flow {
                importFile {
                    emit(it)
                    println("进度：$it")
                }
            }.cancellable().onCompletion {
                println("导入完成")
            }
            val importFlow = filterIsInstance<ViewIntent.Import>().log("[导入]").flatMapLatest {
                importFileFlow
            }.flowOn(Dispatchers.IO).map {
                when (it) {
                    100F -> PartialChange.UI.Success
                    else -> PartialChange.UI.Progress(it)
                }
            }
            val backupsFileFlow = flow {
                exportFile {
                    emit(it)
                    println("进度：$it")
                }
            }.cancellable().onCompletion {
                println("备份完成")
            }
            val backupsFlow = filterIsInstance<ViewIntent.Backups>()
                .log("[备份]")
                .flatMapLatest { backupsFileFlow }.flowOn(Dispatchers.IO).map {
                    when (it) {
                        100F -> PartialChange.UI.Success
                        else -> PartialChange.UI.Progress(it)
                    }
                }

            return merge(
                backupsFlow, importFlow
            )
        }
    //全部数据转二维码

    /**
     * 导入文件
     * @param callBackSchedule SuspendFunction1<Float, Unit>
     */
    private suspend fun importFile(callBackSchedule: suspend (Float) -> Unit) {
        val folderUri = GlobalData.importFolderUri!!
        val (total, files) = readTxtFilesFromUri(folderUri)
        println("总行数:$total")
        var currentSchedule = 0
        val createdAt = System.currentTimeMillis()
        files.forEach { (fileName, lines) ->
            //文件夹位置递增
            val folderPosition = folderRepository.getMaxPosition() + 1
            val noteFolder = NoteFolderEntity.create(
                fileName, createdAt, folderPosition
            )
            val folderId = folderRepository.insert(noteFolder)
            var position = 0
            lines.forEach { (title, contents) ->
                val note = NoteEntity.create(
                    folderId, title, contents.size, createdAt
                )
                val noteId = noteEntityRepository.insert(note)

                val noteContents = contents.reversed().mapIndexed { _, content ->
                    //进度递增,回调进度
                    currentSchedule++
                    callBackSchedule(
                        currentSchedule.toFloat() / total.toFloat() * 100
                    )
                    //位置递增,解决排序问题
                    position++
                    NoteContentEntity.create(
                        noteId, content, createdAt
                    )
                    NoteContentEntity.create(noteId, content, createdAt, position)
                }
                contentRepository.insert(noteContents)
            }

            lines.forEach(::println)
        }
    }

    /**
     *  导出文件
     * @param callBackSchedule SuspendFunction1<Float, Unit>
     */
    private suspend fun exportFile(callBackSchedule: suspend (Float) -> Unit) {
        //计算AllNoteInfo里面的NoteContents的数量
        val allNoteInfo = noteRepository.getAllNoteInfo()
        val total = allNoteInfo.map {
            var count = 0
            it.forEach { noteInfo ->
                noteInfo.noteAndNoteContents.forEach { noteAndContent ->
                    count += noteAndContent.noteContents.size
                }
            }
            count
        }.first()
        //当前进度
        var currentSchedule = 0
        val path = GlobalData.backupsSelectFolderUri!!
        val documentFile = DocumentFile.fromTreeUri(BaseApplication.CONTEXT, path)
        if (documentFile == null) {
            println("文件夹不存在")
            return
        }
        println("NoteContents的数量：${total}")
        allNoteInfo.map {

            it.forEach { noteInfo ->
                val noteFolder = documentFile.createDirectory(noteInfo.noteFolder.name)

                noteInfo.noteAndNoteContents.forEach { noteAndContent ->
                    noteFolder?.let {
                        // 创建笔记文件夹
                        val contentTitle = noteAndContentToTitle(noteAndContent)
                        // 创建笔记内容txt文件
                        contentEntityToTxtFile(
                            noteAndContent.noteContents, contentTitle, noteFolder.uri
                        ) {
                            currentSchedule++
                            callBackSchedule(
                                (currentSchedule.toFloat() / total.toFloat()) * 100
                            )
                        }
                    }
                }
            }
        }.collect()
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
}

private fun noteAndContentToTitle(noteAndContent: NoteAndNoteContent): String {
    var contentTitle = noteAndContent.noteEntity.title
    if (contentTitle.isEmpty()) {
        contentTitle = if (noteAndContent.noteContents.isNotEmpty()) {
            //判断内容字符是否大于5 大于就截取前5个字符作为标题  小于5就取全部
            if (noteAndContent.noteContents[0].content.length > 5) {
                noteAndContent.noteContents[0].content.substring(
                    0, 5
                )
            } else {
                noteAndContent.noteContents[0].content
            }
        } else {
            //如果内容为空  无标题
            "无标题"
        }
    }
    return contentTitle
}