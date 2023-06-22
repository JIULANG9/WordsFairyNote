package com.wordsfairy.note.ui.page.home.foldermanage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ui.page.create.AddFolderDialog
import com.wordsfairy.note.ui.page.home.foldermanage.widgets.FolderList
import com.wordsfairy.note.ui.page.home.foldermanage.widgets.ModifyFolderDialog
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.TopLayout
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
            .blur(if (isBlur) 3.dp else 0.dp)
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding()
    ) {
        TopLayout(
            onBack = onBack,
            titleId = AppResId.String.FolderManage
        )

        Spacer(Modifier.width(12.dp))

        FolderList(noteInfoList,
            onModify = {
                intentChannel.trySend(ViewIntent.ModifyFolderChanged(it))
                isShowModifierDialog = true
            },
            onDelete = {
                intentChannel.trySend(ViewIntent.DeleteFolder(it))

            },
            onMove = {
                //修改位置
                intentChannel.trySend(ViewIntent.MovePosition(it))
            },
            onCreate = {
                isShowAddFolderDialog = true
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
            intentChannel.trySend(
                ViewIntent.NoteFolderNameChanged("")
            )
        },
        onConfirm = {
            intentChannel.trySend(ViewIntent.CreateFolder)
        })
}