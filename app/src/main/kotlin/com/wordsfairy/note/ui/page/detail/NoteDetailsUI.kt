package com.wordsfairy.note.ui.page.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.clickableNoIndication
import com.wordsfairy.note.ui.common.vibration

import com.wordsfairy.note.ui.page.create.ChooseClassifyButton
import com.wordsfairy.note.ui.page.create.CreateNoteEditView
import com.wordsfairy.note.ui.page.detail.wifgets.*
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*
import com.wordsfairy.note.ui.widgets.dropdown.FolderDropdownMenu
import com.wordsfairy.note.ui.widgets.toast.ToastUI
import com.wordsfairy.note.ui.widgets.toast.ToastUIState

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/9 16:20
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@ExperimentalCoroutinesApi
@Composable
fun NoteDetailsUI(
    onBack: () -> Unit,
    viewModel: NoteDetailsViewModel = hiltViewModel()
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }

    val feedback = LocalHapticFeedback.current

    val noteEntity = GlobalData.noteDetailsNoteEntity!!
    val viewIntentInitial = ViewIntent.Initial(noteEntity)
    var isShowContentModifierDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current


    BackHandler(true) {
        when {
            isShowContentModifierDialog -> {
                isShowContentModifierDialog = false
            }
            else -> {
                intentChannel.trySend(ViewIntent.Clean)
                onBack.invoke()
            }
        }
    }

    LaunchedEffect(viewModel) {
        intentChannel
            .consumeAsFlow()
            .noteStartWith(viewIntentInitial)
            .noteStartWith(ViewIntent.RecentUpdates)
            .onEach(viewModel::processIntent)
            .collect()
    }

    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is SingleEvent.UI.Close -> {
                    focusManager.clearFocus()
                }
            }.unit
        }
    }

    val noteFolders by viewModel.noteFolders.collectAsState(initial = emptyList())

    val noteContents by viewModel.noteContents(noteEntity.noteId)
        .collectAsState(initial = emptyList())

    Box() {
        Column(
            Modifier
                .fillMaxSize()
                .blur(if (isShowContentModifierDialog) 6.dp else 0.dp)
                .background(WordsFairyTheme.colors.whiteBackground)
                .clickableNoIndication(focusManager) //点击无涟漪效果
                .systemBarsPadding()
        ) {
            Row(
                Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.width(12.dp))
                /** 返回 */
                MyIconButton(imageVector = Icons.Rounded.KeyboardArrowLeft,size = 36.dp) {
                    feedback.vibration()
                    intentChannel.trySend(ViewIntent.Clean)
                    onBack.invoke()
                }
                /** 文件夹 */
                Box(Modifier.align(Alignment.CenterVertically)) {
                    var expanded by remember { mutableStateOf(false) }
                    /** 文件夹弹窗 */
                    FolderDropdownMenu(expanded, noteFolders, viewState.selectedFolder, onDismiss = {
                        expanded = false
                    }, onClick = {
                        //选择分类文件夹
                        intentChannel.trySend(ViewIntent.SelectFolder(it))
                    })
                    /** 选择文件夹 */
                    ChooseClassifyButton(viewState.selectedFolder?.name, onClick = {
                        expanded = true
                    })
                }

                Spacer(Modifier.weight(1f))

                /** 切换阅读模式 */
                ReadButton(viewState.uiState == UIState.Read) {
                    val isAdd = viewState.uiState != UIState.Add
                    val state = if (isAdd) UIState.Add else UIState.Read
                    intentChannel.trySend(ViewIntent.UIStateChanged(state))
                    feedback.vibration()
                }
                /** 切换搜索模式 */
                SearchButton(viewState.uiState == UIState.Search) {
                    val isAdd = viewState.uiState != UIState.Add
                    if (isAdd) {
                        intentChannel.trySend(ViewIntent.UIStateChanged(UIState.Add))
                        focusManager.clearFocus()
                    } else {
                        intentChannel.trySend(ViewIntent.InitSearch)
                        intentChannel.trySend(ViewIntent.UIStateChanged(UIState.Search))
                    }
                    feedback.vibration()
                }

                /** 右上角  修改标题 */
                SaveTitleAndSet(viewState.canSaveTitle, saveClick = {
                    intentChannel.trySend(ViewIntent.ModifyTitle)
                }, setClick = {
                    postEventValue(EventBus.NavController,  NavigateRouter.DetailPage.Set)

                })
                Spacer(Modifier.width(16.dp))
            }

            VisibilityViewColumn(visible = viewState.uiState != UIState.Read) {
                /** 标题输入框 */
                CreateNoteEditView(
                    text = viewState.title,
                    placeholder = "标题"
                ) {
                    intentChannel.trySend(ViewIntent.TitleChanged(it))
                }
                Spacer(Modifier.height(3.dp))
                Row {
                    Spacer(Modifier.width(24.dp))
                    /** 时间 */
                    MiniText(text = viewState.recentUpdates)
                    Spacer(Modifier.width(6.dp))
                    /** 条数 */
                    MiniText(text = noteContents.size.toString() + "条")
                }
                val isSearch = viewState.uiState == UIState.Search
                /** 笔记输入框 */
                ContentEditView(
                    viewState.noteContent,
                    isSearch,
                    viewState.canSaveContent,
                    onContentChange = {
                        intentChannel.trySend(ViewIntent.ContentChanged(it))
                    },
                    onSearch = {
                        intentChannel.trySend(ViewIntent.SearchContent(it))
                    },
                    saveNote = {
                        intentChannel.trySend(ViewIntent.AddNoteContent)
                        feedback.vibration()
                    })
            }

            /**
             * 笔记内容
             */
            val searchResultTransition = updateTransition(viewState.uiState== UIState.Search, "showCommentArrangementTransition")



            searchResultTransition.AnimatedContent { isSearchUI->
                if (isSearchUI){
                    /**
                     * 搜索结果
                     */
                    SearchResultList(viewState.searchResultData, onDelete = {
                        intentChannel.trySend(ViewIntent.DeleteContent(it))
                    }, onModify = {
                        //准备修改
                        intentChannel.trySend(ViewIntent.ModifyContentChanged(it))
                        isShowContentModifierDialog = true
                    })
                }else{
                    ContentList(noteContents,
                        onMove = { moveContents ->
                            intentChannel.trySend(ViewIntent.MovePosition(moveContents))
                        }, onDelete = {
                            intentChannel.trySend(ViewIntent.DeleteContent(it))
                        }, onModify = {
                            //准备修改
                            intentChannel.trySend(ViewIntent.ModifyContentChanged(it))
                            isShowContentModifierDialog = true
                        })
                }
            }
        }
    }
    ModifyContentDialog(
        isVisible = isShowContentModifierDialog,
        entity = viewState.modifyNoteContent,
        onDismiss = {
            isShowContentModifierDialog = false
        }, onConfirm = {
            intentChannel.trySend(ViewIntent.ModifyContent(it))
        }
    )
}


fun ColumnScope.toNoteDetailsUI(it: NoteEntity) {
    GlobalData.noteDetailsNoteEntity = it
    postEventValue(EventBus.NavController, NavigateRouter.DetailPage.Detail)
}

