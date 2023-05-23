package com.wordsfairy.note.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import com.wordsfairy.note.mvi.MviIntent
import com.wordsfairy.note.mvi.MviSingleEvent
import com.wordsfairy.note.mvi.MviViewModel
import com.wordsfairy.note.mvi.MviViewState

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2023/4/22 17:52
 */
abstract class BaseComponentActivity<
        I : MviIntent,
        S : MviViewState,
        E : MviSingleEvent,
        VM : MviViewModel<I, S, E>
        > : ComponentActivity() ,MviCompose{

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            //结束activity
            finish()
            return
        }
        onCreateBefore()
        super.onCreate(savedInstanceState)
    }

    protected open fun onCreateBefore(){}
}

interface MviCompose {
    fun initViews(savedInstanceState: Bundle?)
}
