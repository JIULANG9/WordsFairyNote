package com.wordsfairy.note.ui.page.backups

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.AlertWarning
import com.wordsfairy.note.ui.widgets.ButtonPrimitive
import com.wordsfairy.note.ui.widgets.CommonTextItem
import com.wordsfairy.note.ui.widgets.GeneralDialog
import com.wordsfairy.note.ui.widgets.ImmerseCardItem
import com.wordsfairy.note.ui.widgets.ItemDivider
import com.wordsfairy.note.ui.widgets.MiniText
import com.wordsfairy.note.ui.widgets.MyIconButton
import com.wordsfairy.note.ui.widgets.TextContent
import com.wordsfairy.note.ui.widgets.Title
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.ToastUI
import com.wordsfairy.note.ui.widgets.toast.ToastUIState
import com.wordsfairy.note.ui.widgets.toast.showToast
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
//            when (event) {
//
//                else -> {}
//            }
        }
    }
    val context = LocalContext.current

    val backupsResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
            val uri = data.data?.data
            if (uri != null) {
                val noteUri = viewModel.getNoteUri(context, uri)
                if (noteUri != null && noteUri.isDirectory) {
                    GlobalData.backupsSelectFolderUri = noteUri.uri
                    context.postEventValue(
                        EventBus.NavController,
                        NavigateRouter.SetPage.BackupsProgressBar
                    )
                    intentChannel.trySend(ViewIntent.Backups)
                } else {

                    ToastModel("文件夹创建文件出错", ToastModel.Type.Error).showToast()
                }
            } else {
                ToastModel("选择困难! ƪ(˘⌣˘)ʃ", ToastModel.Type.Info).showToast()
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
            } else {
                ToastModel("选择困难! ƪ(˘⌣˘)ʃ", ToastModel.Type.Info).showToast()
            }
        }

    val scrollState = rememberScrollState()
    Box() {
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
            Column {
//            Spacer(Modifier.height(12.dp))
//            MiniText(text = "二维码快捷导入", Modifier.padding(start = 32.dp))
//            ImmerseCardItem(Modifier.padding(12.dp)) {
//                CommonItemIcon("生成二维码") {
//                    intentChannel.trySend(ViewIntent.DataToQRCode)
//                    context.postEventValue(
//                        EventBus.NavController,
//                        NavigateRouter.SetPage.BackupsQRCode
//                    )
//                }
//                ItemDivider()
//                CommonItemIcon("扫描二维码导入") {
//                }
//            }
                Spacer(Modifier.height(12.dp))
                MiniText(text = "文件备份", Modifier.padding(start = 32.dp))
                ImmerseCardItem(Modifier.padding(12.dp)) {
                    CommonTextItem(
                        "导出",
                        "选择用于储存保存的数据的文件夹",
                        horizontalPadding = 6.dp
                    ) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        backupsResult.launch(intent)
                    }
                    ItemDivider()
                    CommonTextItem(
                        "导入",
                        "选择${Constants.File.WordsFairyNote}文件夹",
                        horizontalPadding = 6.dp
                    ) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        importResult.launch(intent)
                    }
                }

                Spacer(Modifier.weight(1f))
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
                ToastModel("清除成功!", ToastModel.Type.Normal).showToast()

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