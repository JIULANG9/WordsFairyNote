package com.wordsfairy.common.ext.flow

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/10/27 12:16
 */

@JvmField
 val NULL_VALUE: Symbol = Symbol("NULL_VALUE")



 class Symbol(@JvmField val symbol: String) {
    override fun toString(): String = "<$symbol>"

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    public inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}
