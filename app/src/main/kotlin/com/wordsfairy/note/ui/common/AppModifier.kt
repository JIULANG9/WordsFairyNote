package com.wordsfairy.note.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import com.wordsfairy.base.tools.hideKeyboard
import com.wordsfairy.note.MainActivity


/**
 * View的click方法的两次点击间隔时间
 */
const val VIEW_CLICK_INTERVAL_TIME = 800

/**
 * 防止重复点击(有的人可能会手抖连点两次,造成奇怪的bug)
 */
@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
inline fun Modifier.click(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    enabled: Boolean = true,//中间这三个是clickable自带的参数
    onClickLabel: String? = null,
    role: Role? = null,
    crossinline onClick: () -> Unit
): Modifier {
    var lastClickTime = remember { 0L }//使用remember函数记录上次点击的时间
    return clickable(enabled, onClickLabel, role) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}


/**
 * 防止重复点击,比如用在Button时直接传入onClick函数
 *   Button(onClick = composeClick {})
 */
@Composable
inline fun composeClick(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    crossinline onClick: () -> Unit
): () -> Unit {
    var lastClickTime = remember { 0L }//使用remember函数记录上次点击的时间
    return {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}


/**
 * 长按事件
 * Text(text = "", modifier = Modifier.longClick {})
 *
 *
 */
fun Modifier.longClick(onLongClick: (Offset) -> Unit): Modifier =
    pointerInput(this) {
        detectTapGestures(
            onLongPress = onLongClick
        )
    }

/**
 * 双击事件
 *
 * Modifier.doubleClick { })
 */
fun Modifier.doubleClick(onDoubleClick: (Offset) -> Unit): Modifier =
    //处理手势反馈
    pointerInput(this) {
        //处理基础手势反馈
        detectTapGestures(
            onDoubleTap = onDoubleClick//双击时回调
//            onPress = {}//按下时回调
//            onLongPress = {}//长按时回调
//            onTap = {}//轻触时回调(按下并抬起)
        )
    }


/**
 * 在有键盘的页面,设置在root compose上,点击别处可以自动隐藏键盘
 *
 * Text(text = "", modifier = Modifier.autoCloseKeyboard(getRootViewGroup()))
 */
fun Modifier.autoCloseKeyboard(activity: Activity = MainActivity.CONTEXT): Modifier =
    pointerInput(this) {
        detectTapGestures(
            onPress = {
                hideKeyboard(activity)
            }
        )
    }

//不会触发水波纹之类的效果
@Composable
fun Modifier.clickableNoIndication(focusManager: FocusManager) =
    this.clickable(
        onClick = {
            focusManager.clearFocus()
        },
        // 去除点击效果
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }
    )

/**
 * 点击时,自动隐藏键盘
 * 同时解决 输入框 换行时,输入法消失的问题
 */
@Composable
fun Modifier.onPressNoIndication(focusManager: FocusManager) =
    this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = {
            },
            onLongPress = {
            },
            onPress = {
            },
            onTap = {
                focusManager.clearFocus()
            }
        )
    }

@Composable
fun Modifier.onPressNoIndication(onTap: () -> Unit) =
    this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = {
            },
            onLongPress = {
            },
            onPress = {
            },
            onTap = {
                onTap.invoke()
            }
        )
    }

fun HapticFeedback.vibration() {
    this.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

fun HapticFeedback.vibrationLongPress() {
    this.performHapticFeedback(HapticFeedbackType.LongPress)
}