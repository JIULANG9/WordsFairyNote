package com.wordsfairy.note.ui.widgets.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalHapticFeedback
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.common.vibration
import kotlin.coroutines.resume
import kotlin.math.roundToLong
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.sql.Time
import kotlin.coroutines.CoroutineContext



fun ToastModel.showToast() {
    postEventValue(
        EventBus.ShowToast,
        this
    )
}


@Stable
class ToastUIState {
    private val mutex = Mutex()

    public var currentData: ToastData? by mutableStateOf(null)
        private set

    public suspend fun show(
        message: String,
        icon: ImageVector? = null,
    ): Unit = mutex.withLock {
        try {
            return suspendCancellableCoroutine { cont ->
                currentData = ToastDataImpl(
                    message,
                    icon,
                    cont,
                )
            }
        } finally {
            currentData = null
        }
    }

    public suspend fun show(
        toastModel: ToastModel
    ): Unit = mutex.withLock {
        try {
            return suspendCancellableCoroutine { cont ->
                currentData = ToastDataImpl(
                    toastModel.message,
                    null,
                    cont,
                    toastModel.type,
                    toastModel.durationTime
                )
            }
        } finally {
            currentData = null
        }
    }


    @Stable
    private class ToastDataImpl(
        override val message: String,
        override val icon: ImageVector?,
        private val continuation: CancellableContinuation<Unit>,
        override val type: ToastModel.Type? = ToastModel.Type.Normal,
        val durationTime: Long? = null
    ) : ToastData {
        private var elapsed = 0L
        private var started = 0L
        private var duration = 0L
        private val _state = MutableStateFlow<Int?>(null)
        override val animationDuration: StateFlow<Int?> = _state.asStateFlow()

        val mDurationTime = durationTime ?: when (type) {
            ToastModel.Type.Normal -> ToastDurationTime
            ToastModel.Type.Success -> ToastDurationTime
            ToastModel.Type.Warning -> 3000
            ToastModel.Type.Error -> 5000
            else -> ToastDurationTime
        }

        override suspend fun run(accessibilityManager: AccessibilityManager?) {
            duration = durationTimeout(
                hasIcon = icon != null,
                accessibilityManager = accessibilityManager,
                timeout = mDurationTime
            )
            //无障碍决定永远显示
            //让我们等待明确的解雇，不要运行动画。
            if (duration == Long.MAX_VALUE) {
                delay(duration)
                return
            }

            resume()
            supervisorScope {
                launch {
                    animationDuration.collectLatest { duration ->
                        val animationScale = coroutineContext.durationScale
                        if (duration != null) {
                            started = System.currentTimeMillis()
                            // 关闭动画后，只需显示、等待和隐藏即可。
                            val finalDuration = when (animationScale) {
                                0f -> duration.toLong()
                                else -> (duration.toLong() * animationScale).roundToLong()
                            }
                            delay(finalDuration)
                            this@launch.cancel()
                        } else {
                            elapsed += System.currentTimeMillis() - started
                            delay(Long.MAX_VALUE)
                        }
                    }
                }
            }
        }

        override fun pause() {
            _state.value = null
        }

        override fun resume() {
            val remains = (duration - elapsed).toInt()
            if (remains > 0) {
                _state.value = remains
            } else {
                dismiss()
            }
        }

        override fun dismiss() {
            _state.value = 0
        }

        override fun dismissed() {
            if (continuation.isActive) {
                continuation.resume(Unit)
            }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
public fun ToastUI(
    hostState: ToastUIState,
    modifier: Modifier = Modifier,
    toast: @Composable (ToastData) -> Unit = { Toast(it) },
) {
    val accessibilityManager = LocalAccessibilityManager.current
    val currentData = hostState.currentData ?: return

    key(currentData) {

        var state by remember { mutableStateOf(false) }
        val transition = updateTransition(targetState = state, label = "toast")

        LaunchedEffect(Unit) {
            state = true
            currentData.run(accessibilityManager)
            state = false
        }

        transition.AnimatedVisibility(
            visible = { it },
            modifier = modifier,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            toast(currentData)
        }

        /**
         * 等待完全取消动画和Toast。
         * 此动画解决方法不是使toast数据无效，而是为了防止
         * 当解除动画尚未完成时，重新启动另一个Toast。
         */
        LaunchedEffect(state, transition.currentState, transition.isRunning) {
            if (!state && !transition.currentState && !transition.isRunning) {
                currentData.dismissed()
            }
        }
    }
}

const val ToastDurationTime = 1666L
internal fun durationTimeout(
    hasIcon: Boolean,
    accessibilityManager: AccessibilityManager?,
    timeout: Long = ToastDurationTime
): Long {
    if (accessibilityManager == null) return timeout
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = timeout,
        containsIcons = hasIcon,
        containsText = true,
        containsControls = false,
    )
}

internal val CoroutineContext.durationScale: Float
    get() {
        val scale = this[MotionDurationScale]?.scaleFactor ?: 1f
        check(scale >= 0f)
        return scale
    }