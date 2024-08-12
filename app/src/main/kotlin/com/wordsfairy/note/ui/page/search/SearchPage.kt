package com.wordsfairy.note.ui.page.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.WordsFairyTheme


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 13:59
 */
@Composable
fun SearchPage(
    pageState: SearchUIState,
    cardSize: IntSize,
    fullSize: IntSize,
    cardOffset: IntOffset,
    onPageClosing: () -> Unit,
    onPageClosed: () -> Unit
) {
    val deploymentDuration = 500

    var animReady by remember { mutableStateOf(false) }
    var animFinish by remember { mutableStateOf(false) }

    val background by animateColorAsState(
        if (pageState > SearchUIState.Closed) WordsFairyTheme.colors.background else WordsFairyTheme.colors.whiteBackground,
        label = "backgroundAnimate"
    )
    val cornerSize by animateDpAsState(if (animFinish) 0.dp else 26.dp, label = "cornerSizeAnimate")
    val size by animateIntSizeAsState(
        if (pageState > SearchUIState.Closed) fullSize else cardSize,
        animationSpec = tween(deploymentDuration), label = "sizeAnimation"
    )
    val fullOffset = remember { IntOffset(0, 0) }
    val offsetInimitable = remember { Animatable(IntOffset(0, 0), IntOffset.VectorConverter) }
    LaunchedEffect(pageState) {
        when (pageState) {
            SearchUIState.Opening -> {
                animReady = true
                offsetInimitable.snapTo(cardOffset)
                offsetInimitable.animateTo(fullOffset, animationSpec = tween(deploymentDuration))
                animFinish = true
            }

            SearchUIState.Closing -> {
                animFinish = false
                offsetInimitable.snapTo(fullOffset)
                offsetInimitable.animateTo(cardOffset, animationSpec = tween(deploymentDuration))
                onPageClosed()
                animReady = false
            }

            else -> {}
        }
    }
    if (pageState != SearchUIState.Closed && animReady) {
        Box(
            Modifier
                .offset { offsetInimitable.value }
                .clip(RoundedCornerShape(cornerSize))
                .width(with(LocalDensity.current) { size.width.toDp() })
                .height(with(LocalDensity.current) { size.height.toDp() })
                .background(background)
        ) {
            SearchUI(onBack = onPageClosing)
        }
    }
}

enum class SearchUIState {
    Closing, Closed, Opening
}