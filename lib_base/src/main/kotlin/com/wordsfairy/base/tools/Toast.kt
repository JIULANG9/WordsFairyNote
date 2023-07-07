package com.wordsfairy.base.tools

import android.content.Context
import android.widget.Toast

/**
 * @Description:
 * @Author: JIULANG
 * @Data: 2022/11/18 17:02
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.toast(text: CharSequence) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

@Suppress("NOTHING_TO_INLINE")
inline fun Context.toastLONG(text: CharSequence) =
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()