package com.wordsfairy.note.ui.page.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.base.utils.searchInBrowser
import com.wordsfairy.note.constants.Constants.URL_PRIVACY_PROTECTION
import com.wordsfairy.note.ui.theme.AppColor
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.H4
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.AgreeButton
import com.wordsfairy.note.ui.widgets.BigTitle
import com.wordsfairy.note.ui.widgets.CancelButton
import com.wordsfairy.note.ui.widgets.ConfirmButton
import com.wordsfairy.note.ui.widgets.DisagreeButton
import com.wordsfairy.note.ui.widgets.TextContent
import com.wordsfairy.note.ui.widgets.Title

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/1 9:30
 */

private const val TAG_PRIVACY_PROTECTION = "tag_privacy_protection"

@Composable
fun AgreementUI(
    disagree: () -> Unit = {},
    agree: () -> Unit = {}
) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .background(WordsFairyTheme.colors.whiteBackground)
            .systemBarsPadding()
    ) {
        Image(
            painter = painterResource(id = AppResId.Mipmap.Logo), contentDescription = "logo",
            Modifier.align(Alignment.Center),
        )
        Column(
            Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BigTitle("欢迎")
            Spacer(Modifier.height(9.dp))
            val annotatedText = privacyText()
            ClickableText(text = annotatedText,
                style = TextStyle(textAlign = TextAlign.Center),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = TAG_PRIVACY_PROTECTION, start = offset, end = offset
                    ).firstOrNull()?.let { annotation ->
                        context.searchInBrowser(annotation.item)
                    }
                })
            Spacer(Modifier.height(19.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                /** 取消按钮 */
                DisagreeButton(Modifier.weight(1f)) {
                    disagree.invoke()
                }
                Spacer(Modifier.width(12.dp))
                /** 确认按钮 */
                AgreeButton(Modifier.weight(1f)) {
                    agree.invoke()
                }
            }
            Spacer(Modifier.height(19.dp))
        }

    }
}

@Composable
private fun privacyText() = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            color = WordsFairyTheme.colors.textPrimary,
            fontSize = 17.sp
        )
    ) {
        append("在使用词仙笔记前，您需要认真阅读")
        append("\n")
        append("并用意我们的")
    }
    pushStringAnnotation(tag = TAG_PRIVACY_PROTECTION, annotation = URL_PRIVACY_PROTECTION)
    withStyle(
        style = SpanStyle(
            color = AppColor.blue, fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )
    ) {
        append(stringResource(id = AppResId.String.PrivacyPolicy))
    }
    pop()
}

@Preview
@Composable
fun PreviewAgreementUI() {
    AgreementUI()
}