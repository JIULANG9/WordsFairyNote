package com.wordsfairy.note.ui.page.detail.search



import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ui.common.autoCloseKeyboard
import com.wordsfairy.note.ui.common.clickableNoIndication
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.widgets.SearchEditView
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:00
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSearchUI(
    onBack: () -> Unit,
    viewModel: ContentSearchViewModel = hiltViewModel()
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
                is SingleEvent.UI.Success ->{
                    focusManager.clearFocus()
                }
            }.unit
        }
    }

    BackHandler(true) {
        onBack()
    }

    Column(
        Modifier
            .fillMaxSize()
            .clickableNoIndication(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchEditView(
            viewState.keyword,
            stringResource(id = AppResId.String.Search),
            Modifier.padding(horizontal = 16.dp),
            onValueChanged = {

            },
            onDeleteClick = {

            },
            onSearch = {}
        )
    }
}