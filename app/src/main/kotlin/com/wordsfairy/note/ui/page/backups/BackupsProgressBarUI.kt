package com.wordsfairy.note.ui.page.backups

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.IndicatorComponent
import com.wordsfairy.note.ui.widgets.TextContent
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.ToastUI
import com.wordsfairy.note.ui.widgets.toast.ToastUIState
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * @Description:备份进度条
 * @Author: JIULANG
 * @Data: 2023/6/12 11:05
 */

@Composable
fun BackupsProgressBarUI(
    onBack: () -> Unit,
    viewModel: NoteDataViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewStateFlow.collectAsState()
    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val feedback = LocalHapticFeedback.current
    val context = LocalContext.current


    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is SingleEvent.UI.Success -> {
                    feedback.performHapticFeedback(HapticFeedbackType.LongPress)

                    ToastModel("完成!", ToastModel.Type.Normal).showToast()
                    onBack.invoke()
                }

                else -> {}
            }.unit
        }
    }
    BackHandler(true) {
        onBack()
    }
    val progress = viewState.progress

    Column(
        Modifier
            .fillMaxSize()
            .background(WordsFairyTheme.colors.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
       // TextContent(text = viewState.progress)

        IndicatorComponent(progress)
    }
}
