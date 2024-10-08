package com.wordsfairy.note.ui.page.set


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback

import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit

import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import com.wordsfairy.base.utils.searchInBrowser
import com.wordsfairy.note.MainActivity
import com.wordsfairy.note.constants.Constants.URL_BiliBili_WordsFairy
import com.wordsfairy.note.constants.Constants.URL_GITEE
import com.wordsfairy.note.constants.Constants.URL_GITHUB

import com.wordsfairy.note.constants.Constants.URL_JUEJIN
import com.wordsfairy.note.constants.Constants.URL_PRIVACY_PROTECTION
import com.wordsfairy.note.constants.Constants.URL_WordsFairyApp
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import com.wordsfairy.note.utils.getVersionName

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/24 13:22
 */


@Composable
fun SetPageUI(
    viewModel: SetViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }

    val context = LocalContext.current
    val feedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    //当前系统是否深色模式
    val isSystemInDark = isSystemInDarkTheme()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            MyIconButton(imageVector = Icons.Rounded.KeyboardArrowLeft, size = 39.dp) {
                onBack.invoke()
            }
            Title(stringResource(id = AppResId.String.Set), fontSize = 21.sp)
            Spacer(Modifier.weight(1f))
            /** 切换主题 */
            SwitchThemeButton(viewState.darkUI, onClick = { isDark ->
                //切换主题
                intentChannel.trySend(ViewIntent.SwitchTheme(isDark))
                //关闭系统跟随
                intentChannel.trySend(ViewIntent.ThemeFollowSystem(false))
                feedback.vibration()

            })
            Spacer(Modifier.width(12.dp))
        }

        ImmerseCard(Modifier.padding(12.dp)) {
            Column {
                CommonItemSwitch(
                    "夜间模式跟随系统",
                    viewState.themeFollowSystem
                ) { follow ->
                    //  当前主题与系统主题模式不相符时，切换成对应主题
                    if (AppSystemSetManage.darkUI != isSystemInDark) {
                        intentChannel.trySend(ViewIntent.SwitchTheme(isSystemInDark))
                    }
                    //  开启/关闭系统跟随
                    intentChannel.trySend(ViewIntent.ThemeFollowSystem(follow))
                }
                ItemDivider()
                CommonItemSwitch(
                    "关闭部分动画提升流畅度",
                    viewState.closeAnimation
                ) { follow ->
                    AppSystemSetManage.closeAnimation = follow
                    intentChannel.trySend(ViewIntent.CloseAnimation(follow))
                }
                ItemDivider()
                CommonItemIcon("数据恢复/备份") {
                    postEventValue(
                        EventBus.NavController,
                        NavigateRouter.SetPage.NoteData
                    )
                }
            }
        }
        ImmerseCard(Modifier.padding(12.dp)) {
            Column {

                CommonItemIcon("隐私政策") {
                    context.searchInBrowser(URL_PRIVACY_PROTECTION)
                }
                ItemDivider()

                AnimateContentIcon("应用信息") {
                    CommonTextItem(
                        "版本号",
                        "v${MainActivity.CONTEXT.getVersionName()}",
                        horizontalPadding = 0.dp
                    )
                    CommonItemIcon("gitee源码") {
                        context.searchInBrowser(URL_GITEE)
                    }
                    CommonItemIcon("github源码") {
                        context.searchInBrowser(URL_GITHUB)
                    }
                }
                ItemDivider()
                AnimateContentIcon("联系作者") {
                    CommonTextItem(
                        "微信",
                        "ISSWENJIE",
                        horizontalPadding = 0.dp
                    ) {
                        clipboardManager.setText(AnnotatedString("ISSWENJIE"))
                        ToastModel("已复制至剪贴板", ToastModel.Type.Normal).showToast()
                    }
                    CommonTextItem(
                        "微信公众号",
                        "九狼",
                        horizontalPadding = 0.dp
                    ) {
                        clipboardManager.setText(AnnotatedString("九狼"))
                        ToastModel("已复制至剪贴板", ToastModel.Type.Normal).showToast()
                    }
                    CommonTextItem(
                        "QQ",
                        "2021662556",
                        horizontalPadding = 0.dp
                    ) {
                        clipboardManager.setText(AnnotatedString("2021662556"))
                        ToastModel("已复制至剪贴板", ToastModel.Type.Normal).showToast()
                    }
                    CommonTextItem(
                        "博客",
                        "掘金",
                        horizontalPadding = 0.dp
                    ) {
                        context.searchInBrowser(URL_JUEJIN)
                    }
                }
                ItemDivider()
                CommonTextItem("开发者", "九狼") {

                    ToastModel(
                        "祝你有美好的一天  ＼(^▽^＠)ノ ",
                        ToastModel.Type.Success,
                        2000L
                    ).showToast()
                }
            }
        }
        ImmerseCard(Modifier.padding(12.dp)) {
            Column {
                AnimateContentIcon("词仙-笔记加密共享版") {
                    CommonTextItem(
                        "官网下载",
                        "测试版v1.0",
                        horizontalPadding = 0.dp
                    ) {
                        context.searchInBrowser(URL_WordsFairyApp)
                    }
                    CommonTextItem(
                        "UI视频展示",
                        "BiliBili",
                        horizontalPadding = 0.dp
                    ) {
                        context.searchInBrowser(URL_BiliBili_WordsFairy)
                    }
                }
            }
        }
    }

}
