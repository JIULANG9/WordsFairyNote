package com.wordsfairy.note.ui.page.detail.wifgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsfairy.note.ui.common.click
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.widgets.Title

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/11 22:51
 */
@Composable
fun NoteContentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onClick: (Int) -> Unit,
) {
    val list = listOf(
        ContextItem(1,"",AppResId.Drawable.Search),
        ContextItem(2,"复制",AppResId.Drawable.Copy),
        ContextItem(3,"转发",AppResId.Drawable.Forward),
        ContextItem(4,"删除",AppResId.Drawable.Delete),
        ContextItem(5,"修改",AppResId.Drawable.Modify))

    DropdownMenu(
        expanded = expanded,
        modifier = Modifier.background(WordsFairyTheme.colors.dialogBackground),
        offset = DpOffset(9.dp, 0.dp),
        onDismissRequest = onDismiss,
    ) {
        Row(Modifier.padding(1.dp),verticalAlignment = Alignment.CenterVertically) {
            list.forEachIndexed { index, item ->
                val haveName = item.name.isNotEmpty()

                Column(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .click {
                            onDismiss.invoke()
                            onClick.invoke(item.id)
                        }
                        .wrapContentWidth()
                        .padding(horizontal = if (haveName) 16.dp else 9.dp)
                        .background(WordsFairyTheme.colors.dialogBackground),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.name,
                        Modifier.size(if (haveName) 19.dp else 26.dp),
                        colorFilter= ColorFilter.tint(color = WordsFairyTheme.colors.iconBlack)
                    )
                    if (haveName){
                        Spacer(Modifier.height(2.dp))
                        Title(item.name, color = WordsFairyTheme.colors.textPrimary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}


private data class ContextItem(val id: Int,val name: String,val icon:Int)