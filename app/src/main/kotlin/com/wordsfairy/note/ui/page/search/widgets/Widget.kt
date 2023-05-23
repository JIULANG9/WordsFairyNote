package com.wordsfairy.note.ui.page.search.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.base.mvi.core.unit
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.data.entity.SearchNoteEntity
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.widgets.*
import kotlinx.coroutines.delay


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:15
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEdit(
    searchContent:String,
    onSearch: (String) -> Unit,
) {
    var isLoadingCompleted by remember { mutableStateOf(false) }
    var fullSize by remember { mutableStateOf(IntSize(0, 0)) }

    val size by animateIntSizeAsState(
        if (isLoadingCompleted) fullSize else IntSize(390, 0),
        animationSpec = tween(36)
    )
    LaunchedEffect(Unit) {
        delay(100)
        isLoadingCompleted = true
    }


    var searchContent by remember { mutableStateOf(searchContent) }

    Box(
        modifier = Modifier
            .onSizeChanged {
                fullSize = it
            }
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        SearchEditView(
            searchContent,
            stringResource(id = AppResId.String.Search),
            Modifier.width(with(LocalDensity.current) { size.width.toDp() }),
            onValueChanged = {
                searchContent = it
                onSearch.invoke(searchContent)
            },
            onDeleteClick = {
                searchContent = ""
                onSearch.invoke("")
            },
            onSearch = {}
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun ResultList(
    searchNotes: List<SearchNoteEntity>, keyword: String,
    onClick: (NoteEntity) -> Unit
) {

    var isVisibility by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        delay(360)
        isVisibility = true
    }
    VisibilityView(visible = isVisibility) {
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier
                .padding(4.dp)
                .verticalScroll(scrollState)
        ) {
            searchNotes.forEachIndexed { index, route ->
                SearchItemCard(index, route, keyword, onClick = {
                    //点击事件
                    onClick.invoke(route.noteEntity)
                })
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun SearchItemCard(
    index: Int, entity: SearchNoteEntity, keyword: String,
    onClick: () -> Unit
) {

    var intOffset: IntOffset? by remember { mutableStateOf(null) }
    var cardSize: IntSize? by remember { mutableStateOf(null) }
    ImmerseCard(
        Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .onSizeChanged { cardSize = it }
            .onGloballyPositioned {
                val offset = it.localToRoot(Offset(0f, 0f))
                intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())
            },
        onClick = onClick,
    ) {
        Column(Modifier.padding(9.dp)) {

            Row {
                if (entity.noteEntity.title.isNotEmpty()) {
                    Title(title = entity.noteEntity.title)
                }
                if (entity.folderName?.isNotEmpty() == true) {
                    Spacer(Modifier.weight(1f))
                    NoteTag(entity.folderName, Modifier.align(Alignment.Bottom))
                }
            }
            // 能力有限只能在这里 不是很优雅 [过滤删除 isDelete] tag
            val list = entity.noteContents.filter { !it.isDelete }.takeLast(7).reversed()
            list.forEach { noteContent ->
                /** 关键词高亮显示 */
                HighlightedText(noteContent.content, keyword)
            }
        }
    }
}