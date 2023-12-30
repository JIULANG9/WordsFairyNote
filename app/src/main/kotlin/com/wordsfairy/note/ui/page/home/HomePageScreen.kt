package com.wordsfairy.note.ui.page.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.wordsfairy.base.mvi.core.unit

import com.wordsfairy.note.MainActivity
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.ext.coreui.rememberFlowWithLifecycle
import com.wordsfairy.note.ext.flow.noteStartWith
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.page.create.CreateNoteState
import com.wordsfairy.note.ui.page.create.CreateNotePage
import com.wordsfairy.note.ui.page.detail.NoteDetailState
import com.wordsfairy.note.ui.page.detail.NoteDetailsPage
import com.wordsfairy.note.ui.page.detail.set.ContentSetUI
import com.wordsfairy.note.ui.page.backups.BackupsProgressBarUI
import com.wordsfairy.note.ui.page.backups.NoteDataUI
import com.wordsfairy.note.ui.page.backups.NoteDataViewModel
import com.wordsfairy.note.ui.page.backups.ProgressBarUI
import com.wordsfairy.note.ui.page.detail.noteDetailScreen
import com.wordsfairy.note.ui.page.detail.toNoteDetailsUI
import com.wordsfairy.note.ui.page.home.foldermanage.FolderManageUI
import com.wordsfairy.note.ui.page.search.SearchPage
import com.wordsfairy.note.ui.page.search.SearchUIState
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.page.set.SetPageUI
import com.wordsfairy.note.ui.theme.AppResId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import com.wordsfairy.note.ui.widgets.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/23 22:39
 */
@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalAnimationApi
@Composable
fun HomePageScreen(navController: NavHostController) {
    val noteDataViewModel: NoteDataViewModel = hiltViewModel()

    SlideAnimatedNavHost(
        navController,
        startDestination = NavigateRouter.HomePage.HOME,
    ) {
        composable(
            NavigateRouter.HomePage.HOME
        ) {
            HomePageUI()
        }
        composable(
            NavigateRouter.HomePage.FolderManage
        ) {
            FolderManageUI {
                navController.navigateUp()
            }
        }
        composable(
            NavigateRouter.SetPage.Set
        ) {
            SetPageUI {
                navController.navigateUp()
            }
        }
        noteDetailScreen(navController)

        composable(
            NavigateRouter.DetailPage.Set
        ) {
            ContentSetUI(onBack = {
                navController.navigateUp()
            })
        }
        composable(
            NavigateRouter.SetPage.NoteData
        ) {
            NoteDataUI(onBack = {
                navController.navigateUp()
            }, noteDataViewModel)
        }
        composable(
            NavigateRouter.SetPage.BackupsProgressBar
        ) {
            BackupsProgressBarUI(onBack = {
                navController.navigateUp()
            }, noteDataViewModel)
        }
        composable(
            NavigateRouter.DetailPage.ProgressBarUI
        ) {
            ProgressBarUI(onBack = {
                navController.navigateUp()
            })
        }
    }
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun HomePageUI(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewStateFlow.collectAsState()
    //震动
    val feedback = LocalHapticFeedback.current
    val context = LocalContext.current

    val noteInfoList by viewModel.noteInfoList.collectAsState(emptyList())

    val singleEvent = rememberFlowWithLifecycle(viewModel.singleEvent)
    val intentChannel = remember { Channel<HomeViewIntent>(Channel.UNLIMITED) }
    LaunchedEffect(viewModel) {
        intentChannel
            .consumeAsFlow()
            .noteStartWith(HomeViewIntent.Initial)
            .noteStartWith(HomeViewIntent.GetNoteInfo)
            .onEach(viewModel::processIntent)
            .collect()
    }
    LaunchedEffect(singleEvent) {
        singleEvent.collectLatest { event ->
            when (event) {
                is HomeSingleEvent.Refresh.Success -> {

                }
            }.unit
        }
    }
    Scaffold(
        Modifier
            .onSizeChanged { viewModel.fullSize = it }
            .background(WordsFairyTheme.colors.background)
            .fillMaxSize(),
        content = { padding ->
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WordsFairyTheme.colors.background)
                        .systemBarsPadding()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        /** 首页搜索框 */
                        HomeSearchView(
                            Modifier
                                .padding(start = 16.dp)
                                .weight(1f), onSizedChanged = {
                                viewModel.searchCardSize = it
                            }) { offset ->
                            //点击事件
                            viewModel.searchUIState = SearchUIState.Opening
                            viewModel.searchUIOffset = offset
                        }
                        /** 设置按钮 */
                        MyIconButton(painter = painterResource(id = AppResId.Drawable.Set)) {
                            postEventValue(EventBus.NavController, NavigateRouter.SetPage.Set)
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                    HomeTab(noteInfoList, currentFolderCallback = {
                        GlobalData.noteDetailsNoteFolderEntity = it
                    }, itemOnClick = { entity, offset, cardSize ->
                        //关闭过度动画
                        if (AppSystemSetManage.closeAnimation) {
                            toNoteDetailsUI(entity,false)
                        } else {
                            viewModel.noteDetailUISize = cardSize
                            viewModel.noteDetailUIOffset = offset
                            GlobalData.noteDetailsNoteEntity = entity
                            viewModel.currentNoteDetailsState = NoteDetailState.Opening
                        }
                    })
                }
                /**
                 * 添加笔记
                 */
                HomeAddButton(
                    Modifier
                        .navigationBarsPadding()
                        .align(Alignment.BottomEnd),
                    onSizedChanged = {
                        viewModel.cardSize = it
                    }
                ) { offset ->
                    //点击事件
                    viewModel.currentCreateNoteState = CreateNoteState.Opening
                    viewModel.createNoteUIOffset = offset
                    //震动
                    feedback.vibration()
                }

                /**
                 * 帖子创建
                 */
                CreateNotePage(
                    viewModel.currentCreateNoteState,
                    viewModel.cardSize,
                    viewModel.fullSize,
                    viewModel.createNoteUIOffset,
                    {
                        viewModel.currentCreateNoteState = CreateNoteState.Closing
                    },
                    {
                        viewModel.currentCreateNoteState = CreateNoteState.Closed
                    })

                /**
                 * 帖子详细
                 */
                NoteDetailsPage(
                    viewModel.currentNoteDetailsState,
                    viewModel.noteDetailUISize,
                    viewModel.fullSize,
                    viewModel.noteDetailUIOffset,
                    {
                        viewModel.currentNoteDetailsState = NoteDetailState.Closing
                    },
                    {
                        viewModel.currentNoteDetailsState = NoteDetailState.Closed
                    })

                /**
                 * 搜索
                 */
                SearchPage(
                    viewModel.searchUIState,
                    viewModel.searchCardSize,
                    viewModel.fullSize,
                    viewModel.searchUIOffset,
                    {
                        viewModel.searchUIState = SearchUIState.Closing
                    },
                    {
                        viewModel.searchUIState = SearchUIState.Closed
                    }
                )
            }
        })

    /**
     * 初次加载 隐私政策弹窗
     */
    if (!viewState.consentAgreement) {
        AgreementUI(disagree = {
            MainActivity.CONTEXT.finish()
        },
            agree = {
                intentChannel.trySend(HomeViewIntent.ConsentAgreement)
            })
    }
}



