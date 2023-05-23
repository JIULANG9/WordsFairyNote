package com.wordsfairy.note.ui.page.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.data.room.repository.NoteFolderRepository
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.ui.page.create.CreateNoteState
import com.wordsfairy.note.ui.page.detail.NoteDetailState
import com.wordsfairy.note.ui.page.search.SearchUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/27 17:29
 */
@HiltViewModel
class HomeViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: NoteFolderRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<HomeViewIntent, HomeViewState, HomeSingleEvent>() {
    var fullSize by mutableStateOf(IntSize(0, 0))

    /** 笔记搜索 */
    var searchCaedSize by mutableStateOf(IntSize(0, 0))
    var searchUIOffset by mutableStateOf(IntOffset(0, 0))
    var searchUIState by mutableStateOf(SearchUIState.Closed)

    /** 创建笔记 */
    var cardSize by mutableStateOf(IntSize(0, 0))
    var createNoteUIOffset by mutableStateOf(IntOffset(0, 0))
    var currentCreateNoteState by mutableStateOf(CreateNoteState.Closed)

    /** 笔记详细 */
    var noteDetailUISize by mutableStateOf(IntSize(0, 0))
    var createNoteDetailUIOffset by mutableStateOf(IntOffset(0, 0))
    var currentNoteDetailsState by mutableStateOf(NoteDetailState.Closed)

    override val viewStateFlow: StateFlow<HomeViewState>

    // 查询笔记信息并只显示最新5条笔记内容
    val noteInfoList = noteRepository.getHomeNoteInfo()

    init {
        val initialVS = savedStateHandle.get<HomeViewState?>(VIEW_STATE)?.copy() ?: HomeViewState.initial()
      //  val initialVS = HomeViewState.initial()

        viewStateFlow = merge(
            intentFlow.filterIsInstance<HomeViewIntent.Initial>().take(1),
            intentFlow.filterNot { it is HomeViewIntent.Initial }
        ).toPartialChangeFlow().sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .onEach { savedStateHandle[VIEW_STATE] = it }
            .catch {
                Log.e(logTag, "[HomeViewModel] Throwable:", it)
            }
            .stateIn(
                viewModelScope, SharingStarted.Eagerly, initialVS
            )
    }

    private fun Flow<HomePartialChange>.sendSingleEvent(): Flow<HomePartialChange> {
        return onEach { change ->
            val event = when (change) {
                is HomePartialChange.UI.Close -> HomeSingleEvent.Refresh.Success

                else -> return@onEach
            }
            sendEvent(event)
        }
    }

    private fun Flow<HomeViewIntent>.toPartialChangeFlow(): Flow<HomePartialChange> =
        shareWhileSubscribed().run {
            /** 初始化 */
            val initFlow = filterIsInstance<HomeViewIntent.Initial>()
                .log("[Intent]")
                .map {
                    HomePartialChange.UI.Init
                }
            val noteFolders = filterIsInstance<HomeViewIntent.GetNoteFolder>()
                .log("GetNoteFolder")
                .flowOn(Dispatchers.IO)
                .map {
                    val data = folderRepository.getNoteFolder()
                    HomePartialChange.NoteData.NoteFolders(data)
                }

            /**
             * 当前笔记文件夹
             */
            val currentNoteFolderFlow = filterIsInstance<HomeViewIntent.CurrentNoteFolder>()
                .log("[当前笔记文件夹]")
                .map {
                    HomePartialChange.NoteData.NoteFolderData(it.folderEntity)
                }

            /**
             * 笔记详细
             */
            val openNoteEntityFlow = filterIsInstance<HomeViewIntent.OpenNoteEntity>()
                .log("[OpenNoteEntity]")
                .map {
                    HomePartialChange.NoteData.NoteEntityData(it.noteEntity)
                }

            return merge(
                initFlow,
                currentNoteFolderFlow,
                openNoteEntityFlow,
                noteFolders
            )
        }

    private companion object {
        private const val VIEW_STATE = "com.wordsfairy.note.ui.page.home.HomeViewModel"
    }
}