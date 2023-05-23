package com.wordsfairy.note.ui.page.progress


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wordsfairy.base.tools.toast

import com.wordsfairy.base.utils.readTxtFileByLinesWithProgress
import com.wordsfairy.note.constants.EventBus

import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.data.room.db.AppDataBase
import com.wordsfairy.note.ext.flowbus.postEventValue

import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.IndicatorComponent
import kotlinx.coroutines.*

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/17 11:09
 */

@Composable
fun ProgressBarUI(
    onBack: () -> Unit,
) {

    var progress by remember { mutableStateOf(0F) }
    val context = LocalContext.current

    /**
     * 读取文本
     */

    LaunchedEffect(Unit) {
        val contents = mutableListOf<NoteContentEntity>()
        try {
            withContext(Dispatchers.IO) {
                val uri = GlobalData.importFile!!
                val noteId = GlobalData.noteDetailsNoteEntity!!.noteId
                val lines = context.readTxtFileByLinesWithProgress(uri)
                val createdAt = System.currentTimeMillis()
                var maxPosition = AppDataBase.getInstance().noteContentDao().getMaxPosition(noteId)

                lines.forEach { (line, pro) ->
                    delay(20)
                    maxPosition += 1
                    progress = pro
                    contents.add(NoteContentEntity.create(noteId, line, createdAt, maxPosition))
                }

                AppDataBase.getInstance().noteContentDao().insertNotes(contents)
            }
            progress = 100F
            //[CreateNoteUI] 批量创建

            if (GlobalData.createBatchImport) {
                contents.reverse()
                postEventValue(EventBus.CreateBatchImport, contents)
            }
            delay(30)
            onBack.invoke()
        } catch (e: Exception) {
            context.toast(e.message.toString())
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        IndicatorComponent(progress)
    }
}
