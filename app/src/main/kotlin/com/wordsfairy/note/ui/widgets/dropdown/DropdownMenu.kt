package com.wordsfairy.note.ui.widgets.dropdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.ui.common.click
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.ImmerseCard
import com.wordsfairy.note.ui.widgets.Title


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/29 15:08
 */

@Composable
fun FolderDropdownMenu(
    expanded: Boolean,
    noteFolders: List<NoteFolderEntity>,
    selectedFolder: NoteFolderEntity?,
    onDismiss: () -> Unit,
    onClick: (NoteFolderEntity) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        modifier = Modifier.background(WordsFairyTheme.colors.dialogBackground),
        onDismissRequest = onDismiss,
    ) {

        noteFolders.forEachIndexed { index, item ->
            val name = item.name
            val isSelect = selectedFolder?.folderId == item.folderId
            val background =
                if (isSelect) WordsFairyTheme.colors.themeUi.copy(alpha = 0.3f) else WordsFairyTheme.colors.dialogBackground
            val textColor = if (isSelect) AppColor.themeColor else WordsFairyTheme.colors.textPrimary

            Row(
                Modifier
                    .click {
                        onClick.invoke(item)
                        onDismiss.invoke()
                    }
                    .width(199.dp)
                    .background(background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Title(name, color = textColor, fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                if (isSelect) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = name,
                        Modifier.size(26.dp),
                        tint = AppColor.themeColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviews() {

}

@Composable
fun AnimatedVisibilitySlide(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = {
                    -it
                },
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 100
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = {
                    -it
                },
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 100
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = { onDismiss.invoke() },
                        // 去除点击效果
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .imePadding()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    // 检测手指滑动事件
                                    if (dragAmount.y < 0) {
                                        // 手指向上滑动
                                        // 在这里实现你的逻辑
                                        onDismiss.invoke()
                                    }
                                })
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(WordsFairyTheme.colors.dialogBackground),
                    elevation = CardDefaults.cardElevation(3.dp), content = content
                )
            }
        }
    }
}


@Composable
fun AnimatedSlideFormBottom(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = {
                    it
                },
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 100
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = {
                    it
                },
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 100
                )
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = { onDismiss.invoke() },
                        // 去除点击效果
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        })
            ) {
                Card(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth()
                        .padding(9.dp)
                        .align(Alignment.BottomCenter)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    // 检测手指滑动事件
                                    if (dragAmount.y > 0) {
                                        // 手指向上滑动
                                        // 在这里实现你的逻辑
                                        onDismiss.invoke()
                                    }
                                })
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(WordsFairyTheme.colors.dialogBackground),
                    elevation = CardDefaults.cardElevation(3.dp), content = content
                )
            }
        }
    }
}
