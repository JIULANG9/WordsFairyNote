package com.wordsfairy.note.ui.page.detail.set

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.base.tools.toast
import com.wordsfairy.base.utils.isUTF8

import com.wordsfairy.note.constants.Constants
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.entity.DialogDataBean
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.page.set.AnimateContentIcon
import com.wordsfairy.note.ui.page.set.CommonItemIcon
import com.wordsfairy.note.ui.page.set.CommonItemSwitch
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:01
 */

@Composable
fun ContentSetUI(
    onBack: () -> Unit,
    viewModel: ContentSetViewModel = hiltViewModel()
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }

    val showDialog = remember { mutableStateOf(false) }


    val context = LocalContext.current
    val feedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val txtSelectorLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                context.apply {
                    if (isUTF8(uri)) {
                        GlobalData.importFile = uri
                        GlobalData.createBatchImport = false
                        postEventValue(
                            EventBus.NavController,
                            NavigateRouter.DetailPage.ProgressBarUI
                        )
                    } else {
                        val dialogDataBean = DialogDataBean.create(
                            title = "文件编码不符",
                            message = "文件编码不是UTF-8字符, 导入可能出现乱码，请重新选择UTF-8字符的文件，或者切换文件编码",
                            isWaring = false,
                            clackTag =  ContentSetViewModel.IsNotUTF8Tag
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
            .onEach(viewModel::processIntent)
            .collect()
    }

    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is SingleEvent.UI.ShowDialog -> {
                    showDialog.value =true
                }
            }.unit
        }
    }

    BackHandler(true) {
        onBack()
    }
    Box() {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Title(stringResource(id = AppResId.String.Set), fontSize = 21.sp)
                Spacer(Modifier.weight(1f))
            }

            ImmerseCard(Modifier.padding(12.dp)) {
                Column {
                    AnimateContentIcon("数据批量导出/导入") {
                        CommonItemIcon("导入 [txt]") {
                            txtSelectorLauncher.launch("text/plain")
                        }
                        CommonItemIcon("导出(即将开发)") {
                            context.toast("即将开发")
                        }
                    }
                    ItemDividerSetUI()
                    CommonItemSwitch(
                        "复制后转跳微信",
                        viewState.jumpToWeChat
                    ) { jump ->
                        intentChannel.trySend(ViewIntent.CopyJumpToWeChat(jump))
                    }
                    ItemDividerSetUI()

                    AnimateContentIcon("搜索引擎") {
                        val baidu = Constants.SearchEngines.Baidu
                        val bing = Constants.SearchEngines.Bing
                        val google = Constants.SearchEngines.Google

                        val baiduIcon =
                            if (viewState.currentUrl == baidu) AppResId.Drawable.Correct else null
                        CommonItemIcon("百度", iconId = baiduIcon) {
                            intentChannel.trySend(ViewIntent.SearchEngines(baidu))
                        }
                        val bingIcon =
                            if (viewState.currentUrl == bing) AppResId.Drawable.Correct else null
                        CommonItemIcon("必应", iconId = bingIcon) {
                            intentChannel.trySend(ViewIntent.SearchEngines(bing))
                        }
                        val googleIcon =
                            if (viewState.currentUrl == google) AppResId.Drawable.Correct else null
                        CommonItemIcon("谷歌", iconId = googleIcon) {
                            intentChannel.trySend(ViewIntent.SearchEngines(google))
                        }
                    }
                    ItemDividerSetUI()

                    AnimateContentIcon("清除数据") {
                        CommonItemIcon("清除此笔记", textColor = AppColor.red) {
                            val dialogDataBean = DialogDataBean.create(
                                title = "清除数据",
                                message = "清除此笔记",
                                isWaring = true,
                                clackTag =  ContentSetViewModel.RecycleNoteTag
                            )
                            intentChannel.trySend(ViewIntent.ShowDialog(dialogDataBean))
                        }
                        CommonItemIcon("清除此笔记所有内容", textColor = AppColor.red) {

                            val dialogDataBean = DialogDataBean.create(
                                title = "清除数据",
                                message = "清除此笔记所有内容",
                                isWaring = true,
                                clackTag =  ContentSetViewModel.RecycleNoteContentsTag
                            )
                            intentChannel.trySend(ViewIntent.ShowDialog(dialogDataBean))
                        }
                    }
                }
            }
        }
        if (showDialog.value) {
            GeneralDialog(
                dialogState = showDialog,
                title = viewState.dialogDataBean.title,
                message = viewState.dialogDataBean.message,
                isWaring = viewState.dialogDataBean.isWaring,
                positiveBtnText = stringResource(id = AppResId.String.Confirm),
                onPositiveBtnClicked = {
                    when (viewState.dialogDataBean.clackTag) {
                        ContentSetViewModel.RecycleNoteTag ->{
                            intentChannel.trySend(ViewIntent.RecycleNote)
                        }
                        ContentSetViewModel.RecycleNoteContentsTag ->{
                            intentChannel.trySend(ViewIntent.RecycleNoteContents)
                        }
                        ContentSetViewModel.IsNotUTF8Tag ->{
                            txtSelectorLauncher.launch("text/plain")
                        }
                    }
                },
                negativeBtnText = stringResource(id = AppResId.String.Cancel),
                onNegativeBtnClicked = {

                }
            )
        }
    }
}

/**
 * 分割线
 */
@Composable
private fun ItemDividerSetUI() {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = WordsFairyTheme.colors.textSecondary,
        thickness = 0.3.dp
    )
}
