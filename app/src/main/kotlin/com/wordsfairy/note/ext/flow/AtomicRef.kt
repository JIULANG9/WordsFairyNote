package com.wordsfairy.note.ext.flow

import java.util.concurrent.atomic.AtomicReference as JavaAtomicReference
/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/10/27 12:19
 */

internal class AtomicRef<T>  constructor(initialValue: T) {
    private val atomic = JavaAtomicReference(initialValue)

     var value: T
        get() = atomic.get()
        set(value) = atomic.set(value)

     fun compareAndSet(expect: T, update: T): Boolean = atomic.compareAndSet(expect, update)
}
