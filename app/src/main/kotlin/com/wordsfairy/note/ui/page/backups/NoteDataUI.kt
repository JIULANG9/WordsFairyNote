package com.wordsfairy.note.ui.page.backups

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.base.tools.toast
import com.wordsfairy.common.tools.DATE_FORMAT_Month_Day_Time_Second
import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.room.db.AppDataBase
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.page.detail.set.ContentSetViewModel
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.AlertInfo
import com.wordsfairy.note.ui.widgets.AlertSuccess
import com.wordsfairy.note.ui.widgets.AlertWarning
import com.wordsfairy.note.ui.widgets.ButtonPrimitive
import com.wordsfairy.note.ui.widgets.GeneralDialog
import com.wordsfairy.note.ui.widgets.MyIconButton
import com.wordsfairy.note.ui.widgets.TextContent
import com.wordsfairy.note.ui.widgets.Title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/12 13:01
 */


@Composable
fun NoteDataUI(
    onBack: () -> Unit,
    viewModel: NoteDataViewModel = hiltViewModel(),
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }

    val showDialog = remember { mutableStateOf(false) }


    LaunchedEffect(viewModel) {
        intentChannel
            .consumeAsFlow()
            .noteStartWith(ViewIntent.Initial)
            .onEach(viewModel::processIntent)
            .collect()
    }

    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->

        }
    }
    val context = LocalContext.current

    val backupsResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
            val uri = data.data?.data
            if (uri != null) {
                val  noteUri = viewModel.getNoteUri(context, uri)
                if (noteUri != null && noteUri.isDirectory) {
                    GlobalData.backupsSelectFolderUri = noteUri.uri
                    context.postEventValue(
                        EventBus.NavController,
                        NavigateRouter.SetPage.BackupsProgressBar
                    )
                    intentChannel.trySend(ViewIntent.Backups)
                }else{
                    context.toast("文件夹创建文件出错!")
                }
            }else{
                context.toast("选择困难!")
            }
        }
    val importResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
            val uri = data.data?.data
            if (uri != null) {
                GlobalData.importFolderUri = uri
                context.postEventValue(
                    EventBus.NavController,
                    NavigateRouter.SetPage.BackupsProgressBar
                )
                intentChannel.trySend(ViewIntent.Import)
            }else{
                context.toast("选择困难!")
            }
        }

    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding()

    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            MyIconButton(imageVector = Icons.Rounded.KeyboardArrowLeft, size = 39.dp) {
                onBack.invoke()
            }
            Title(stringResource(id = AppResId.String.DataRecoveryBackup), fontSize = 21.sp)
        }
        Column(
            Modifier.verticalScroll(scrollState)
        ) {
            AlertSuccess(
                modifier = Modifier.padding(12.dp),
                icon = null,
                title = {
                    Title(title = "备份导出")
                },
                content = {
                    val content = "选择用于储存保存的数据的文件夹"
                    TextContent(content)
                },
                actions = {
                    ButtonPrimitive("导出数据据",
                        primitiveColor = WordsFairyTheme.colors.themeUi,
                        isAccent = true,
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            backupsResult.launch(intent)
                        })
                }
            )
            AlertSuccess(
                modifier = Modifier.padding(12.dp),
                icon = null,
                title = {
                    Title(title = "数据恢复")
                },
                content = {
                    TextContent("选择有一个用于恢复文件夹")
                    TextContent("${Constants.File.WordsFairyNote} $DATE_FORMAT_Month_Day_Time_Second")
                },
                actions = {
                    ButtonPrimitive("选择恢复数据 ",
                        primitiveColor = WordsFairyTheme.colors.themeUi,
                        onClick = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            importResult.launch(intent)
                        })
                }
            )
            AlertInfo(
                icon = null,
                modifier = Modifier.padding(12.dp),
                title = {
                    Title(title = "文件夹结构")
                },
                content = {
                    TextContent(
                        "笔记文件夹\n" +
                                "|\n" +
                                "+------+------+\n" +
                                "|\t\t\t\t\t\t\t\t\t\t\t\t\t\t|\n" +
                                "笔记标题\t\t\t\t\t\t笔记标题\n" +
                                "|\t\t\t\t\t\t\t\t\t\t\t\t\t\t|\n" +
                                "\t+------+\t\t\t\t\t\t+------+\n" +
                                "\t|\t\t\t\t\t\t|\t\t\t\t\t\t|\t\t\t\t\t\t|\n" +
                                "\t笔记\t\t\t笔记\t\t\t笔记\t\t\t笔记\n" +
                                "\t|\t\t\t\t\t\t|\t\t\t\t\t\t|\t\t\t\t\t\t|\n" +
                                "\t...\t\t\t\t\t\t...\t\t\t\t\t\t...\t\t\t\t\t\t...",
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }
            )
            AlertWarning(
                modifier = Modifier.padding(12.dp),
                title = {
                    Title(title = "请谨慎操作")
                },
                content = {
                    TextContent("清除所有数据，包括文件夹、笔记、内容等")
                },
                actions = {
                    ButtonPrimitive(
                        "清除所有数据 ", onClick = {
                            showDialog.value = true
                        },
                        primitiveColor = WordsFairyTheme.colors.error
                    )
                }
            )
        }
    }

    if (showDialog.value) {
        GeneralDialog(
            dialogState = showDialog,
            title = "请谨慎操作",
            message = "清除所有数据\n包括文件夹、笔记、内容等",
            isWaring = true,
            positiveBtnText = stringResource(id = AppResId.String.Confirm),
            onPositiveBtnClicked = {
                viewModel.clearAllTables()
                context.toast("清除成功!")
            },
            negativeBtnText = stringResource(id = AppResId.String.Cancel),
            onNegativeBtnClicked = {

            }
        )
    }
}



@Preview(showBackground = false)
@Composable
private fun NoteDataUIPreview() {
    NoteDataUI(onBack = {})
}