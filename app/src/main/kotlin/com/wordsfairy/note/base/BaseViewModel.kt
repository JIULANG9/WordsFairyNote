package com.wordsfairy.note.base

import com.wordsfairy.note.mvi.*

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/7/29 17:36
 */
abstract class BaseViewModel<I : MviIntent, S : MviViewState, E : MviSingleEvent> : AbstractMviViewModel<I, S, E>() {

}