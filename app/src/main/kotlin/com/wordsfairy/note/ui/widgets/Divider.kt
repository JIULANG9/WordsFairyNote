package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/**
 * @Description: 分割线
 * @Author: JIULANG
 * @Data: 2023/6/21 10:05
 */

/**
 * 分割线
 */
@Composable
fun ItemDivider(hPadding: Dp = 20.dp) {
    HorizontalDivider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = hPadding),
        thickness = 0.3.dp,
        color = WordsFairyTheme.colors.textSecondary
    )
}