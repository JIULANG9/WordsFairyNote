package com.wordsfairy.note.ui.page.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.GlobalData
import com.wordsfairy.note.data.entity.SearchNoteEntity
import com.wordsfairy.note.data.room.repository.NoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 14:00
 */


@HiltViewModel
class SearchViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle,

    ) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>

    private val resultDataCache: MutableList<SearchNoteEntity> = mutableListOf()

    init {
        val initialVS = savedStateHandle.get<ViewState?>(VIEW_STATE)?.copy() ?: ViewState.initial()

        viewStateFlow = intentFlow
            .toPartialChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .onEach { savedStateHandle[VIEW_STATE] = it }
            .catch {
                Log.e(logTag, "[CreateNoteViewModel] Throwable:", it)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )

    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach { change ->

        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        run {
            /** 初始化 */
            val initFlow = filterIsInstance<ViewIntent.Initial>()
                .log("[Intent]")
                .map {
                    PartialChange.UI.Init
                }

            /**
             * 初始化所有数据
             */
            val getAllDataFlow = filterIsInstance<ViewIntent.InitAllDataFlow>()
                .log("[初始化所有数据]")
                .map {
                    val data = viewStateFlow.value.resultData.ifEmpty {
                        val allData = noteRepository.getSearchUIData()
                        resultDataCache.clear()
                        resultDataCache.addAll(allData)
                        allData
                    }
                    PartialChange.NoteData.InitData(data)
                }.flowOn(Dispatchers.IO)

            /**
             * 模糊搜索
             */
            val searchKeywordFlow = filterIsInstance<ViewIntent.SearchKeyword>()
                .log("[模糊搜索]")
                .map {
                    val keyword = it.keyword
                    val resultData = if (keyword.isNotEmpty()) {
                        GlobalData.searchContent = keyword
                        noteRepository.searchNotes(keyword)
                    } else {
                        resultDataCache
                    }
                    PartialChange.NoteData.SearchResultData(keyword, resultData)
                }

            return merge(
                initFlow,
                getAllDataFlow,
                searchKeywordFlow
            )
        }

    private companion object {
        private const val VIEW_STATE = "SearchViewModel"
    }
}