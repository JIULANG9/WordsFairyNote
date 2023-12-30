package com.wordsfairy.note.ui.page.set

import androidx.compose.runtime.Composable

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wordsfairy.note.base.BaseViewModel
import com.wordsfairy.note.constants.EventBus
import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.data.room.repository.NoteRepository
import com.wordsfairy.note.ext.flowbus.postEventValue
import com.wordsfairy.note.ui.theme.FollowSystemLiveData
import com.wordsfairy.note.ui.theme.WordsFairyTheme
import com.wordsfairy.note.ui.theme.WordsFairyThemeLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/16 0:00
 */


@HiltViewModel
class SetViewModel @Inject internal constructor(
    private val noteRepository: NoteRepository,
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
            /**切换主题*/
            val switchThemeFlow = filterIsInstance<ViewIntent.SwitchTheme>()
                .log("[切换主题]")
                .map {
                    val isDark = it.isDark
                    AppSystemSetManage.setDarkMode(isDark)
                    val theme = if (isDark) {
                        WordsFairyTheme.Theme.Dark
                    } else {
                        WordsFairyTheme.Theme.Light
                    }
                    WordsFairyThemeLiveData.postValue(theme)
                    PartialChange.UI.DarkUI(isDark)
                }.flowOn(Dispatchers.IO)

            val themeFollowSystemFlow = filterIsInstance<ViewIntent.ThemeFollowSystem>()
                .log("[主题跟随系统]")
                .map {
                    val isFollow = it.isFollow
                    AppSystemSetManage.followSystem(isFollow)
                    FollowSystemLiveData.postValue(isFollow)
                    PartialChange.UI.FollowSystem(isFollow)
                }.flowOn(Dispatchers.IO)

            val closeAnimation = filterIsInstance<ViewIntent.CloseAnimation>()
                .log("[关闭动画]")
                .map {
                    val isClose = it.isClose
                    AppSystemSetManage.closeAnimation = isClose
                    PartialChange.UI.CloseAnimation(isClose)
                }.flowOn(Dispatchers.IO)
            return merge(
                switchThemeFlow,
                themeFollowSystemFlow,
                closeAnimation
            )
        }
}