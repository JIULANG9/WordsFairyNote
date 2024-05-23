package com.wordsfairy.note.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.common.composeClick

import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/29 0:24
 */
@Composable
fun SmallButton(
    name: String,
    color: Color = WordsFairyTheme.colors.themeUi,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    AssistChip(onClick = { onClick.invoke() },
        colors = AssistChipDefaults.assistChipColors(color),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        border = AssistChipDefaults.assistChipBorder(
            enabled,
            borderColor = color,
            disabledBorderColor = color
        ),
        label = {
            Text(text = name, fontSize = 12.sp, color = if(enabled) WordsFairyTheme.colors.textWhite else WordsFairyTheme.colors.textPrimary)
        })
}

@Composable
fun ButtonPrimitive(
    name: String,
    primitiveColor: Color = WordsFairyTheme.colors.themeUi,
    isAccent: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonColors = if (isAccent) {
        primitiveColor.copy(alpha = 0.2f)
    } else {
        primitiveColor
    }
    val textColor = if (isAccent) {
        primitiveColor
    } else {
        WordsFairyTheme.colors.textWhite
    }
    Button(
        modifier = modifier, onClick = composeClick {
            onClick.invoke()
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(buttonColors)
    ) {
        Text(text = name, fontSize = 16.sp, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RoundedCornerButton(
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = WordsFairyTheme.colors.primaryBtnBg,
    textColor: Color = WordsFairyTheme.colors.textPrimary,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        modifier = modifier.padding(vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = stringResource(id = textId),
            fontSize = 16.sp,
            modifier = Modifier.padding(6.dp),
            color = textColor
        )
    }
}


/** 取消按钮 */
@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    RoundedCornerButton(
        AppResId.String.Cancel,
        modifier,
        containerColor = WordsFairyTheme.colors.immerseBackground,
        textColor = WordsFairyTheme.colors.textPrimary,
        onClick = onClick
    )
}

/** 确认按钮 */
@Composable
fun ConfirmButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    RoundedCornerButton(
        AppResId.String.Confirm,
        modifier,
        containerColor = WordsFairyTheme.colors.themeUi,
        textColor = WordsFairyTheme.colors.textWhite,
        onClick = onClick
    )
}

/** 不同意按钮 */
@Composable
fun DisagreeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    RoundedCornerButton(
        AppResId.String.Disagree,
        modifier,
        containerColor = WordsFairyTheme.colors.immerseBackground,
        textColor = WordsFairyTheme.colors.textPrimary,
        onClick = onClick
    )
}

/** 同意按钮 */
@Composable
fun AgreeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    RoundedCornerButton(
        AppResId.String.Agree,
        modifier,
        containerColor = AppColor.blue,
        textColor = WordsFairyTheme.colors.textWhite,
        onClick = onClick
    )
}

