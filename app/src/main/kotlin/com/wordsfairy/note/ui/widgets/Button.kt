package com.wordsfairy.note.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.common.composeClick
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/29 0:24
 */
@Composable
fun MyButton(name: String,
             color: Color = WordsFairyTheme.colors.themeUi,
             enabled:Boolean = true,
             onClick: () -> Unit ) {
    Button(
        modifier = Modifier.height(31.dp), onClick = composeClick {
            onClick.invoke()
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(color)
    ) {
        Text(text = name, fontSize = 12.sp, color = WordsFairyTheme.colors.textWhite)
    }
}

@Composable
fun RoundedCornerButton(
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = WordsFairyTheme.colors.primaryBtnBg,
    textColor:Color = WordsFairyTheme.colors.textPrimary,
    onClick: () -> Unit,
    ){
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

