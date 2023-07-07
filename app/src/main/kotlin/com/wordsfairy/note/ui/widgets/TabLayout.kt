package com.wordsfairy.note.ui.widgets

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.NoteFolderEntity

import com.wordsfairy.note.data.entity.NoteInfo
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/28 10:41
 */


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    noteFolders: List<NoteInfo>,
    currentFolderCallback: (NoteFolderEntity) -> Unit = {},
    itemOnClick: (entity: NoteEntity, offset: IntOffset, cardSize: IntSize) -> Unit
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = AppSystemSetManage.homeTabRememberPage, pageCount = {
            noteFolders.size
        })
    val scope = rememberCoroutineScope()
    val feedback = LocalHapticFeedback.current

    var isVibration by remember { mutableStateOf(0) }


    val currentIndex = pagerState.currentPage
    LaunchedEffect(currentIndex) {
        // 监听pagerState的变化
        launch(Dispatchers.IO) {
            AppSystemSetManage.homeTabRememberPage = currentIndex
        }
    }

    Row {
        ScrollableTabRow(
            modifier = Modifier.weight(1f),
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty()) {
                    PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
                }
            },
            containerColor = WordsFairyTheme.colors.background,
            divider = {

            }
        ) {
            // Add tabs for all of our pages
            noteFolders.forEachIndexed { index, title ->
                val selected = (pagerState.currentPage == index)
                Tab(
                    selected = selected,
                    selectedContentColor = WordsFairyTheme.colors.textPrimary,
                    unselectedContentColor = WordsFairyTheme.colors.textSecondary,
                    onClick = {
                        scope.launch {
                            feedback.vibration()
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Text(
                        text = title.noteFolder.name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(9.dp)
                    )
                }
            }
        }
        IconButton(onClick = {
            feedback.vibration()
            context.postEventValue(
                EventBus.NavController,
                NavigateRouter.HomePage.FolderManage
            )
        }) {
            Image(
                painter = painterResource(AppResId.Drawable.Folder),
                modifier = Modifier.size(26.dp),
                contentDescription = "Folder"
            )
        }
        Spacer(Modifier.width(6.dp))
    }

    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 20,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val scrollState = rememberScrollState()
        LaunchedEffect(scrollState.value) {
            if (isVibration < noteFolders.size) {
                isVibration++
                return@LaunchedEffect
            }
            if (scrollState.value == 0 || scrollState.value == scrollState.maxValue) {
                feedback.vibration()
            }

        }
        Box(modifier = Modifier.fillMaxSize()) {
            StaggeredVerticalGrid(
                maxColumnWidth = 220.dp,
                modifier = Modifier
                    .padding(4.dp)
                    .verticalScroll(scrollState)
            ) {
                noteFolders[page].noteAndNoteContents.forEachIndexed { index, route ->
                    HomeItemCard(index, route) { entity, offset, cardSize ->
                        itemOnClick.invoke(entity, offset, cardSize)
                        currentFolderCallback.invoke(noteFolders[page].noteFolder)
                    }
                }
            }
        }
    }
}

/**
 * PagerTap 指示器
 * @param  percent  指示器占用整个tab宽度的比例
 * @param  height   指示器的高度
 * @param  color    指示器的颜色
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerTabIndicatorOld(
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    color: Color = WordsFairyTheme.colors.themeUi,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 0.6f,
    height: Dp = 5.dp,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentPage = minOf(tabPositions.lastIndex, pagerState.currentPage)
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction

        val indicatorWidth = currentTab.width.toPx() * percent

        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).toPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).toPx()
        } else {
            currentTab.left.toPx()
        }

        val canvasHeight = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(
                indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                canvasHeight - height.toPx()
            ),
            size = Size(indicatorWidth + indicatorWidth * abs(fraction), height.toPx()),
            cornerRadius = CornerRadius(50f)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    color: Color = WordsFairyTheme.colors.themeUi,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 0.6f,
    height: Dp = 5.dp,
) {

    val currentPage by rememberUpdatedState(newValue = pagerState.currentPage)
    val fraction by rememberUpdatedState(newValue = pagerState.currentPageOffsetFraction)
    val currentTab = tabPositions[currentPage]

    val previousTab = tabPositions.getOrNull(currentPage - 1)
    val nextTab = tabPositions.getOrNull(currentPage + 1)
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {

            val indicatorWidth = currentTab.width.toPx() * percent
            val indicatorOffset = if (fraction > 0 && nextTab != null) {
                lerp(currentTab.left, nextTab.left, fraction).toPx()
            } else if (fraction < 0 && previousTab != null) {
                lerp(currentTab.left, previousTab.left, -fraction).toPx()
            } else {
                currentTab.left.toPx()
            }

            val canvasHeight = size.height
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                    canvasHeight - height.toPx()
                ),
                size = Size(indicatorWidth + indicatorWidth * abs(fraction), height.toPx()),
                cornerRadius = CornerRadius(50f)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

}