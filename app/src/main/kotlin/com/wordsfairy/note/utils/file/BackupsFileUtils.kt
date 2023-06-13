package com.wordsfairy.note.utils.file


import android.net.Uri

import androidx.documentfile.provider.DocumentFile
import com.wordsfairy.note.base.BaseApplication
import com.wordsfairy.note.data.entity.NoteContentEntity
import java.io.BufferedWriter

import java.io.OutputStreamWriter

/**
 * 创建笔记内容txt文件
 */
suspend fun contentEntityToTxtFile( contentEntity: List<NoteContentEntity>, fileName: String, folderUri: Uri,callBack: suspend ()->Unit) {
    val mContext = BaseApplication.CONTEXT
    val folder = DocumentFile.fromTreeUri(mContext, folderUri)
    val file = folder?.createFile("text/plain", fileName)
    val uri = file?.uri
    if (uri != null) {
        mContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream).use { outputStreamWriter ->
                BufferedWriter(outputStreamWriter).use { bufferedWriter ->
                    contentEntity.forEach { data ->
                        val line = data.content
                        bufferedWriter.write(line)
                        bufferedWriter.newLine()
                        callBack.invoke()
                    }
                }
            }
        }
    }
}

/**
 * 读取所有的子文件夹名称
 * 接着读取的子文件夹面所有的txt文件
 * 名称再接着按行读取txt文件的内容。
 * 根据行数 返回总数
 * @param uri Uri
 * @return Pair<Int, List<Pair<String, List<Pair<String, List<String>>>>>>
 */
fun readTxtFilesFromUri(uri: Uri): Pair<Int, List<Pair<String, List<Pair<String, List<String>>>>>> {
    var lineCount = 0
    val context = BaseApplication.CONTEXT

    val result = mutableListOf<Pair<String, List<Pair<String, List<String>>>>>()
    val folder = DocumentFile.fromTreeUri(context, uri)
    folder?.listFiles()?.forEach { subFolder ->
        if (subFolder.isDirectory) {
            val subFolderName = subFolder.name
            val subFolderResult = mutableListOf<Pair<String, List<String>>>()
            subFolder.listFiles().forEach { file ->
                if (file.isFile && file.name?.endsWith(".txt") == true) {
                    //去除.txt后缀
                    val fileName = file.name?.substring(0, file.name?.length!! - 4)
                    val content = mutableListOf<String>()
                    context.contentResolver.openInputStream(file.uri)?.bufferedReader()?.useLines { lines ->
                        lines.forEach {
                            content.add(it)
                            lineCount++
                        }
                    }
                    subFolderResult.add(Pair(fileName ?: "", content))
                }
            }
            result.add(Pair(subFolderName ?: "", subFolderResult))
        }
    }
    return Pair(lineCount, result)
}
