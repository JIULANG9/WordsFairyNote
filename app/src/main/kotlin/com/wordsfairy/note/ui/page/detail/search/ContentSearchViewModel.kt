package com.wordsfairy.note.ui.page.detail.search


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.data.room.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/13 17:00
 */

@HiltViewModel
class ContentSearchViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
) : BaseViewModel<ViewIntent, ViewState, SingleEvent>() {

    override val viewStateFlow: StateFlow<ViewState>


    init {
        val initialVS = ViewState.initial()

        viewStateFlow = intentFlow.toPartialChangeFlow().sendSingleEvent()
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

        }
    }

    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> =
        run {

            return merge(

            )
        }
}