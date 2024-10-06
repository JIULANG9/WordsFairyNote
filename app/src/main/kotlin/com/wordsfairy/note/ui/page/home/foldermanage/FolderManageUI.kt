package com.wordsfairy.note.ui.page.home.foldermanage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.page.create.AddFolderDialog
import com.wordsfairy.note.ui.page.home.foldermanage.widgets.FolderList
import com.wordsfairy.note.ui.page.home.foldermanage.widgets.ModifyFolderDialog
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.GeneralDialog
import com.wordsfairy.note.ui.widgets.ImmerseCard
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
 * @Data: 2023/6/21 21:58
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FolderManageUI(
    viewModel: FolderManageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }
    val feedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val showDeleteWarningDialog = remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        intentChannel
            .consumeAsFlow()
            .noteStartWith(ViewIntent.Initial)
            .onEach(viewModel::processIntent)
            .collect()
    }

    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is SingleEvent.UI.Success -> {
                    focusManager.clearFocus()
                }

                is SingleEvent.UI.CreateFolder -> {
                    intentChannel.trySend(ViewIntent.NoteFolderNameChanged("")).let { }
                }
            }.unit
        }
    }


    val noteInfoList by viewModel.noteInfoList.collectAsState(emptyList())

    var isShowModifierDialog by remember { mutableStateOf(false) }
    var isShowAddFolderDialog by remember { mutableStateOf(false) }


    BackHandler(true) {
        when {
            isShowModifierDialog -> {
                isShowModifierDialog = false
            }

            isShowAddFolderDialog -> {
                isShowAddFolderDialog = false
            }

            else -> {
                onBack()
            }
        }
    }
    val isBlur = isShowModifierDialog || isShowAddFolderDialog
    Column(
        Modifier
            .fillMaxSize()
            .blur(if (isBlur) 9.dp else 0.dp)
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            MyIconButton(
                painter = painterResource(id = AppResId.Drawable.Arrow_Left),
                size = 24.dp
            ) {
                onBack.invoke()
            }
            Title(stringResource(id = AppResId.String.FolderManage), fontSize = 19.sp)
            Spacer(modifier = Modifier.weight(1F))
            AssistChip(onClick = {
                isShowAddFolderDialog = true
                feedback.vibration()

            },
                border = AssistChipDefaults.assistChipBorder(
                    true,
                    borderColor = WordsFairyTheme.colors.themeAccent,
                    disabledBorderColor = WordsFairyTheme.colors.themeAccent
                ),
                colors = AssistChipDefaults.assistChipColors(WordsFairyTheme.colors.themeUi),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add",
                        Modifier.size(24.dp),
                        tint = WordsFairyTheme.colors.textWhite
                    )
                },
                label = {
                    Text(
                        text = "创建",
                        fontSize = 12.sp,
                        color = WordsFairyTheme.colors.textWhite
                    )
                })
            Spacer(Modifier.width(12.dp))

        }
        Spacer(Modifier.width(12.dp))

        FolderList(noteInfoList,
            onModify = {
                intentChannel.trySend(ViewIntent.ModifyFolderChanged(it))
                isShowModifierDialog = true
            },
            onDelete = {
                viewModel.beDeletedFolder = it
                showDeleteWarningDialog.value = true
            },
            onMove = {
                //修改位置
                intentChannel.trySend(ViewIntent.MovePosition(it))
            },
            onCreate = {

            })
    }
    /** 修改笔记文件夹名称 */
    ModifyFolderDialog(
        isVisible = isShowModifierDialog,
        entity = viewState.modifyFolderEntity,
        onDismiss = {
            isShowModifierDialog = false
        }, onConfirm = {
            intentChannel.trySend(ViewIntent.ModifyFolder(it))
        }
    )
    /** 添加笔记文件夹 */
    AddFolderDialog(isShowAddFolderDialog, viewState.addNoteFolderName,
        onValueChange = {
            intentChannel.trySend(
                ViewIntent.NoteFolderNameChanged(
                    it
                )
            )
        },
        onDismiss = {
            isShowAddFolderDialog = false
        },
        onConfirm = {
            intentChannel.trySend(ViewIntent.CreateFolder)
        })

    if (showDeleteWarningDialog.value) {
        GeneralDialog(
            dialogState = showDeleteWarningDialog,
            title = "请谨慎操作",
            message = "清除该文件夹下的笔记和内容，不可恢复，是否继续？",
            isWaring = true,
            positiveBtnText = stringResource(id = AppResId.String.Confirm),
            onPositiveBtnClicked = {
                intentChannel.trySend(ViewIntent.DeleteFolder(viewModel.beDeletedFolder))
            },
            negativeBtnText = stringResource(id = AppResId.String.Cancel),
            onNegativeBtnClicked = {

            }
        )
    }
}