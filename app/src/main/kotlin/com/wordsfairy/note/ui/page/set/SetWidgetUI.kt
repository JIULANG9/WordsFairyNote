package com.wordsfairy.note.ui.page.set


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.AnimaSun
import com.wordsfairy.note.ui.widgets.ImmerseCard
import com.wordsfairy.note.ui.widgets.Moon

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/12/28 18:06
 */
@Composable
fun SwitchThemeButton(
    isDarkTheme: Boolean,
    onClick: (Boolean) -> Unit,
) {

    IconButton(onClick = {
        onClick.invoke(!isDarkTheme)
    }) {
        if (isDarkTheme) {
            Moon(
                Modifier
                    .size(21.dp)
            )
        } else {
            AnimaSun(
                modifier = Modifier
                    .size(29.dp)
            )
        }
    }
}


@Composable
fun CommonItemSwitch(
    text: String,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {

    ImmerseCard(Modifier.padding(horizontal = 6.dp)) {
        Column(modifier) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = text, fontSize = 18.sp, color = WordsFairyTheme.colors.textPrimary)
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isEnabled,
                    onCheckedChange = {
                        onCheckedChange.invoke(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = WordsFairyTheme.colors.themeUi
                    )
                )
            }
        }
    }
}


/**
 * 动画图标
 * AnimateContent
 * */
@Composable
fun AnimateContentIcon(
    title: String,
    expandContent: @Composable ColumnScope.() -> Unit
) {
    var expand by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .wrapContentSize()
            .animateContentSize(
                tween(500)
            ),
    ) {
        // 定义旋转动画
        val rotationValue: Float by animateFloatAsState(if (expand) 90f else 0f)
        ImmerseCard {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        expand = !expand
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 18.sp, color = WordsFairyTheme.colors.textPrimary)
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(id = AppResId.Drawable.Arrow_Right),
                    contentDescription = "right",
                    Modifier
                        .size(21.dp)
                        .rotate(rotationValue)
                )
            }
        }

        if (expand) {
            Column(
                content = expandContent,
                horizontalAlignment = Alignment.CenterHorizontally
            )
        }
    }
}


@Composable
fun CommonItemIcon(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = WordsFairyTheme.colors.textPrimary,
    iconId: Int? = AppResId.Drawable.Arrow_Right,
    onClick: () -> Unit = {}
) {
    ImmerseCard(modifier.padding(horizontal = 6.dp), onClick = onClick) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = text, fontSize = 18.sp, color = textColor)
                Spacer(Modifier.weight(1f))
                if (iconId!=null){
                    Image(
                        painter = painterResource(id = iconId),
                        contentDescription = "right",
                        Modifier.size(21.dp),
                        colorFilter = ColorFilter.tint(color = WordsFairyTheme.colors.icon)
                    )
                }
            }
        }
    }
}
