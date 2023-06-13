package com.wordsfairy.note.ui.widgets

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/8/5 22:20
 */
@Composable
fun MyIconButton(
    painter: Painter,
    description: String = "",
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = WordsFairyTheme.colors.icon,
    onClick: () -> Unit = {},
) {
    IconButton(onClick = onClick,modifier) {
        Image(
            painter = painter,
            modifier = Modifier.size(size),
            contentDescription = description,
            colorFilter= ColorFilter.tint(color = tint)
        )
    }
}
@Composable
fun MyIconButton(
    imageVector: ImageVector,
    description: String = "",
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint : Color= WordsFairyTheme.colors.icon,
    onClick: () -> Unit = {},
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription =description,
            modifier.size(size),
            tint =tint
        )
    }
}



@Composable
fun HomeAddButton(
    modifier: Modifier,
    onSizedChanged: (IntSize) -> Unit,
    onClick: (offset: IntOffset) -> Unit,
) {
    var intOffset: IntOffset? by remember { mutableStateOf(null) }
    FloatingActionButton(onClick = {
        onClick(intOffset!!)
    },
        modifier
            .padding(16.dp)
            .onSizeChanged { onSizedChanged(it) }
            .onGloballyPositioned {
                val offset = it.localToRoot(Offset(0f, 0f))
                intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())
            },
        containerColor = WordsFairyTheme.colors.themeUi
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "add",
            Modifier.size(26.dp),
            tint = WordsFairyTheme.colors.textWhite
        )
    }
}

/**
 * 这样，当按下按钮时，IconButton会显示图标；松开按钮之后，图标会保持显示2秒，并逐渐消失。
 * @param imageVector ImageVector
 * @param description String
 * @param modifier Modifier
 * @param onClick Function0<Unit>
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconButtonWithHiddenIcon(
    imageVector: ImageVector,
    description: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var iconVisible by remember { mutableStateOf(false) }
    val iconAlpha by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    val scope = rememberCoroutineScope()

    IconButton(
        onClick =  onClick,
        modifier = modifier.pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> iconVisible = true
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    scope.launch {
                        delay(2000)
                        iconVisible= false
                    }
                }
            }
            true
        }
    ) {
        Box {
            Icon(
                imageVector = imageVector,
                contentDescription =description,
                Modifier.size(26.dp).alpha(iconAlpha),
                tint = WordsFairyTheme.colors.icon
            )

        }
    }
}