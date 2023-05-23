package com.wordsfairy.note.ui.page.set

import com.wordsfairy.note.data.AppSystemSetManage
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewState

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/5/16 11:20
 */
sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    data class SwitchTheme(val isDark:Boolean) : ViewIntent
    data class ThemeFollowSystem(val isFollow:Boolean) : ViewIntent

}

data class ViewState(
    val darkUI: Boolean,
    val themeFollowSystem: Boolean
) : MviViewState {
    companion object {
        fun initial() = ViewState(
            darkUI = AppSystemSetManage.darkUI,
            themeFollowSystem = AppSystemSetManage.darkModeFollowSystem
        )
    }
}

sealed interface SingleEvent : MviSingleEvent {
    sealed interface UI : SingleEvent {
        object Success : UI

    }
}

internal sealed interface PartialChange {
    fun reduce(vs: ViewState): ViewState

    sealed class UI : PartialChange {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Success -> vs.copy()
                is DarkUI -> vs.copy(darkUI = darkUI)
                is FollowSystem -> vs.copy(themeFollowSystem = follow)
            }
        }
        data class DarkUI(val darkUI:Boolean) : UI()
        data class FollowSystem(val follow:Boolean,) : UI()
        object Success : UI()
    }
}