package com.wordsfairy.note.ui.page.detail.wifgets

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.wordsfairy.base.utils.jumpToWeChat
import com.wordsfairy.base.utils.searchInBrowser
import com.wordsfairy.base.utils.systemShare
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.entity.NoteContentEntity

import com.wordsfairy.note.ui.common.click
import com.wordsfairy.note.ui.common.vibration

import com.wordsfairy.note.ui.page.create.CreateNoteContentEditView
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.*

import com.wordsfairy.note.ui.widgets.reorderable.*
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * @Description: 内容列表
 * @Author: JIULANG
 * @Data: 2023/5/10 2:13
 */

/**
 * 搜索按钮
 */
@Composable
fun SearchButton(
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    MyIconButton(
        painter = painterResource(id = AppResId.Drawable.Search),
        stringResource(id = AppResId.String.Search),
        size = 24.dp,
        tint = if (isHighlight) WordsFairyTheme.colors.iconBlack else WordsFairyTheme.colors.icon
    ) {
        onClick.invoke()
    }
}

/**
 * 阅读模式按钮
 */
@Composable
fun ReadButton(
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    MyIconButton(
        painter = painterResource(id = AppResId.Drawable.ReadMode),
        stringResource(id = AppResId.String.Search),
        size = 22.dp,
        tint = if (isHighlight) WordsFairyTheme.colors.iconBlack else WordsFairyTheme.colors.icon
    ) {
        onClick.invoke()
    }
}

@Composable
fun SaveTitleAndSet(
    expanded: Boolean = false,
    modifier: Modifier = Modifier,
    saveClick: () -> Unit,
    setClick: () -> Unit,
) {
    AnimatedVisibility(
        expanded,
        modifier
    ) {
        MyIconButton(imageVector = Icons.Rounded.Check) {
            saveClick.invoke()
        }
    }
    AnimatedVisibility(
        !expanded,
        modifier
    ) {
        MyIconButton(painter = painterResource(id = AppResId.Drawable.Set)) {
            setClick.invoke()
        }
    }
}

@Composable
fun VisibilityViewColumn(
    visible: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.animateContentSize()) {
        if (visible) {
            Column(content = content)
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContentEditView(
    noteContent: String,
    isSearch: Boolean,
    canSaved: Boolean,
    onContentChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    saveNote: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val feedback = LocalHapticFeedback.current

    ImmerseCard(
        modifier = Modifier.padding(12.dp),
        elevation = 2.dp
    ) {
        Column(
            Modifier.padding(horizontal = 6.dp)
        ) {
            var appendTextValue by remember { mutableStateOf("") }
            /** 笔记输入框 */
            Spacer(Modifier.height(6.dp))
            CreateNoteContentEditView(
                text = noteContent,
                addendText = appendTextValue,
                placeholder = if (isSearch) "搜索笔记" else "开始书学",
                isAutoFocused = false
            ) {
                if (isSearch) {
                    onSearch.invoke(it)
                } else {
                    onContentChange.invoke(it)
                }
            }
            Row(Modifier.fillMaxWidth()) {
                if (isSearch) {
                    NoteTag(string = "搜索模式", Modifier.align(Alignment.Bottom))
                }
                Spacer(Modifier.weight(1f))
                SmallButton("剪贴板", color = AppColor.blue) {
                    val clipboardText = clipboardManager.getText()?.text ?: ""
                    appendTextValue = clipboardText
                }
                Spacer(Modifier.width(6.dp))
                if (!isSearch) {
                    SmallButton("保存", enabled = canSaved) {
                        saveNote.invoke()
                        feedback.vibration()
                    }
                }
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}

/**
 * 内容列表
 */
@Composable
fun ContentList(
    noteContents: List<NoteContentEntity>,
    onMove: (List<NoteContentEntity>) -> Unit,
    onDelete: (NoteContentEntity) -> Unit,
    onModify: (NoteContentEntity) -> Unit,
) {

    var showProgress by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(460)
        showProgress = true
    }
    var data by remember { mutableStateOf<List<NoteContentItem>>(mutableListOf()) }

    val isVisible = noteContents.isNotEmpty() && showProgress
    if (isVisible) {
        var noteContentsCache by remember { mutableStateOf(noteContents) }
        val feedback = LocalHapticFeedback.current
        val mutex = Mutex()

        val state = rememberReorderableLazyListState(onDragStart = {
            feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }, onMove = { from, to ->
            data = data.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            CoroutineScope(Dispatchers.IO).launch {
                mutex.withLock {
                    noteContentsCache = noteContentsCache.apply {
                        val fromIndex = from.index
                        val toIndex = to.index
                        val fromPos = this[fromIndex].position
                        val toPos = this[toIndex].position
                        this[fromIndex].position = toPos
                        this[toIndex].position = fromPos
                        Collections.swap(this, fromIndex, toIndex)
                    }
                }
            }
        }, onDragEnd = { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                delay(100)
                onMove.invoke(noteContentsCache)
            }
        })
        LaunchedEffect(noteContents.hashCode()) {
            val isChange = noteContents.size != noteContentsCache.size

            //过滤其他数据 减少内存开销
            data = noteContents.map {
                NoteContentItem(it.noteContextId, it.content)
            }
            if (isChange) {
                //原数据 大于 缓存 即新增
                val isAdd = noteContents.size > noteContentsCache.size
                if (isAdd) {
                    //解决没有新增动画 滚动到第一位
                    state.scrollToItem(0, 0)
                }
                noteContentsCache = noteContents
            }
        }
        val deletedRouteList = remember { mutableStateListOf<Long>() }
        LazyColumn(
            state = state.listState,
            modifier = Modifier.reorderable(state)
        ) {
            itemsIndexed(data, { _, item -> item }) { index, item ->

                //显示动画
                val animatedProgress = remember {
                    androidx.compose.animation.core.Animatable(
                        initialValue = 0.8f
                    )
                }
                LaunchedEffect(Unit) {
                    animatedProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(360, easing = LinearEasing)
                    )
                }
                ReorderableItem(state, key = item) { isDragging ->
                    //删除动画
                    AnimatedVisibility(
                        visible = !deletedRouteList.contains(item.id),
                        enter = expandVertically(),
                        exit = shrinkVertically(
                            animationSpec = tween(
                                durationMillis = 1000,
                            )
                        )
                    ) {
                        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp,
                            label = "elevation"
                        )
                        ImmerseCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .shadow(elevation.value)
                                .graphicsLayer(
                                    scaleY = animatedProgress.value,
                                    scaleX = animatedProgress.value
                                ),
                            shape = RoundedCornerShape(6.dp),
                            backgroundColor = WordsFairyTheme.colors.itemImmerse,
                        ) {
                            ContentLayout(item, state, onDelete = { id ->
                                deletedRouteList.add(id)
                                onDelete.invoke(noteContentsCache[index])
                            }, onModify = {
                                onModify.invoke(noteContentsCache[index])
                            })
                        }
                    }
                }
            }
        }
    }
}


/**
 * 内容列表
 */
@Composable
fun SearchResultList(
    noteContents: List<NoteContentEntity>,
    onDelete: (NoteContentEntity) -> Unit,
    onModify: (NoteContentEntity) -> Unit,
) {
    var data by remember { mutableStateOf<List<NoteContentItem>>(mutableListOf()) }

    val isVisible = noteContents.isNotEmpty()
    if (isVisible) {

        val feedback = LocalHapticFeedback.current
        val state = rememberLazyListState()

        LaunchedEffect(noteContents.hashCode()) {
            //过滤其他数据 减少内存开销
            data = noteContents.map {
                NoteContentItem(it.noteContextId, it.content)
            }

        }
        val deletedRouteList = remember { mutableStateListOf<Long>() }
        LazyColumn(
            state = state,
            modifier = Modifier
        ) {
            itemsIndexed(data, { _, item -> item }) { index, item ->

                //删除动画
                AnimatedVisibility(
                    visible = !deletedRouteList.contains(item.id),
                    enter = expandVertically(),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 1000,
                        )
                    )
                ) {
                    ImmerseCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        backgroundColor = WordsFairyTheme.colors.itemImmerse,
                    ) {
                        ContentLayout(item, null, onDelete = { id ->
                            deletedRouteList.add(id)
                            onDelete.invoke(noteContents[index])
                        }, onModify = {
                            onModify.invoke(noteContents[index])
                        })
                    }
                }
            }
        }
    }
}


@Composable
private fun ContentLayout(
    item: NoteContentItem, state: ReorderableLazyListState?,
    onDelete: (Long) -> Unit,
    onModify: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Box(
        Modifier
            .click { expanded = true }
            .padding(start = 9.dp, top = 1.dp, bottom = 1.dp, end = 3.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        TextNoteContent(
            text = item.content,
            Modifier.padding(vertical = 9.dp),
            color = WordsFairyTheme.colors.textPrimary
        )
        if (state != null) {
            IconButtonWithHiddenIcon(
                imageVector = Icons.Rounded.Menu,
                modifier = Modifier
                    .detectReorderAfterLongPress(state)
                    .align(Alignment.TopEnd)
            )
        }

        NoteContentDropdownMenu(expanded, onDismiss = {
            expanded = false
        }) {
            val content = item.content
            when (it) {
                1 -> {
                    //搜索
                    context.searchInBrowser(AppSystemSetManage.searchEngines, content)
                }

                2 -> {
                    //复制
                    clipboardManager.setText(AnnotatedString(content))

                    ToastModel("已复制", ToastModel.Type.Normal).showToast()
                    if (AppSystemSetManage.jumpToWeChat) {
                        context.jumpToWeChat()
                    }
                }

                3 -> {
                    //文本分享
                    context.systemShare(content)
                }

                4 -> {
                    onDelete.invoke(item.id)
                }

                5 -> {
                    onModify.invoke()
                }
            }
        }
    }
}


@Parcelize
private data class NoteContentItem(
    var id: Long,
    var content: String
) : Parcelable