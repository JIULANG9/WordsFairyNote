package com.wordsfairy.note.ext.flow

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.input.key.Key.Companion.Symbol
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/10/4 17:24
 */
public fun <T> flowFromSuspend(function: suspend () -> T): Flow<T> = flow { emit(function()) }


infix fun <T> Flow<T>.noteStartWith(item: T): Flow<T> = concat(flowOf(item), this)


fun <T> concat(flow1: Flow<T>, flow2: Flow<T>): Flow<T> = flow {
    emitAll(flow1)
    emitAll(flow2)
}



suspend inline fun <T, R> Flow<T>.flatMapFirst(crossinline transform: suspend (value: T) -> Flow<R>): Flow<R> =
    flatMapConcat { value ->
        transform(value).take(1)
    }
