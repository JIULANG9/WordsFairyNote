package com.wordsfairy.note.ui.widgets



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.WordsFairyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImmerseCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit ={},
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = WordsFairyTheme.colors.itemBackground,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor,contentColor = contentColor),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = border,
        content = content)
}

@Composable
fun ImmerseCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = WordsFairyTheme.colors.itemBackground,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor,contentColor = contentColor),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = border,
        content = content)
}
/**
 * 增加未读小红点
 */
fun Modifier.unread(read: Boolean, badgeColor: Color) = this
    .drawWithContent {
        drawContent()
        if (!read) {
            drawCircle(
                color = badgeColor,
                radius = 5.dp.toPx(),
                center = Offset(size.width - 1.dp.toPx(), 1.dp.toPx()),
            )
        }
    }
