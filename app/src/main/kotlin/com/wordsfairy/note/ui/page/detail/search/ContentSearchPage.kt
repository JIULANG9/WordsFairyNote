package com.wordsfairy.note.ui.page.detail.search

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/** 笔记内容界面 (弃用了)
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:00
 */
@Composable
fun ContentSearchPage(
    pageState:ContentSearchUIState,
    cardSize: IntSize,
    fullSize: IntSize,
    cardOffset: IntOffset,
    onPageClosing: () -> Unit,
    onPageClosed: () -> Unit
) {

    var animReady by remember { mutableStateOf(false) }

    var animFinish by remember { mutableStateOf(false) }

    val cornerSize by animateDpAsState(if (animFinish) 0.dp else 16.dp)
    val size by animateIntSizeAsState(if (pageState > ContentSearchUIState.Closed) fullSize else cardSize)
    val fullOffset = remember { IntOffset(0, 0) }
    val offsetAnimatable = remember { Animatable(IntOffset(0, 0), IntOffset.VectorConverter) }
    LaunchedEffect(pageState) {
        when (pageState) {
            ContentSearchUIState.Opening -> {
                animReady = true
                offsetAnimatable.snapTo(cardOffset)
                offsetAnimatable.animateTo(fullOffset)
                animFinish = true

            }
            ContentSearchUIState.Closing -> {
                animFinish = false
                offsetAnimatable.snapTo(fullOffset)
                offsetAnimatable.animateTo(cardOffset)
                animReady = false
                onPageClosed()
            }
            else -> {}
        }
    }
    if (pageState != ContentSearchUIState.Closed && animReady) {
        Box(
            Modifier
                .offset { offsetAnimatable.value }
                .clip(RoundedCornerShape(cornerSize))
                .width(with(LocalDensity.current) { size.width.toDp() })
                .height(with(LocalDensity.current) { size.height.toDp() })
                .background(WordsFairyTheme.colors.background)
        ){
            ContentSearchUI(onBack ={

            })
        }
    }
}

enum class ContentSearchUIState {
    Closing, Closed, Opening
}