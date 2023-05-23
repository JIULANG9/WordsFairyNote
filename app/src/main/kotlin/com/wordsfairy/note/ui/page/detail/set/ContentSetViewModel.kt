package com.wordsfairy.note.ui.page.detail.set

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:02
 */

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.room.repository.NoteContentRepository
import com.wordsfairy.note.data.room.repository.NoteEntityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:00
 */

@HiltViewModel
class ContentSetViewModel @Inject internal constructor(
    private val noteRepository: NoteEntityRepository,
    private val contentRepository: NoteContentRepository
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>


    init {
        val initialVS = ViewState.initial()

        viewStateFlow = merge(
            intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is ViewIntent.Initial }

        )
            .toPartialChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it) }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )

    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->
            val event = when (change) {
                is PartialChange.UI.ShowDialog -> SingleEvent.UI.ShowDialog
                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        shareWhileSubscribed().run {
            /** 初始化 */
            val initFlow = filterIsInstance<ViewIntent.Initial>()
                .log("[Intent]")
                .map {
                  PartialChange.UI.Init
                }.distinctUntilChanged()
            val searchEnginesFlow = filterIsInstance<ViewIntent.SearchEngines>()
                .log("[SearchEngines]")
                .map {
                    val url =it.url
                    AppSystemSetManage.searchEngines =url
                    PartialChange.UI.SetSearchEngines(url)
                }.distinctUntilChanged()

            /** 复制之后转跳微信 */
            val jumpToWeChatFlow = filterIsInstance<ViewIntent.CopyJumpToWeChat>()
                .log("[SearchEngines]")
                .map {
                    val jump =it.jump
                    AppSystemSetManage.jumpToWeChat =jump
                    PartialChange.UI.CopyJumpToWeChat(jump)
                }.distinctUntilChanged()

            /** 回收笔记实体 */
            val recycleNoteFlow = filterIsInstance<ViewIntent.RecycleNote>()
                .log("[回收笔记实体]")
                .map {
                    val note = GlobalData.noteDetailsNoteEntity!!
                    noteRepository.recycle(note)
                    PartialChange.NoteData.RecycleNote
                }.flowOn(Dispatchers.IO).distinctUntilChanged()

            /** 回收笔记实体 */
            val recycleNoteContentsFlow = filterIsInstance<ViewIntent.RecycleNoteContents>()
                .log("[回收笔记实体]")
                .map {
                    val note = GlobalData.noteDetailsNoteEntity!!
                    contentRepository.recycleAll(note.noteId)
                    PartialChange.NoteData.RecycleNote
                }.flowOn(Dispatchers.IO).distinctUntilChanged()
            /** 显示弹窗 */
            val showDialogFlow = filterIsInstance<ViewIntent.ShowDialog>()
                .log("[显示弹窗]")
                .map {
                    PartialChange.UI.ShowDialog(it.dialogDataBean)
                }

            return merge(
                initFlow,searchEnginesFlow,
                jumpToWeChatFlow,
                recycleNoteFlow,
                recycleNoteContentsFlow,
                showDialogFlow
            )
        }

     companion object {
         const val RecycleNoteTag = 100
         const val RecycleNoteContentsTag = 101
         const val IsNotUTF8Tag = 103
    }
}