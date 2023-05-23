package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/12/28 16:11
 */
@Composable
fun CommonTextItem(
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
    clickable :Boolean = true,
    horizontalPadding : Dp = 6.dp,
    onClick: () -> Unit = {}
) {
    ImmerseCard(modifier.padding(horizontal = horizontalPadding)) {
        Column(Modifier.clickable(clickable) {
            onClick.invoke()
        }) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 18.sp, color = WordsFairyTheme.colors.textPrimary)
                Spacer(Modifier.weight(1f))
                Text(text = subtitle, fontSize = 16.sp, color = WordsFairyTheme.colors.textSecondary)
            }

        }
    }
}