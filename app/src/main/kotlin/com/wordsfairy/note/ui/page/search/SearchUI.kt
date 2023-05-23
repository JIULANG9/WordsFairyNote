package com.wordsfairy.note.ui.page.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback

import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.clickableNoIndication
import com.wordsfairy.note.ui.page.search.widgets.ResultList
import com.wordsfairy.note.ui.page.search.widgets.SearchEdit
import com.wordsfairy.note.ui.widgets.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 13:59
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUI(
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
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
            .noteStartWith(ViewIntent.InitAllDataFlow)
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
            .statusBarsPadding()
            .clickableNoIndication(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchEdit(viewState.keyword){
            intentChannel.trySend(ViewIntent.SearchKeyword(it))
        }

        ResultList(viewState.resultData,viewState.keyword){
            GlobalData.noteDetailsNoteEntity = it
            postEventValue(EventBus.NavController,  NavigateRouter.DetailPage.Detail)
        }


    }
}

