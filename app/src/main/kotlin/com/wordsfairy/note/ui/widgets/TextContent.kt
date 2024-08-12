package com.wordsfairy.note.ui.widgets

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.material.placeholder
import com.wordsfairy.note.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/11/12 0:29
 */


@Composable
fun TextNoteContent(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = WordsFairyTheme.colors.textPrimary,
    textAlign: TextAlign = TextAlign.Start,
    isFold: Boolean = false,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {

//    SelectionContainer {
//        Text(
//            text = text,
//            modifier = modifier,
//            fontSize = 17.sp,
//            color = color,
//            textAlign = textAlign
//        )
//    }
    Text(
        text = text,
        modifier = modifier,
        fontSize = 17.sp,
        color = color,
        textAlign = textAlign,
        onTextLayout = onTextLayout,
        maxLines = if (isFold) 6 else Int.MAX_VALUE, // 限制最多显示3行
        overflow = TextOverflow.Ellipsis // 当文本超出时显示省
    )
}

@Composable
fun TextContent(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = WordsFairyTheme.colors.textSecondary,
    maxLines: Int = 99,
    textAlign: TextAlign = TextAlign.Start,
    canCopy: Boolean = false,
    isLoading: Boolean = false
) {
    if (canCopy) {
        SelectionContainer {
            Title(
                title = text,
                modifier = modifier,
                fontSize = H6,
                color = color,
                maxLine = maxLines,
                textAlign = textAlign,
                isLoading = isLoading
            )
        }
    } else {
        Title(
            title = text,
            modifier = modifier,
            fontSize = H5,
            color = color,
            maxLine = maxLines,
            textAlign = textAlign,
            isLoading = isLoading
        )
    }
}

@Composable
fun MiniText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = WordsFairyTheme.colors.textSecondary,
    maxLines: Int = 1,
    textAlign: TextAlign = TextAlign.Start,
    isLoading: Boolean = false
) {
    Title(
        title = text,
        modifier = modifier,
        fontSize = H7,
        color = color,
        maxLine = maxLines,
        textAlign = textAlign,
        isLoading = isLoading,
    )
}

@Composable
fun BigTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Title(
        title = title,
        modifier = modifier,
        fontSize = H3,
        color = WordsFairyTheme.colors.textPrimary
    )
}

@Composable
fun TextSecondary(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = WordsFairyTheme.colors.textSecondary,
    maxLines: Int = 1,
    textAlign: TextAlign = TextAlign.Start,
    isLoading: Boolean = false
) {
    Text(
        text = text,
        modifier = modifier.placeholder(
            visible = isLoading,

            ),
        fontSize = 15.sp,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign
    )
}

@Composable
fun MiniTimeText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = H8,
    color: Color = WordsFairyTheme.colors.textSecondary,
) {
    Text(
        text = text,
        modifier = modifier,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize,
        color = color,
        maxLines = 1,
    )
}

@Composable
fun AnnotatedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = H6,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal,
    canCopy: Boolean = false,
    isLoading: Boolean = false
) {
    if (canCopy) {
        SelectionContainer {
            Text(
                text = text,
                modifier = modifier
                    .placeholder(
                        visible = isLoading,
                        color = WordsFairyTheme.colors.whiteBackground
                    ),
                fontWeight = fontWeight,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign
            )
        }
    } else {
        Text(
            text = text,
            modifier = modifier
                .placeholder(
                    visible = isLoading,
                    color = WordsFairyTheme.colors.whiteBackground
                ),
            fontWeight = fontWeight,
            fontSize = fontSize,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign
        )
    }
}

@Composable
fun Title(
    title: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    color: Color = WordsFairyTheme.colors.textPrimary,
    fontWeight: FontWeight = FontWeight.Bold,
    maxLine: Int = 1,
    textAlign: TextAlign = TextAlign.Start,
    isLoading: Boolean = false
) {
    Text(
        text = title,
        modifier = modifier
            .placeholder(
                visible = isLoading,
                color = WordsFairyTheme.colors.placeholder
            ),
        fontWeight = fontWeight,
        fontSize = fontSize,
        color = color,
        maxLines = maxLine,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign
    )
}

@Composable
fun HighlightedText(
    content: String,
    highlighted: String,
) {
    // 创建一个变量来存储最终的 AnnotatedString
    val annotatedText = remember { mutableStateOf(AnnotatedString(content)) }
    val textColor = WordsFairyTheme.colors.textSecondary
    val highlightColor = AppColor.themeColor
    // 在协程中异步处理高亮逻辑
    LaunchedEffect(Unit) {
        val splitContent = content.split(highlighted, ignoreCase = true)
        // 构建 AnnotatedString
        val newAnnotatedText = withContext(Dispatchers.IO) {
            buildAnnotatedString {
                for (i in splitContent.indices) {
                    withStyle(style = SpanStyle(color = textColor)) {
                        append(splitContent[i])
                    }
                    if (i != splitContent.size - 1) {
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append(highlighted)
                        }
                    }
                }
            }
        }
        // 更新 AnnotatedString
        annotatedText.value = newAnnotatedText

    }
    // 显示文本
    Text(
        text = annotatedText.value,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}