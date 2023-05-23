package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.theme.AppColor

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/14 12:30
 */

@Composable
fun NoteTag(string: String, modifier: Modifier = Modifier,
) {
    Text(
        text = string,
        modifier = modifier.background(AppColor.themeAccent, RoundedCornerShape(6.dp)).padding(2.dp),
        color = AppColor.themeColor,
        fontSize = 12.sp
    )
}
