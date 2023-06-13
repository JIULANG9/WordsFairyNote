package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.LocalAppColors
import com.wordsfairy.note.ui.theme.WordsFairyTheme


@Composable
fun AlertInfo(
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    suppressed: Boolean = false,
    icon: ImageVector? = Icons.Rounded.Info,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {

    Alert(
        icon = icon,
        title = title,
        borderColor = WordsFairyTheme.colors.info,
        actions = actions,
        content = content,
        suppressed = suppressed,
        modifier = modifier,
    )
}

@Composable
fun AlertSuccess(
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    suppressed: Boolean = false,
    icon: ImageVector? = Icons.Rounded.CheckCircle,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Alert(
        icon = icon,
        title = title,
        actions = actions,
        content = content,
        suppressed = suppressed,
        borderColor = WordsFairyTheme.colors.themeUi,
        modifier = modifier,
    )
}

@Composable
fun AlertWarning(
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    suppressed: Boolean = false,
    icon: ImageVector = Icons.Rounded.Warning,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Alert(
        icon = icon,
        borderColor = WordsFairyTheme.colors.error,
        title = title,
        actions = actions,
        content = content,
        suppressed = suppressed,
        modifier = modifier,
    )
}

@Composable
fun AlertCritical(
    title: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    suppressed: Boolean = false,
    icon: ImageVector? = Icons.Rounded.Star,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Alert(
        icon = icon,
        title = title,
        actions = actions,
        content = content,
        suppressed = suppressed,
        modifier = modifier,
    )
}

@Composable
private fun Alert(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    title: @Composable ColumnScope.() -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    suppressed: Boolean,
    borderColor: Color = WordsFairyTheme.colors.success,
) {
    AlertContainer(
        suppressed = suppressed,
        contentPadding = PaddingValues(12.dp),
        borderColor = borderColor,
        modifier = modifier,
    ) {
        if (icon != null) {
            MyIconButton(
                imageVector = icon,
                tint = borderColor
            )
        }
        AlertContent(
            title = title,
            actions = actions,
            content = content,
            suppressed = suppressed,
        )
    }
}

@Composable
private fun AlertContent(
    title: @Composable ColumnScope.() -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    suppressed: Boolean,
) {
    Column {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            title()
            content()
        }
        AlertButtons(topPadding = 12.dp, suppressed, actions)
    }
}

@Composable
private fun AlertButtons(
    topPadding: Dp,
    suppressed: Boolean,
    content: @Composable () -> Unit,
) {
    AlertButtonsLayout(topPadding, content)
}

@Composable
private fun AlertButtonsLayout(
    topPadding: Dp,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
    ) { measurables, incomingConstraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }

        val topPaddingPx = topPadding.roundToPx()
        val buttonSpacing = 8.dp.roundToPx()

        val buttonsCount = measurables.size
        val buttonSize =
            ((incomingConstraints.maxWidth - (buttonSpacing * (buttonsCount - 1))) / buttonsCount)
        val buttonConstraint =
            incomingConstraints.copy(minWidth = buttonSize, maxWidth = buttonSize)

        val placeables = measurables.map {
            it.measure(buttonConstraint)
        }

        val maxHeight = placeables.maxOf { it.height } + topPaddingPx
        layout(incomingConstraints.maxWidth, maxHeight) {
            var x = 0
            for (placeable in placeables) {
                placeable.place(x, y = topPaddingPx)
                x += buttonSize + buttonSpacing
            }
        }
    }
}

@Composable
internal fun AlertContainer(
    suppressed: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    borderColor: Color,
    content: @Composable RowScope.() -> Unit,
) {
    val bgColor = when (suppressed) {
        true -> WordsFairyTheme.colors.icon
        false -> borderColor.copy(alpha = 0.1f)
    }
    val mBorderColor = when (suppressed) {
        true -> borderColor
        false -> WordsFairyTheme.colors.icon
    }.copy(0.1f)

//    val accentColor = WordsFairyTheme.colors.themeUi
    val shape = RoundedCornerShape(9.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBehind {
                drawRect(bgColor)
                drawLine(
                    color = borderColor,
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = 6.dp.toPx(),
                    // doubled width to account for path offset
                )
            }
            .border(1.dp, mBorderColor, shape)
            .padding(top = 3.dp) // stroke width
            .padding(contentPadding),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
fun AlertCriticalPreview() {

    AlertInfo(
        title = { Title("Title") },
        content = {
            TextContent("Content description")
        },
        actions = {
            MyButton("AlertInfo", onClick = {})

        },
    )
    AlertInfo(
        title = { Title("Title") },
        suppressed = false,
        icon = null,
        content = {
            TextContent("Content description")
        },
        actions = {
            MyButton("AlertInfo", onClick = {})
        },
    )

}
