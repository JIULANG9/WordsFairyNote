package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.data.entity.NoteAndNoteContent
import com.wordsfairy.note.data.entity.NoteEntity
import com.wordsfairy.note.ui.theme.WordsFairyTheme

import kotlin.math.ceil

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/5 16:59
 */
@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 }
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}
@ExperimentalMaterial3Api
@Composable
fun HomeItemCard(
    index: Int, entity: NoteAndNoteContent,
    onClick: (entity: NoteEntity, offset: IntOffset, cardSize: IntSize)  -> Unit
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
        onClick = {
            onClick(entity.noteEntity,intOffset!!,cardSize!!)
        },
    ) {
        Column(Modifier.padding(9.dp)) {
            if (entity.noteEntity.title != ""){
                Title(title = entity.noteEntity.title)
                Spacer(Modifier.height(6.dp))
            }
            entity.noteContents.forEach { noteContent ->
                TextSecondary(text = noteContent.content)
            }
        }
    }
}

