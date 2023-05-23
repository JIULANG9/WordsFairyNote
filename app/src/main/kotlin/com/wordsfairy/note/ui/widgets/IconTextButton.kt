package com.wordsfairy.note.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/29 14:06
 */

@Composable
fun IconTextButton(
    text: String,
    @DrawableRes id: Int,
    color: Color = WordsFairyTheme.colors.immerseBackground,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Image(
            painter = painterResource(id),
            contentDescription = text
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text, color = WordsFairyTheme.colors.textPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconTextButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,

    ) {
    AssistChip(
        onClick = onClick,
        leadingIcon = {

            Icon(
                imageVector,
                contentDescription = text,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = WordsFairyTheme.colors.icon
            )
        },
        label = {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp)
            )
        }
    )
}
