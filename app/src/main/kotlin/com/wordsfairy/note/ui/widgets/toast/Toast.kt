package com.wordsfairy.note.ui.widgets.toast


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.Title
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
public interface ToastData {
    public val message: String
    public val icon: ImageVector?
    public val animationDuration: StateFlow<Int?>
    public val type: ToastModel.Type?
    public suspend fun run(accessibilityManager: AccessibilityManager?)
    public fun pause()
    public fun resume()
    public fun dismiss()
    public fun dismissed()
}


private data class ColorData(
    val backgroundColor: Color,
    val textColor: Color,
    val iconColor: Color,
    val icon: ImageVector? = null,
)

@Composable
public fun Toast(
    toastData: ToastData,
) {

    val animateDuration by toastData.animationDuration.collectAsState()

    val colorData = when (toastData.type) {
        ToastModel.Type.Normal -> ColorData(
            backgroundColor = WordsFairyTheme.colors.background,
            textColor = WordsFairyTheme.colors.textPrimary,
            iconColor = WordsFairyTheme.colors.textPrimary,
            icon = Icons.Rounded.Notifications,
        )

        ToastModel.Type.Success -> ColorData(
            backgroundColor = WordsFairyTheme.colors.success,
            textColor = WordsFairyTheme.colors.textWhite,
            iconColor = WordsFairyTheme.colors.textWhite,
            icon = Icons.Rounded.CheckCircle,
        )

        ToastModel.Type.Info -> ColorData(
            backgroundColor = WordsFairyTheme.colors.info,
            textColor = WordsFairyTheme.colors.textWhite,
            iconColor = WordsFairyTheme.colors.textWhite,
            icon = Icons.Rounded.Info
        )

        ToastModel.Type.Warning -> ColorData(
            backgroundColor = AppColor.warning,
            textColor = WordsFairyTheme.colors.textWhite,
            iconColor = WordsFairyTheme.colors.textWhite,
            icon = Icons.Rounded.Warning
        )

        ToastModel.Type.Error -> ColorData(
            backgroundColor = WordsFairyTheme.colors.error,
            textColor = WordsFairyTheme.colors.textWhite,
            iconColor = WordsFairyTheme.colors.textWhite,
            icon = Icons.Rounded.Warning
        )

        else -> ColorData(
            backgroundColor = WordsFairyTheme.colors.dialogBackground,
            textColor = WordsFairyTheme.colors.textPrimary,
            iconColor = WordsFairyTheme.colors.textPrimary,
            icon = Icons.Rounded.Notifications,
        )
    }
    val icon = toastData.icon ?: colorData.icon
    key(toastData) {
        Toast(
            message = toastData.message,
            icon = icon,
            backgroundColor = colorData.backgroundColor,
            iconColor = colorData.iconColor,
            textColor = colorData.textColor,
            animateDuration = animateDuration,
            onPause = toastData::pause,
            onResume = toastData::resume,
            onDismissed = toastData::dismissed,
        )
    }
}

@Composable
private fun Toast(
    message: String,
    icon: ImageVector?,
    backgroundColor: Color,
    iconColor: Color,
    textColor: Color,
    animateDuration: Int? = 0,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onDismissed: () -> Unit = {},
) {
    val roundedValue = 26.dp
    val feedback = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .padding(8.dp)
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .toastGesturesDetector(onPause, onResume, onDismissed),
        color = backgroundColor,
        shape = RoundedCornerShape(roundedValue),
        tonalElevation = 2.dp,
    ) {
        val progress = remember { Animatable(0f) }
        LaunchedEffect(animateDuration) {
            // 禁用动画时不要运行动画。
            if (coroutineContext.durationScale == 0f) return@LaunchedEffect

            if (animateDuration == null) {
                progress.stop()
            } else {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = animateDuration,
                        easing = EaseOut,
                    ),
                )
                //进度条完成后，震动一下。
                feedback.vibration()
            }
        }

        val color = LocalContentColor.current
        Row(
            Modifier
                .drawBehind {
                    val fraction = progress.value * size.width
                    drawRoundRect(
                        color = color,
                        size = Size(width = fraction, height = size.height),
                        cornerRadius = CornerRadius(roundedValue.toPx()),
                        alpha = 0.1f,
                    )
                }
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    Modifier.size(24.dp),
                    tint = iconColor
                )
            }
            Title(message, color = textColor, maxLine = 6)
        }
    }
}

private fun Modifier.toastGesturesDetector(
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDismissed: () -> Unit,
): Modifier = composed {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    pointerInput(Unit) {
        val decay = splineBasedDecay<Float>(this)
        coroutineScope {
            while (true) {
                awaitPointerEventScope {
                    // Detect a touch down event.
                    val down = awaitFirstDown()
                    onPause()
                    val pointerId = down.id

                    val velocityTracker = VelocityTracker()
                    // Stop any ongoing animation.
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        offsetY.stop()
                        alpha.stop()
                    }

                    verticalDrag(pointerId) { change ->
                        onPause()
                        // Update the animation value with touch events.
                        val changeY = (offsetY.value + change.positionChange().y).coerceAtMost(0f)
                        launch {
                            offsetY.snapTo(changeY)
                        }
                        if (changeY == 0f) {
                            velocityTracker.resetTracking()
                        } else {
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        }
                    }

                    onResume()
                    // No longer receiving touch events. Prepare the animation.
                    val velocity = velocityTracker.calculateVelocity().y
                    val targetOffsetY = decay.calculateTargetValue(
                        offsetY.value,
                        velocity,
                    )
                    // The animation stops when it reaches the bounds.
                    offsetY.updateBounds(
                        lowerBound = -size.height.toFloat() * 3,
                        upperBound = size.height.toFloat(),
                    )
                    launch {
                        if (velocity >= 0 || targetOffsetY.absoluteValue <= size.height) {
                            // Not enough velocity; Slide back.
                            offsetY.animateTo(
                                targetValue = 0f,
                                initialVelocity = velocity,
                            )
                        } else {
                            // The element was swiped away.
                            launch { offsetY.animateDecay(velocity, decay) }
                            launch {
                                alpha.animateTo(targetValue = 0f, animationSpec = tween(300))
                                onDismissed()
                            }
                        }
                    }
                }
            }
        }
    }
        .offset {
            IntOffset(0, offsetY.value.roundToInt())
        }
        .alpha(alpha.value)
}
