package com.wordsfairy.note.ui.page.home.foldermanage.widgets

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wordsfairy.note.data.entity.NoteFolderEntity
import com.wordsfairy.note.ui.common.vibration
import com.wordsfairy.note.ui.theme.AppResId
import com.wordsfairy.note.ui.widgets.ImmerseCard
import com.wordsfairy.note.ui.widgets.MyIconButton
import com.wordsfairy.note.ui.widgets.TextContent
import com.wordsfairy.note.ui.widgets.reorderable.ReorderableItem
import com.wordsfairy.note.ui.widgets.reorderable.ReorderableLazyListState
import com.wordsfairy.note.ui.widgets.reorderable.detectReorderAfterLongPress
import com.wordsfairy.note.ui.widgets.reorderable.rememberReorderableLazyListState
import com.wordsfairy.note.ui.widgets.reorderable.reorderable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.parcelize.Parcelize
import java.util.Collections

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/6/21 22:39
 */
@Composable
fun FolderList(
    folders: List<NoteFolderEntity>,
    onMove: (List<NoteFolderEntity>) -> Unit ={},
    onDelete: (NoteFolderEntity) -> Unit={},
    onModify: (NoteFolderEntity) -> Unit={},
    onCreate: () -> Unit = {}
) {
    val feedback = LocalHapticFeedback.current

    var data by remember { mutableStateOf<List<FolderItemBean>>(mutableListOf()) }
    var noteFoldersCache by remember { mutableStateOf(folders) }
    val deletedRouteList = remember { mutableStateListOf<Long>() }
    val mutex = Mutex()


    val state = rememberReorderableLazyListState(onDragStart = {
        feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }, onMove = { from, to ->
        data = data.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        CoroutineScope(Dispatchers.IO).launch {
            mutex.withLock {
                noteFoldersCache = noteFoldersCache.apply {
                    val fromIndex = from.index
                    val toIndex = to.index
                    val fromPos = this[fromIndex].position
                    val toPos = this[toIndex].position
                    this[fromIndex].position = toPos
                    this[toIndex].position = fromPos
                    Collections.swap(this, fromIndex, toIndex)
                }
            }
        }
    }, onDragEnd = { _, _ ->
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            onMove.invoke(noteFoldersCache)
        }
    })
    LaunchedEffect(folders.hashCode()) {
        val isChange = folders.size != noteFoldersCache.size
        //过滤其他数据 减少内存开销
        data = folders.map {
            FolderItemBean(it.folderId, it.name)
        }
        noteFoldersCache = folders
    }
    //列表
    LazyColumn(
        state = state.listState,
        modifier = Modifier.reorderable(state)
    ) {
        itemsIndexed(data, { _, item -> item }) { index, item ->
            ReorderableItem(state, key = item) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                //删除动画
                AnimatedVisibility(
                    visible = !deletedRouteList.contains(item.id),
                    enter = expandVertically(),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 1000,
                        )
                    )
                ) {
                    ImmerseCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .shadow(elevation.value),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ItemView(item.content, state, onModify = {
                            feedback.vibration()
                            onModify.invoke(noteFoldersCache[index])
                        }, onDelete = {
                            feedback.vibration()
                            onDelete.invoke(noteFoldersCache[index])
                        })
                    }
                }
            }
        }
        item {
            //创建文件夹
            ImmerseCard( Modifier .padding(horizontal = 66.dp, vertical = 5.dp),onClick ={
                onCreate.invoke()
                feedback.vibration()

            }){
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    TextContent(text = "创建文件夹",Modifier.align(Alignment.Center))
                }
            }
        }
    }


}

@Composable
private fun ItemView(
    folderName: String,
    state: ReorderableLazyListState,
    onModify: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(12.dp))
        TextContent(text = folderName)
        Spacer(Modifier.weight(1f))

        MyIconButton(painter = painterResource(id = AppResId.Drawable.Modify)) {
            onModify.invoke()
        }
        MyIconButton(painter = painterResource(id = AppResId.Drawable.Delete)) {
            onDelete.invoke()
        }
        MyIconButton(
            imageVector = Icons.Rounded.Menu,
            modifier = Modifier
                .detectReorderAfterLongPress(state)
        )
    }
}
@Parcelize
private data class FolderItemBean(
    var id: Long,
    var content: String
) : Parcelable