package com.wordsfairy.note.ui.page.backups

import androidx.documentfile.provider.DocumentFile
import com.wordsfairy.note.base.BaseApplication
import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.entity.NoteAndNoteContent
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.data.entity.NoteInfo
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.utils.file.contentEntityToTxtFile
import com.wordsfairy.note.utils.file.readTxtFilesFromUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader


/**
 * 导入文件
 * @param callBackSchedule SuspendFunction1<Float, Unit>
 */
suspend fun importFile(
    folderRepository: NoteFolderRepository,
    noteEntityRepository: NoteEntityRepository,
    contentRepository: NoteContentRepository,
    callBackSchedule: suspend (Float) -> Unit,
) {
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
 * 导入Json文件
 */
suspend fun importJsonFile(
    folderRepository: NoteFolderRepository,
    noteEntityRepository: NoteEntityRepository,
    contentRepository: NoteContentRepository,
    error: suspend (String) -> Unit = {},
    callBackSchedule: suspend (Float) -> Unit,
) {
    val folderUri = GlobalData.importFolderUri!!

    val documentFile = DocumentFile.fromSingleUri(BaseApplication.CONTEXT, folderUri)
    //文件名称
    if (documentFile == null) {
        error.invoke("文件不存在")
        return
    }
    val fileName = documentFile.name
    if (fileName != Constants.File.BackupFileName) {
        error.invoke("文件名错误")
        return
    }

    BaseApplication.CONTEXT.contentResolver.openInputStream(documentFile.uri)?.bufferedReader()
        .use { reader ->
            val jsonString = reader?.use(BufferedReader::readText)
            if (!jsonString.isNullOrEmpty()) {
                // 使用 kotlinx.serialization 反序列化 JSON
                try {
                    val allNoteInfo: List<NoteInfo> = Json.decodeFromString(jsonString)

                    val total =
                        allNoteInfo.sumOf { it.noteAndNoteContents.sumOf { note -> note.noteContents.size } }

                    var currentSchedule = 0
                    allNoteInfo.forEach { noteInfo ->

                        val folderId = folderRepository.insert(noteInfo.noteFolder)

                        noteInfo.noteAndNoteContents.forEach { noteAndContent ->

                            val note = noteAndContent.noteEntity.copy(folderId = folderId)
                            val noteId = noteEntityRepository.insert(note)
                            val noteContents =
                                noteAndContent.noteContents.mapIndexed { index, content ->
                                    currentSchedule++
                                    // callBackSchedule(currentSchedule.toFloat() / total.toFloat() * 100)
                                    content.copy(
                                        noteId = noteId
                                    )
                                }
                            contentRepository.insert(noteContents)
                        }
                    }
                } catch (e: Exception) {
                    println("XLog:JSON 解析错误: ${e.message}")
                    error.invoke("数据格式有误")
                    return
                }
            } else {
                println("XLog:文件为空或读取失败")
                error.invoke("文件为空或读取失败")
                return
            }
            // 由于速度太快，为了美观，所以演示度进
            for (i in 1..100) {
                // 模拟耗时操作
                delay(36)
                callBackSchedule(i.toFloat())
            }
        }
}

/**
 *  导出文件
 * @param callBackSchedule SuspendFunction1<Float, Unit>
 */
suspend fun exportFile(
    noteRepository: NoteRepository,
    error: suspend (String) -> Unit = {},
    callBackSchedule: suspend (Float) -> Unit,
) {
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
        error("文件夹不存在")
        return
    }
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
 *  导出文件
 * @param callBackSchedule SuspendFunction1<Float, Unit>
 */

suspend fun exportToJsonFile(
    noteRepository: NoteRepository,
    error: suspend (String) -> Unit = {},
    callBackSchedule: suspend (Float) -> Unit,
) {
    // 设置导出路径
    val path = GlobalData.backupsSelectFolderUri!!
    val documentFile = DocumentFile.fromTreeUri(BaseApplication.CONTEXT, path)
    if (documentFile == null) {
        error("文件夹不存在")
        return
    }
    // JSON 序列化对象
    val json = Json { prettyPrint = true }
    // 创建笔记文件夹
    val backupFile = documentFile.createFile("application/json", Constants.File.BackupFileName)
    if (backupFile == null) {
        error("无法创建备份文件")
        return
    }
    // 获取所有的笔记信息
    val allNoteInfo = noteRepository.getAllNoteInfo()
    // 将笔记信息序列化为 JSON 并保存到文件中
    allNoteInfo.map {
        //设置noteInfo.noteFolder id为0
        it.forEach { noteInfo ->
            noteInfo.noteFolder.folderId = 0
            noteInfo.noteAndNoteContents.forEach { noteAndContent ->
                noteAndContent.noteEntity.noteId = 0
                noteAndContent.noteContents.forEach { content ->
                    content.noteContextId = 0
                }
            }
        }

        val allNoteInfoJson = json.encodeToString(it)
        //验证allNoteInfoJson 是否正确
        Json.decodeFromString<List<NoteInfo>>(allNoteInfoJson)
        if (allNoteInfoJson.isEmpty()) {
            error("数据格式有误")
            return@map
        }

        BaseApplication.CONTEXT.contentResolver.openOutputStream(backupFile.uri)
            ?.use { outputStream ->
                outputStream.write(allNoteInfoJson.toByteArray())
            }
        for (i in 1..100) {
            // 模拟耗时操作
            delay(36)
            callBackSchedule(i.toFloat())
        }

    }.cancellable().catch { e ->
        error.invoke("处理数据时发生错误: ${e.message}")
    }.collect()
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