package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun ItemDivider() {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = WordsFairyTheme.colors.textSecondary,
        thickness = 0.3.dp
    )
}