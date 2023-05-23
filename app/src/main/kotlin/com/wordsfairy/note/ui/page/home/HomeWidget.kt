package com.wordsfairy.note.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.constants.NavigateRouter
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.widgets.ImmerseCard
import com.wordsfairy.note.ui.widgets.MyIconButton
import com.wordsfairy.note.ui.widgets.TextContent

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 13:49
 */
@Composable
fun HomeSearchView(
    modifier: Modifier = Modifier,
    onSizedChanged: (IntSize) -> Unit,
    onClick: (offset: IntOffset) -> Unit,
) {
    var intOffset: IntOffset? by remember { mutableStateOf(null) }
    ImmerseCard(
        modifier.padding(top = 2.dp),
        onClick = {
            onClick(intOffset!!)
        },
        RoundedCornerShape(26.dp)

    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { onSizedChanged(it) }
                .onGloballyPositioned {
                    val offset = it.localToRoot(Offset(0f, 0f))
                    intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            MyIconButton(painter = painterResource(id = AppResId.Drawable.Search), size = 24.dp){
                onClick(intOffset!!)
            }
            Spacer(Modifier.width(16.dp))
            TextContent(text = "词语仙境")
        }
    }
}