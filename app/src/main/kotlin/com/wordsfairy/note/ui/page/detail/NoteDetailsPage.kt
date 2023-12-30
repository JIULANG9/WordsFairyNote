package com.wordsfairy.note.ui.page.detail


import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * @Description: 笔记详细
 * @Author: JIULANG
 * @Data: 2023/5/9 15:23
 */
@ExperimentalCoroutinesApi
@Composable
fun NoteDetailsPage(
    pageState: NoteDetailState,
    cardSize: IntSize,
    fullSize: IntSize,
    cardOffset: IntOffset,
    onPageClosing: () -> Unit,
    onPageClosed: () -> Unit
) {

    //震动
    val feedback = LocalHapticFeedback.current
    var animReady by remember { mutableStateOf(false) }
    var animFinish by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    val cornerSize by animateDpAsState(if (animFinish) 0.dp else 16.dp, label = "animateDpAsState")

    val DEPLOYMENT_DURATION = 560

    val size by animateIntSizeAsState(
        if (pageState > NoteDetailState.Closed) fullSize else cardSize,
        animationSpec = tween(DEPLOYMENT_DURATION), label = "animateIntSizeAsState"
    )

    val fullOffset = remember { IntOffset(0, 0) }

    val offsetInimitable = remember { Animatable(IntOffset(0, 0), IntOffset.VectorConverter) }
    LaunchedEffect(pageState) {
        when (pageState) {
            NoteDetailState.Opening -> {
                animReady = true
                offsetInimitable.snapTo(cardOffset)
                offsetInimitable.animateTo(fullOffset, animationSpec = tween(DEPLOYMENT_DURATION))
                animFinish = true
            }
            NoteDetailState.Closing -> {
                animFinish = false
                offsetInimitable.snapTo(fullOffset)
                offsetInimitable.animateTo(cardOffset, animationSpec = tween(DEPLOYMENT_DURATION))
                animReady = false
                onPageClosed()
            }
            else -> {}
        }
    }

    if (pageState != NoteDetailState.Closed && animReady) {

        Box(
            Modifier
                .offset {
                    offsetInimitable.value
                }
                .clip(RoundedCornerShape(cornerSize))
                .width(with(LocalDensity.current) { size.width.toDp() })
                .height(with(LocalDensity.current) { size.height.toDp() })

        ) {
            NoteDetailsUI(onBack = {
                visible = false
                onPageClosing.invoke()
            })
            if (pageState == NoteDetailState.Closing){
                Box(Modifier.fillMaxSize().background(WordsFairyTheme.colors.itemBackground))
            }
        }
    }
}

enum class NoteDetailState {
    Closing, Closed, Opening
}