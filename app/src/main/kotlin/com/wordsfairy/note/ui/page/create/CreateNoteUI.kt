package com.wordsfairy.note.ui.page.create

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.base.tools.toast
import com.wordsfairy.base.utils.isUTF8
import com.wordsfairy.note.MainActivity
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.entity.DialogDataBean
import com.wordsfairy.note.data.entity.NoteContentEntity
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.observeEvent
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.clickableNoIndication
import com.wordsfairy.note.ui.common.vibrationFeedback
import com.wordsfairy.note.ui.page.detail.set.ContentSetViewModel
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*
import com.wordsfairy.note.ui.widgets.dropdown.FolderDropdownMenu
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/28 17:04
 */

@ExperimentalComposeUiApi
@Composable
fun CreateNoteUI(
    onBack: () -> Unit,
    viewModel: CreateNoteViewModel = hiltViewModel()
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }

    //笔记文件夹
    val noteFolders by viewModel.noteFolders.collectAsState(initial = emptyList())
    var isShowAddFolderDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current as MainActivity
    val feedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val noteContentItems = remember { viewState.noteContentItems }

    context.observeEvent(key = EventBus.CreateBatchImport) {
        val list = it as List<NoteContentEntity>
        noteContentItems.addAll(list)
    }
    /**
     * dialog
     */
    val showDialog = remember { mutableStateOf(false) }

    /**
     * 批量导入
     */
    val txtSelectorLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                context.apply {
                    if (isUTF8(uri)) {
                        GlobalData.run {
                            importFile = uri
                            createBatchImport = true
                        }
                        postEventValue(
                            EventBus.NavController,
                            NavigateRouter.DetailPage.ProgressBarUI
                        )
                    } else {
                        val dialogDataBean = DialogDataBean.create(
                            title = "文件编码不符",
                            message = "文件编码不是UTF-8字符, 导入可能出现乱码，请重新选择UTF-8字符的文件，或者切换文件编码",
                            isWaring = false,
                            clackTag = CreateNoteViewModel.IsNotUTF8Tag
                        )
                        intentChannel.trySend(ViewIntent.ShowDialog(dialogDataBean))
                    }
                }
            } else {
                context.toast("选择困难！")
            }
        }


    LaunchedEffect(viewModel) {
        intentChannel
            .consumeAsFlow()
            .noteStartWith(ViewIntent.Initial)
            .noteStartWith(ViewIntent.GetCurrentTime)
            .onEach(viewModel::processIntent)
            .collect()
    }
    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is SingleEvent.UI.Toast -> {
                    context.toast(event.content)
                }
                is SingleEvent.UI.AddNoteContent -> {
                    noteContentItems.add(0, event.noteContent)
                }
                is SingleEvent.UI.ShowDialog -> {
                    showDialog.value = true
                }
            }.unit
        }
    }

    BackHandler(true) {
        intentChannel.trySend(ViewIntent.Clean)
        onBack()
    }
    Box {
        Column(
            Modifier
                .fillMaxSize()
                .background(WordsFairyTheme.colors.whiteBackground)
                .clickableNoIndication(focusManager)
                .systemBarsPadding()
        ) {
            Row(
                Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.width(16.dp))
                /** 返回 */
                MyIconButton(imageVector = Icons.Rounded.ArrowBack) {
                    intentChannel.trySend(ViewIntent.Clean)
                    onBack.invoke()
                }
                Spacer(Modifier.width(16.dp))
                /** 文件夹 */
                Box(Modifier.align(Alignment.CenterVertically)) {
                    var expanded by remember { mutableStateOf(false) }
                    /** 文件夹弹窗 */
                    FolderDropdownMenu(
                        expanded,
                        noteFolders,
                        viewState.selectedFolder,
                        onDismiss = {
                            expanded = false
                        },
                        onClick = {
                            //选择分类文件夹
                            intentChannel.trySend(ViewIntent.SelectFolder(it))
                        })

                    ChooseClassifyButton(viewState.selectedFolder?.name, onClick = {
                        expanded = true
                    }, longClick = {
                        isShowAddFolderDialog = true
                        feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    })
                }
                Spacer(Modifier.weight(1f))
                /** 右上角  保存标题 */
                ButtonSaveTitle(viewState.canSaveTitle) {
                    intentChannel.trySend(ViewIntent.CreateNoteEntity)
                }
                Spacer(Modifier.width(16.dp))

            }
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
                MiniText(text = viewState.currentTime)
                Spacer(Modifier.width(6.dp))
                /** 字数 */
                MiniText(text = viewState.wordNumber)
            }
            /** 笔记输入框 */
            NoteContentEditView(viewState, intentChannel, txtSelector = {
                txtSelectorLauncher.launch("text/plain")
            })

            /** 笔记 列表 */
            NoteContentLazyColumn(noteContentItems)

        }
        /** 添加笔记文件夹名称 */
        AddFolderDialog(isShowAddFolderDialog, viewState.addNoteFolderName,
            onValueChange = {
                intentChannel.trySend(ViewIntent.NoteFolderNameChanged(it))
            },
            onDismiss = {
                isShowAddFolderDialog = false
                intentChannel.trySend(ViewIntent.NoteFolderNameChanged(""))
            },
            onConfirm = { name ->
                intentChannel.trySend(ViewIntent.CreateFolder)
            })
        if (showDialog.value) {
            GeneralDialog(
                dialogState = showDialog,
                title = viewState.dialogDataBean.title,
                message = viewState.dialogDataBean.message,
                isWaring = viewState.dialogDataBean.isWaring,
                positiveBtnText = stringResource(id = AppResId.String.Confirm),
                onPositiveBtnClicked = {
                    when (viewState.dialogDataBean.clackTag) {
                        CreateNoteViewModel.IsNotUTF8Tag -> {
                            txtSelectorLauncher.launch("text/plain")
                        }
                    }
                },
                negativeBtnText = stringResource(id = AppResId.String.Cancel)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteContentEditView(
    viewState: ViewState,
    intentChannel: Channel<ViewIntent>,
    txtSelector: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    ImmerseCard(
        modifier = Modifier.padding(12.dp),
        elevation = 2.dp
    ) {
        Column(
            Modifier.padding(6.dp)
        ) {
            var appendTextValue by remember { mutableStateOf("") }
            /** 笔记输入框 */
            CreateNoteContentEditView(
                text = viewState.noteContent,
                addendText = appendTextValue,
                placeholder = "开始书学"
            ) {
                intentChannel.trySend(ViewIntent.NoteContentChanged(it))
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.width(6.dp))
                if (viewState.noteEntity != null) {
                    MyButton("批量创建") {
                        GlobalData.noteDetailsNoteEntity = viewState.noteEntity
                        txtSelector.invoke()
                    }
                }
                Spacer(Modifier.weight(1f))
                MyButton("剪贴板", color = AppColor.blue) {
                    val clipboardText = clipboardManager.getText()?.text ?: ""
                    appendTextValue = clipboardText
                }
                Spacer(Modifier.width(6.dp))
                MyButton("保存", enabled = viewState.canSaved) {
                    intentChannel.trySend(ViewIntent.SaveNote)
                }
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteContentEditView(
    noteContent: String,
    canSaved: Boolean,
    onContentChange: (String) -> Unit = {},
    saveNote: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val feedback = LocalHapticFeedback.current

    ImmerseCard(
        modifier = Modifier.padding(12.dp),
        elevation = 2.dp
    ) {
        Column(
            Modifier.padding(6.dp)
        ) {
            var appendTextValue by remember { mutableStateOf("") }
            /** 笔记输入框 */
            CreateNoteContentEditView(
                text = noteContent,
                addendText = appendTextValue,
                placeholder = "开始书学",
                isAutoFocused = false
            ) {
                onContentChange.invoke(it)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                Spacer(Modifier.weight(1f))
                MyButton("剪贴板", color = AppColor.blue) {
                    val clipboardText = clipboardManager.getText()?.text ?: ""
                    appendTextValue = clipboardText

                }
                Spacer(Modifier.width(6.dp))
                MyButton("保存", enabled = canSaved) {
                    saveNote.invoke()
                    vibrationFeedback(feedback)
                }
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateNotePage(
    pageState: CreateNoteState,
    cardSize: IntSize,
    fullSize: IntSize,
    cardOffset: IntOffset,
    onPageClosing: () -> Unit,
    onPageClosed: () -> Unit
) {
    var animReady by remember { mutableStateOf(false) }
    var animFinish by remember { mutableStateOf(false) }
    val background by animateColorAsState(
        if (pageState == CreateNoteState.Closing) AppColor.themeColor else Color.Transparent)
    val alpha by animateFloatAsState(
        targetValue = if (pageState == CreateNoteState.Closing) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300)
    )

    val DEPLOYMENT_DURATION = 500
    val size by animateIntSizeAsState(if (pageState > CreateNoteState.Closed) fullSize else cardSize,
        animationSpec = tween(DEPLOYMENT_DURATION))

    val fullOffset = remember { IntOffset(0, 0) }
    val offsetAnimatable = remember { Animatable(IntOffset(0, 0), IntOffset.VectorConverter) }
    val cornerSize by animateDpAsState(if (animFinish) 0.dp  else 16.dp)


    LaunchedEffect(pageState) {
        when (pageState) {
            CreateNoteState.Opening -> {
                animReady = true
                offsetAnimatable.snapTo(cardOffset)
                offsetAnimatable.animateTo(fullOffset,animationSpec = tween(DEPLOYMENT_DURATION))
                animFinish = true
            }
            CreateNoteState.Closing -> {
                animFinish = false
                offsetAnimatable.snapTo(fullOffset)
                offsetAnimatable.animateTo(cardOffset,animationSpec = tween(DEPLOYMENT_DURATION))
                animReady = false
                onPageClosed()
            }
            else -> {}
        }
    }
    if (pageState != CreateNoteState.Closed && animReady) {
        Box(
            Modifier
                .offset { offsetAnimatable.value }
                .clip(RoundedCornerShape(cornerSize))
                .width(with(LocalDensity.current) { size.width.toDp() })
                .height(with(LocalDensity.current) { size.height.toDp() })

        ) {
            CreateNoteUI(onBack = onPageClosing)
            if (pageState == CreateNoteState.Closing){
                Box(Modifier.fillMaxSize()
                    .alpha(alpha)
                    .background(background))
            }
        }
    }
}

enum class CreateNoteState {
    Closing, Closed, Opening
}
