package com.wordsfairy.note.ext.flow


import com.wordsfairy.common.ext.flow.NULL_VALUE
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/10/27 12:19
 */
fun <A, B, R> Flow<A>.withLatestFrom(
    other: Flow<B>,
    transform: suspend (A, B) -> R
): Flow<R> {
    return flow {
        val otherRef = AtomicRef<Any?>(null)

        try {
            coroutineScope {
                launch(start = CoroutineStart.UNDISPATCHED) {
                    other.collect { otherRef.value = it ?: NULL_VALUE }
                }

                collect { value ->
                    emit(
                        transform(
                            value,
                            NULL_VALUE.unbox(otherRef.value ?: return@collect)
                        )
                    )
                }
            }
        } finally {
            otherRef.value = null
        }
    }
}
